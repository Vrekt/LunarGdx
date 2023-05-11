package gdx.lunar.server.world.testing;

import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.game.utilities.Disposable;

/**
 * Represents a world within the server with handling of basic functions
 */
public interface World extends Disposable {

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
     * Check if a player is timed out based on the world configuration
     *
     * @param player the player
     * @param now    the current time
     * @param <T>    type
     * @return {@code true} if the player is timed out
     */
    <T extends LunarServerPlayerEntity> boolean isTimedOut(T player, float now);

    /**
     * Timeout the player
     *
     * @param player the player
     * @param <T>    their type
     */
    <T extends LunarServerPlayerEntity> void timeoutPlayer(T player);

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
     * @param player the player
     * @param x      their X
     * @param y      their Y
     * @param angle  their angle
     * @param <T>    type
     */
    <T extends LunarServerPlayerEntity> void handlePlayerPosition(T player, float x, float y, float angle);

    /**
     * Handle a player velocity update
     *
     * @param player the player
     * @param x      their vel X
     * @param y      their vel Y
     * @param angle  their angle
     * @param <T>    type
     */
    <T extends LunarServerPlayerEntity> void handlePlayerVelocity(T player, float x, float y, float angle);

    /**
     * Spawn a player in this world
     *
     * @param player the player
     * @param <T>    type
     */
    <T extends LunarServerPlayerEntity> void spawnPlayerInWorld(T player);

    /**
     * Remove a player in this world
     *
     * @param player the player
     * @param <T>    type
     */
    <T extends LunarServerPlayerEntity> void removePlayerInWorld(T player);

    /**
     * Spawn an entity in this world
     *
     * @param entity the entity
     * @param <T>    type
     */
    <T extends LunarServerPlayerEntity> void removeEntityInWorld(T entity);

    /**
     * Get a player from their entity ID
     *
     * @param entityId the entity ID
     * @param <T>      type
     * @return the player or {@code  null} if none exists
     */
    <T extends LunarServerPlayerEntity> T getPlayer(int entityId);

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

}
