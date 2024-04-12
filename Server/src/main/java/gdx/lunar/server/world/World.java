package gdx.lunar.server.world;

import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.server.entity.ServerEntity;
import gdx.lunar.server.entity.ServerPlayerEntity;
import gdx.lunar.server.game.utilities.Disposable;

import java.util.concurrent.ConcurrentMap;

/**
 * Represents a world within the server with handling of basic functions
 */
public interface World extends Disposable {

    /**
     * @return the name of this world
     */
    String getName();

    /**
     * @return {@code  true} if this world is full
     */
    boolean isFull();

    /**
     * @param entityId the player's entity ID
     * @return {@code  true} if the provided entity ID exists within the players list.
     */
    boolean hasPlayer(int entityId);

    /**
     * @param entityId the (entities) entity ID
     * @return {@code  true} if the provided entity ID exists within the entities list.
     */
    boolean hasEntity(int entityId);

    /**
     * @return a map of all players in this world
     */
    ConcurrentMap<Integer, ServerPlayerEntity> getPlayers();

    /**
     * @return a map of all entities in this world
     */
    ConcurrentMap<Integer, ServerEntity> getEntities();

    /**
     * Check if a player is timed out based on the world configuration
     *
     * @param player the player
     * @param now    the current time
     * @return {@code true} if the player is timed out
     */
    boolean isTimedOut(ServerPlayerEntity player, float now);

    /**
     * Timeout the player
     *
     * @param player the player
     */
    void timeoutPlayer(ServerPlayerEntity player);

    /**
     * @param username the username to check
     * @return {@code true} if username does exist within the world.
     */
    boolean doesUsernameExist(String username);

    /**
     * Assign an entity ID for a player or entity
     *
     * @param isPlayer if the entity is a player
     * @return a new entity ID
     */
    int assignEntityIdFor(boolean isPlayer);

    /**
     * Handle a player position update
     *
     * @param player   the player
     * @param x        their X
     * @param y        their Y
     * @param rotation their rotation
     */
    void handlePlayerPosition(ServerPlayerEntity player, float x, float y, float rotation);

    /**
     * Handle a player velocity update
     *
     * @param player   the player
     * @param x        their vel X
     * @param y        their vel Y
     * @param rotation their rotation
     */
    void handlePlayerVelocity(ServerPlayerEntity player, float x, float y, float rotation);

    /**
     * Spawn a player in this world
     *
     * @param player the player
     */
    void spawnPlayerInWorld(ServerPlayerEntity player);

    /**
     * Spawn an entity into the world
     *
     * @param entity the entity
     */
    void spawnEntityInWorld(ServerEntity entity);

    /**
     * Remove a player in this world
     *
     * @param player the player
     */
    void removePlayerInWorld(ServerPlayerEntity player);

    /**
     * Spawn an entity in this world
     *
     * @param entity the entity
     */
    void removeEntityInWorld(ServerEntity entity);

    /**
     * Get a player from their entity ID
     *
     * @param entityId the entity ID
     * @return the player or {@code  null} if none exists
     */
    ServerPlayerEntity getPlayer(int entityId);

    <T extends ServerPlayerEntity> T getPlayerAs(int entityId);

    ServerEntity getEntity(int entityId);

    <T extends ServerEntity> T getEntityAs(int entityId);

    /**
     * Broadcast a packet.
     * This is queued and not sent instantly.
     *
     * @param packet the packet
     */
    void broadcast(Packet packet);

    /**
     * Broadcast a packet (now) but exclude an entity ID from that broadcast.
     *
     * @param exclusion the entity ID to exclude
     * @param packet    the packet
     */
    void broadcastNowWithExclusion(int exclusion, Packet packet);

    /**
     * Broadcast a packet (queued) but exclude an entity ID from that broadcast.
     *
     * @param exclusion the entity ID to exclude
     * @param packet    the packet
     */
    void broadcastWithExclusion(int exclusion, Packet packet);

    /**
     * Tick this world
     *
     * @param delta delta
     */
    void tick(float delta);

    /**
     * @return current time of the world
     */
    long getTime();

    /**
     * @return the current tick of the world
     */
    float getTick();

}
