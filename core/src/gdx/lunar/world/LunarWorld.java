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
     * Set the origin of this world
     *
     * @param position the position
     */
    void setWorldOrigin(Vector2 position);

    /**
     * @return the world origin point or {@code  null} if none.
     */
    Vector2 getWorldOrigin();

    /**
     * @return the world configuration
     */
    WorldConfiguration getConfiguration();

    /**
     * Set the configuration of this world
     *
     * @param configuration the config
     */
    void setConfiguration(WorldConfiguration configuration);

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
     */
    void spawnEntityInWorld(LunarEntity entity, Vector2 position);

    /**
     * Spawn an entity in this world
     * This method will spawn the provided entity at {@code getWorldOrigin}
     *
     * @param entity the entity
     */
    void spawnEntityInWorld(LunarEntity entity);

    /**
     * Spawn a player in this world
     *
     * @param player   the player
     * @param position the position to spawn them at
     */
    void spawnPlayerInWorld(LunarEntityPlayer player, Vector2 position);

    /**
     * Spawn a player in this world
     * This method will spawn the provided player at {@code getWorldOrigin}
     *
     * @param player the player
     */
    void spawnPlayerInWorld(LunarEntityPlayer player);

    /**
     * Remove an entity from this world
     *
     * @param entityId the ID
     * @param destroy if the entity should be disposed of
     */
    void removeEntityInWorld(int entityId, boolean destroy);

    /**
     * Remove a player from this world
     *
     * @param entityId the ID
     * @param destroy  if the player should be destroyed. {@code false} if the player itself is calling this method.
     */
    void removePlayerInWorld(int entityId, boolean destroy);

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
