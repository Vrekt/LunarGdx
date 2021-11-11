package gdx.lunar.server.world;

import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.SPacketPlayerPosition;
import gdx.lunar.protocol.packet.server.SPacketPlayerVelocity;
import gdx.lunar.protocol.packet.server.SPacketRemovePlayer;
import gdx.lunar.server.game.entity.LunarEntity;
import gdx.lunar.server.game.entity.player.LunarPlayer;
import gdx.lunar.server.game.utilities.Disposable;
import gdx.lunar.server.world.config.WorldConfiguration;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a separate world or instance with players.
 */
public abstract class World implements Disposable {

    protected final ConcurrentMap<Integer, LunarPlayer> players = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Integer, LunarEntity> entities = new ConcurrentHashMap<>();

    protected final WorldConfiguration configuration;
    protected final String worldName;

    /**
     * Queued packet updates
     */
    protected final Queue<QueuedPacket> queuedPackets = new ConcurrentLinkedQueue<>();

    /**
     * Initialize a new world.
     *
     * @param worldName     the name of this world.
     * @param configuration the configuration to use
     */
    public World(String worldName, WorldConfiguration configuration) {
        this.worldName = worldName;
        this.configuration = configuration;
    }

    /**
     * @return the name of this world.
     */
    public String getName() {
        return worldName;
    }

    public ConcurrentMap<Integer, LunarEntity> getEntities() {
        return entities;
    }

    public ConcurrentMap<Integer, LunarPlayer> getPlayers() {
        return players;
    }

    public <T extends LunarEntity> T getEntity(int id) {
        return (T) entities.get(id);
    }

    public <T extends LunarPlayer> T getPlayer(int id) {
        return (T) entities.get(id);
    }

    /**
     * @param player the player
     * @return {@code true} if this player is timed out based on {@code playerTimeoutMs}
     */
    protected boolean isTimedOut(LunarPlayer player) {
        return System.currentTimeMillis() - player.getConnection().getLastPacketReceived() >= configuration.getPlayerTimeoutMs();
    }

    /**
     * Remove the player from worlds and disconnect the connection
     *
     * @param player the player
     */
    protected void timeoutPlayer(LunarPlayer player) {
        player.kickPlayer("Timed out.");

        removePlayerInWorld(player);
        player.getServer().handlePlayerDisconnect(player);
    }

    /**
     * Assign an entity ID within this world.
     *
     * @return the new entity ID.
     */
    public int assignEntityId() {
        return players.size() + 1 + ThreadLocalRandom.current().nextInt(111, 999);
    }

    /**
     * @return {@code true} if this world is full.
     */
    public boolean isWorldFull() {
        return players.size() >= configuration.getCapacity();
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     * @param x      X
     * @param y      Y
     */
    public void spawnPlayerInWorld(LunarPlayer player, float x, float y) {
        player.setWorldIn(this);
        player.setPosition(x, y);

        // send all current players in this world to the connecting player.
        for (LunarPlayer other : players.values()) other.sendPlayerToOtherPlayer(player);

        // broadcast the joining of this player to others.
        // TODO: broadcast(send())

        // add this new player to the list
        players.put(player.getEntityId(), player);
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     */
    public void spawnPlayerInWorld(LunarPlayer player) {
        spawnPlayerInWorld(player, 0, 0);
    }

    /**
     * Remove the player in this world
     *
     * @param player the player
     */
    public void removePlayerInWorld(LunarPlayer player) {
        if (!players.containsKey(player.getEntityId())) return;
        players.remove(player.getEntityId());

        broadcastPacketImmediately(player.getEntityId(), new SPacketRemovePlayer(player.getEntityId()));
    }

    /**
     * Handle velocity updates from players
     *
     * @param player    the player
     * @param velocityX X vel
     * @param velocityY Y vel
     * @param rotation  rotation
     */
    public void handlePlayerVelocity(LunarPlayer player, float velocityX, float velocityY, int rotation) {
        queuedPackets.add(new QueuedPlayerPacket(player.getEntityId(),
                new SPacketPlayerVelocity(player.getConnection().alloc(), player.getEntityId(), velocityX, velocityY, rotation)));
    }

    /**
     * Handle position updates from players
     *
     * @param player   the player
     * @param x        x
     * @param y        y
     * @param rotation rotation
     */
    public void handlePlayerPosition(LunarPlayer player, float x, float y, int rotation) {
        player.setLocation(x, y);

        queuedPackets.add(new QueuedPlayerPacket(player.getEntityId(), new SPacketPlayerPosition(player.getConnection().alloc(), player.getEntityId(), rotation, x, y)));
    }

    /**
     * Tick this world.
     */
    public abstract void tick();

    /**
     * Broadcast a packet in this world
     *
     * @param packet the packet
     */
    public void broadcastPacketInWorld(Packet packet) {
        for (LunarPlayer player : players.values()) {
            player.getConnection().queue(packet);
        }
    }

    /**
     * Broadcast a packet and send it to all immediately.
     *
     * @param excluded the excluded ID
     * @param packet   the packet
     */
    public void broadcastPacketImmediately(int excluded, Packet packet) {
        for (LunarPlayer player : players.values())
            if (player.getEntityId() != excluded) player.getConnection().sendImmediately(packet);
    }

    /**
     * Broadcast a packet to all players
     *
     * @param entityIdExcluded the entity ID to exclude
     * @param direct           the direct
     */
    public void broadcast(int entityIdExcluded, Packet direct) {
        for (LunarPlayer value : players.values()) {
            if (value.getEntityId() != entityIdExcluded) {
                value.getConnection().queue(direct);
            }
        }
    }

    @Override
    public void dispose() {
        players.clear();
        queuedPackets.clear();
    }

    /**
     * Represents a packet that is queued waiting to be sent.
     */
    protected static abstract class QueuedPacket {

        /**
         * The entity id who is sending the packet.
         */
        public int entityId;

        /**
         * Contents of the packet.
         */
        public Packet packet;

        public QueuedPacket(int entityId, Packet packet) {
            this.entityId = entityId;
            this.packet = packet;
        }
    }

    /**
     * Default implementation of {@link QueuedPacket}
     */
    protected static final class QueuedPlayerPacket extends QueuedPacket {
        public QueuedPlayerPacket(int entityId, Packet packet) {
            super(entityId, packet);
        }
    }

}
