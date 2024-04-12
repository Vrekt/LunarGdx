package gdx.lunar.server.entity;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.game.utilities.Disposable;
import gdx.lunar.server.world.World;

/**
 * Represents a server entity
 */
public interface ServerEntity extends Disposable {

    /**
     * @return the server
     */
    LunarServer getServer();

    /**
     * Set the world this entity is in
     *
     * @param world the world
     */
    void setWorldIn(World world);

    /**
     * @return the world this entity is in
     */
    World getWorld();

    /**
     * Set if this entity is in a world
     *
     * @param inWorld inWorld
     */
    void setInWorld(boolean inWorld);

    /**
     * @ {@code true} if this entity is in a world.
     */
    boolean isInWorld();

    /**
     * Set the entityId of this entity
     *
     * @param id the id
     */
    void setEntityId(int id);

    /**
     * @return this entities unique ID
     */
    int getEntityId();

    /**
     * Set the name of this entity
     *
     * @param name the name
     */
    void setName(String name);

    /**
     * @return the name of this entity.
     * Possibly {@code null} depending on the situation
     */
    String getName();

    /**
     * Set the position of this entity
     *
     * @param x x
     * @param y y
     */
    void setPosition(float x, float y);

    /**
     * Set the position of this entity
     *
     * @param position position
     */
    void setPosition(Vector2 position);

    /**
     * @return the position of this entity
     */
    Vector2 getPosition();

    /**
     * Set the velocity of this entity
     *
     * @param x velocity X
     * @param y velocity Y
     */
    void setVelocity(float x, float y);

    /**
     * Set the velocity of this entity
     *
     * @param velocity velocity
     */
    void setVelocity(Vector2 velocity);

    /**
     * @return the velocity of this entity
     */
    Vector2 getVelocity();

    /**
     * Set the rotation of this entity
     *
     * @param rotation rotation
     */
    void setRotation(float rotation);

    /**
     * @return the rotation of this entity
     */
    float getRotation();

}
