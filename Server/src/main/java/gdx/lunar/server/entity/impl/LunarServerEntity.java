package gdx.lunar.server.entity.impl;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.server.entity.ServerEntity;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.world.World;

/**
 * Base implementation of a server entity
 */
public class LunarServerEntity implements ServerEntity {

    protected LunarServer server;
    protected World world;
    protected boolean inWorld;

    protected int entityId;
    protected String name;

    protected Vector2 position, velocity;
    protected float rotation;

    public LunarServerEntity(LunarServer server) {
        this.server = server;

        this.position = new Vector2();
        this.velocity = new Vector2();
    }

    @Override
    public LunarServer getServer() {
        return server;
    }

    @Override
    public void setWorldIn(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setInWorld(boolean inWorld) {
        this.inWorld = inWorld;
    }

    @Override
    public boolean isInWorld() {
        return inWorld;
    }

    @Override
    public void setEntityId(int id) {
        this.entityId = id;
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    @Override
    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void dispose() {
        world = null;
        inWorld = false;
        entityId = -1;
        name = null;
    }
}
