package gdx.lunar.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import lunar.shared.entity.LunarEntity;
import lunar.shared.entity.player.LunarEntityPlayer;

/**
 * Represents a single game world
 */
public interface LunarWorld extends Disposable {

    /**
     * @return the name of this world
     */
    String getWorldName();

    /**
     * Set the name of this world
     *
     * @param worldName the world name
     */
    void setWorldName(String worldName);

    /**
     * @return the world's entity engine
     */
    Engine getEntityEngine();

    /**
     * Set the entity engine of this world
     *
     * @param engine the engine
     */
    void setEntityEngine(Engine engine);

    /**
     * @return the box2d {@link World} of this world
     */
    World getEntityWorld();

    /**
     * @return {@code  true} if this world is full
     */
    boolean isFull();

    /**
     * Set the spawn of this world
     *
     * @param position the position
     */
    void setWorldSpawn(Vector2 position);

    /**
     * @return the world spawn point or {@code  null} if none.
     */
    Vector2 getWorldSpawn();

    /**
     * @return the world configuration
     */
    WorldConfiguration getConfiguration();

    /**
     * Adds a default player collision listener to the {@link World}
     * With this, if any player comes in contact with another player
     * and that player has disabled collision on then it will be ignored
     * and no collision will be processed.
     */
    void addDefaultPlayerCollisionListener();

    /**
     * Spawn an entity in this world
     *
     * @param entity   the entity
     * @param position the position to spawn them at
     * @param <T>      type
     */
    <T extends LunarEntity> void spawnEntityInWorld(T entity, Vector2 position);

    /**
     * Spawn an entity in this world
     * This method will spawn the provided entity at {@code getWorldSpawn}
     *
     * @param entity the entity
     * @param <T>    type
     */
    <T extends LunarEntity> void spawnEntityInWorld(T entity);

    /**
     * Spawn a player in this world
     *
     * @param player   the player
     * @param position the position to spawn them at
     * @param <T>      type
     */
    <T extends LunarEntityPlayer> void spawnPlayerInWorld(T player, Vector2 position);

    /**
     * Spawn a player in this world
     * This method will spawn the provided player at {@code getWorldSpawn}
     *
     * @param player the player
     * @param <T>    type
     */
    <T extends LunarEntityPlayer> void spawnPlayerInWorld(T player);

    /**
     * Remove an entity from this world
     *
     * @param entityId the ID
     */
    void removeEntityInWorld(int entityId);

    /**
     * Remove a player from this world
     *
     * @param entityId the ID
     */
    void removePlayerInWorld(int entityId);

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
     * Update a players position within this world
     *
     * @param entityId the entity ID
     * @param x        their X
     * @param y        their Y
     * @param angle    angle
     */
    void updatePlayerPositionInWorld(int entityId, float x, float y, float angle);

    /**
     * Update a players velocity within this world
     *
     * @param entityId the entity ID
     * @param x        their vel X
     * @param y        their vel Y
     * @param angle    angle
     */
    void updatePlayerVelocityInWorld(int entityId, float x, float y, float angle);

    /**
     * Update player properties
     *
     * @param id       the player ID
     * @param name     the name
     * @param entityId the entity ID
     */
    void updatePlayerProperties(int id, String name, int entityId);

    /**
     * Update this world
     *
     * @param delta the delta
     * @return the capped delta time
     */
    float update(float delta);

    /**
     * Step the physics simulation of this world
     *
     * @param delta the delta time
     */
    void stepPhysicsSimulation(float delta);

}
