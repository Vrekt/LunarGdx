package gdx.lunar.server.world;

import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketRemovePlayer;
import gdx.lunar.server.entity.LunarServerEntity;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.game.utilities.Disposable;
import gdx.lunar.server.instance.Instance;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import gdx.lunar.world.AbstractWorld;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a world that is networked.
 */
public abstract class ServerWorld extends AbstractWorld<LunarServerPlayerEntity, LunarServerEntity> implements Disposable {
    protected final ServerWorldConfiguration configuration;
    protected final String worldName;

    /**
     * Queued packet updates
     */
    protected final Queue<QueuedPacket> queuedPackets = new ConcurrentLinkedQueue<>();
    // all the instances within this world
    protected final CopyOnWriteArrayList<Instance> instancesInWorld = new CopyOnWriteArrayList<>();

    public ServerWorld(ServerWorldConfiguration configuration, String worldName) {
        this.configuration = configuration;
        this.worldName = worldName;
    }

    public void addInstance(Instance instance) {
        this.instancesInWorld.add(instance);
    }

    public Instance getInstance(int instanceId) {
        return instancesInWorld.stream().filter(instance -> instance.getInstanceId() == instanceId).findFirst().orElse(null);
    }

    public boolean doesUsernameExist(String username) {
        return players.values().stream().anyMatch(player -> player.getName().equalsIgnoreCase(username));
    }

    /**
     * @return the name of this world.
     */
    public String getName() {
        return worldName;
    }

    /**
     * @param player the player
     * @return {@code true} if this player is timed out based on {@code playerTimeoutMs}
     */
    protected boolean isTimedOut(LunarServerPlayerEntity player) {
        return System.currentTimeMillis() - player.getServerConnection().getLastPacketReceived() >= configuration.getPlayerTimeoutMs();
    }

    /**
     * Remove the player from worlds and disconnect the connection
     *
     * @param player the player
     */
    protected void timeoutPlayer(LunarServerPlayerEntity player) {
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
    public boolean isFull() {
        return players.size() >= configuration.getCapacity();
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     * @param x      X
     * @param y      Y
     */
    public void spawnPlayerInWorld(LunarServerPlayerEntity player, float x, float y) {
        player.setWorldIn(this);
        player.setPosition(x, y, true);

        // send all current players in this world to the connecting player.
        for (LunarServerPlayerEntity other : players.values()) other.sendPlayerToPlayer(player);

        // broadcast the joining of this player to others.
        broadcastPacketImmediately(player.getEntityId(),
                new SPacketCreatePlayer(player.getName(), player.getEntityId(), x, y));

        // add this new player to the list
        players.put(player.getEntityId(), player);
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     */
    public void spawnPlayerInWorld(LunarServerPlayerEntity player) {
        spawnPlayerInWorld(player, 0, 0);
    }

    /**
     * Remove the player in this world
     *
     * @param player the player
     */
    public void removePlayerInWorld(LunarServerPlayerEntity player) {
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
    public void handlePlayerVelocity(LunarServerPlayerEntity player, float velocityX, float velocityY, float rotation) {
        player.getVelocity().set(velocityX, velocityY);
        player.setRotation(rotation);
    }

    /**
     * Handle position updates from players
     *
     * @param player   the player
     * @param x        x
     * @param y        y
     * @param rotation rotation
     */
    public void handlePlayerPosition(LunarServerPlayerEntity player, float x, float y, float rotation) {
        player.setPosition(x, y, true);
        player.setRotation(rotation);
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
        for (LunarServerPlayerEntity player : players.values()) player.getServerConnection().queue(packet);
    }

    /**
     * Broadcast a packet and send it to all immediately.
     *
     * @param excluded the excluded ID
     * @param packet   the packet
     */
    public void broadcastPacketImmediately(int excluded, Packet packet) {
        for (LunarServerPlayerEntity player : players.values())
            if (player.getEntityId() != excluded) player.getServerConnection().sendImmediately(packet);
    }

    /**
     * Broadcast a packet to all players
     *
     * @param entityIdExcluded the entity ID to exclude
     * @param direct           the direct
     */
    public void broadcast(int entityIdExcluded, Packet direct) {
        for (LunarServerPlayerEntity value : players.values()) {
            if (value.getEntityId() != entityIdExcluded) {
                value.getServerConnection().queue(direct);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
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

}
