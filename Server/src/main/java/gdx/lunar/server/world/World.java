package gdx.lunar.server.world;

import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketPlayerPosition;
import gdx.lunar.protocol.packet.server.SPacketPlayerVelocity;
import gdx.lunar.protocol.packet.server.SPacketRemovePlayer;
import gdx.lunar.server.game.entity.Entity;
import gdx.lunar.server.game.entity.player.Player;
import gdx.lunar.server.game.utilities.Disposable;
import io.netty.buffer.ByteBuf;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a separate world or instance with players.
 */
public abstract class World implements Disposable {

    protected final ConcurrentMap<Integer, Player> players = new ConcurrentHashMap<>();
    protected final ConcurrentMap<Integer, Entity> entities = new ConcurrentHashMap<>();

    protected final String worldName;
    protected final int maxPacketsPerTick, capacity;

    protected int maxEntities, maxEntityRequests;

    /**
     * Queued packet updates
     */
    protected final Queue<QueuedPacket> queuedPackets = new ConcurrentLinkedQueue<>();

    /**
     * Initialize a new world.
     *
     * @param worldName         the name of this world.
     * @param maxPacketsPerTick the max packets to process per tick.
     * @param capacity          max capacity.
     * @param maxEntities       the max amount of entities allowed in this world
     * @param maxEntityRequests the max amount of  requests allowed per second.
     */
    public World(String worldName, int maxPacketsPerTick, int capacity, int maxEntities, int maxEntityRequests) {
        this.worldName = worldName;
        this.maxPacketsPerTick = maxPacketsPerTick;
        this.capacity = capacity;
        this.maxEntities = maxEntities;
        this.maxEntityRequests = maxEntityRequests;
    }

    /**
     * @return the name of this world.
     */
    public String getName() {
        return worldName;
    }

    public int getMaxEntities() {
        return maxEntities;
    }

    public int getMaxEntityRequests() {
        return maxEntityRequests;
    }

    public ConcurrentMap<Integer, Entity> getEntities() {
        return entities;
    }

    public ConcurrentMap<Integer, Player> getPlayers() {
        return players;
    }

    /**
     * Assign an entity ID within this world.
     * <p>
     * Sync this to ensure entity IDs aren't duplicated?
     *
     * @return the new entity ID.
     */
    public synchronized int assignEntityId() {
        return players.size() + 1 + ThreadLocalRandom.current().nextInt(111, 999);
    }

    /**
     * @return {@code true} if this world is full.
     */
    public boolean isFull() {
        return players.size() >= capacity;
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     */
    public void spawnPlayerInWorld(Player player) {
        player.setWorldIn(this);

        // send all current players in this world to the connecting player.
        for (Player value : players.values()) value.sendTo(player);

        // broadcast the joining of this player to others.
        broadcastPacketInWorld(new SPacketCreatePlayer(player.getConnection().alloc(), player.getName(), player.getEntityId(), 0.0f, 0.0f));

        // add this new player to the map.
        players.put(player.getEntityId(), player);
    }

    /**
     * Remove the player in this world
     *
     * @param player the player
     */
    public void removePlayerInWorld(Player player) {
        players.remove(player.getEntityId());

        // broadcast removal to all.
        broadcastPacketInWorld(new SPacketRemovePlayer(player.getConnection().alloc(), player.getEntityId()));
    }

    /**
     * Handle velocity updates from players
     *
     * @param player    the player
     * @param velocityX X vel
     * @param velocityY Y vel
     * @param rotation  rotation
     */
    public void handlePlayerVelocity(Player player, float velocityX, float velocityY, int rotation) {
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
    public void handlePlayerPosition(Player player, float x, float y, int rotation) {
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
        for (Player player : players.values()) {
            player.getConnection().queue(packet);
        }
    }

    /**
     * Broadcast a packet to all players
     *
     * @param entityIdExcluded the entity ID to exclude
     * @param direct           the direct
     */
    public void broadcast(int entityIdExcluded, Packet direct) {
        for (Player value : players.values()) {
            if (value.getEntityId() != entityIdExcluded) {
                value.getConnection().queue(direct);
            }
        }
    }

    /**
     * Broadcast a packet to all players
     *
     * @param direct the direct
     */
    public void broadcast(ByteBuf direct) {
        players.forEach((entityId, player) -> {
            player.getConnection().queue(direct);
        });
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
