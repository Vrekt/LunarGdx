package lunar.shared.entity.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.network.AbstractConnectionHandler;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.LunarEntity;

/**
 * Represents a base of a player whether the controlling player or a network player.
 */
public abstract class LunarEntityPlayer extends LunarEntity {

    protected boolean ignoreOtherPlayerCollision;
    // ideally, do not use this variable
    // use getConnection() for override functionality like custom
    // connection handlers
    protected AbstractConnectionHandler connection;
    protected long positionSendRate, velocitySendRate;
    protected long lastPosition, lastVelocity;

    public LunarEntityPlayer(Entity entity, boolean addDefaultComponents) {
        super(entity, addDefaultComponents);
    }

    public LunarEntityPlayer(boolean addDefaultComponents) {
        super(addDefaultComponents);
    }

    /**
     * Get the network connection of this player
     *
     * @return the connection
     */
    public AbstractConnectionHandler getConnection() {
        return connection;
    }

    /**
     * Set the network connection of this player
     * Default implementations will automatically inform {@link AbstractConnectionHandler} of this player
     *
     * @param connection the connection
     */
    public void setConnection(AbstractConnectionHandler connection) {
        this.connection = connection;
        connection.setPlayer(this);
    }

    /**
     * Disable or enable collision between other players
     *
     * @param collision state
     */
    public void disablePlayerCollision(boolean collision) {
        this.ignoreOtherPlayerCollision = collision;
    }

    /**
     * @return {@code  true} if other player collision is disabled
     */
    public boolean isPlayerCollisionDisabled() {
        return ignoreOtherPlayerCollision;
    }

    /**
     * Set the send rate of velocity and position packets in milliseconds
     * A value of {@code 50} will send velocity and/or position packets every 50 milliseconds.
     *
     * @param velocity the velocity milliseconds send time
     * @param position the position milliseconds send time
     */
    public void setNetworkSendRateInMs(long velocity, long position) {
        velocitySendRate = velocity;
        positionSendRate = position;
    }

    /**
     * Send related position and velocity packets if it's time to do so.
     */
    public void updateNetworkPositionAndVelocity() {
        final long now = System.currentTimeMillis();
        if (lastPosition == 0 || (now - lastPosition) >= positionSendRate) {
            getConnection().updatePosition(getPosition(), getAngle());
            lastPosition = now;
        }

        if (lastVelocity == 0 || (now - lastVelocity) >= velocitySendRate) {
            getConnection().updateVelocity(getVelocity(), getAngle());
            lastVelocity = now;
        }
        getConnection().update();
    }

    /**
     * Define this entities {@link com.badlogic.gdx.physics.box2d.Body} and spawn it in the provided {@link World}
     *
     * @param world the world
     * @param x     the position X to spawn at
     * @param y     the position Y to spawn at
     */
    public void defineEntity(World world, float x, float y) {
        if (this.body != null) return;
        getPosition().set(x, y);
        getPreviousPosition().set(x, y);
        getInterpolatedPosition().set(x, y);

        this.body = definitionHandler.createBodyInWorld(world, x, y, getPropertiesComponent());
        this.body.setUserData(this);

        definitionHandler.resetDefinition();
    }

    @Override
    public void spawnInWorld(LunarWorld world, Vector2 position) {
        defineEntity(world.getEntityWorld(), position.x, position.y);
        world.spawnPlayerInWorld(this, position);
    }

    @Override
    public void spawnInWorld(LunarWorld world) {
        this.spawnInWorld(world, world.getWorldOrigin());
    }

    @Override
    public void removeFromWorld() {
        if (!inWorld) return;

        // don't destroy, we will do that ourselves.
        getWorld().removePlayerInWorld(getEntityId(), false);

        if (body != null) {
            getWorld().getEntityWorld().destroyBody(body);
            body = null;
        }

        this.inWorld = false;
    }

    @Override
    public void update(float delta) {
        if (getConnection() != null) {
            updateNetworkPositionAndVelocity();
        }
    }

}
