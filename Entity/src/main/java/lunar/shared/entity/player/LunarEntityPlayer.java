package lunar.shared.entity.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import gdx.lunar.instance.LunarInstance;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.packet.client.CPacketApplyEntityBodyForce;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.drawing.LunarAnimatedEntity;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;

/**
 * Represents a local or network player.
 */
public abstract class LunarEntityPlayer extends LunarAnimatedEntity {

    // if other player collisions should be turned off.
    protected boolean ignorePlayerCollision;

    // connection for this player
    protected AbstractConnection connection;
    protected long positionSendRate, velocitySendRate;
    protected long lastPosition, lastVelocity;

    public LunarEntityPlayer(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public LunarEntityPlayer(boolean initializeComponents) {
        super(initializeComponents);
    }

    public void setConnection(AbstractConnection connection) {
        this.connection = connection;
        connection.setLocalPlayer(this);
    }

    public AbstractConnection getConnection() {
        return connection;
    }

    /**
     * If this player should ignore collisions with other players.
     *
     * @param ignorePlayerCollision the state
     */
    public void setIgnorePlayerCollision(boolean ignorePlayerCollision) {
        this.ignorePlayerCollision = ignorePlayerCollision;
    }

    public void setNetworkSendRatesInMs(long velocitySendRate, long positionSendRate) {
        this.velocitySendRate = velocitySendRate;
        this.positionSendRate = positionSendRate;
    }

    /**
     * @return if this player ignores collision with other players.
     */
    public boolean isIgnorePlayerCollision() {
        return ignorePlayerCollision;
    }

    /**
     * Define this players box2d entity
     *
     * @param world the box2d world
     * @param x     spawn x
     * @param y     spawn y
     */
    public void definePlayer(World world, float x, float y) {
        getPosition().set(x, y);
        getPrevious().set(x, y);
        setInterpolated(x, y);

        this.body = definitionHandler.createBodyInWorld(world, x, y, getConfig());
        definitionHandler.resetDefinition();
    }

    @Override
    public <P extends LunarEntityPlayer,
            N extends LunarNetworkEntityPlayer,
            E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> world, float x, float y) {
        definePlayer(world.getWorld(), x, y);
        world.spawnEntityInWorld(this, x, y);
        this.inWorld = true;
        this.getInstance().setWorldIn(world);
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> world) {
        spawnEntityInWorld(world, world.getSpawn().x, world.getSpawn().y);
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void removeEntityInWorld(LunarWorld<P, N, E> world) {
        if (body != null) {
            world.getWorld().destroyBody(body);
            body = null;
        }
        world.removeEntityInWorld(this);
        this.getInstance().setWorldIn(null);
        this.inWorld = false;
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInInstance(LunarInstance<P, N, E> instance, float x, float y) {
        getPosition().set(x, y);
        getPrevious().set(x, y);
        setInterpolated(x, y);

        // destroy previous body from the other world.
        getWorldIn().getWorld().destroyBody(body);
        body = null;

        // create a new body for the instance
        definePlayer(instance.getWorld(), x, y);
        instance.spawnEntityInWorld(this, x, y);

        this.inInstance = true;
        this.getInstance().setInstanceIn(instance);
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInInstance(LunarInstance<P, N, E> instance) {
        spawnEntityInInstance(instance, instance.getSpawn().x, instance.getSpawn().y);
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void removeEntityInInstance(LunarInstance<P, N, E> instance) {
        if (body != null) {
            instance.getWorld().destroyBody(body);
            body = null;
        }
        instance.removeEntityInWorld(this);
        this.getInstance().setInstanceIn(null);
        this.inInstance = false;
    }

    @Override
    public void update(float delta) {
        if (connection != null) {
            final long now = System.currentTimeMillis();
            if (lastPosition == 0 || (now - lastPosition) >= positionSendRate) {
                connection.updatePosition(rotation, getX(), getY());
                lastPosition = now;
            }

            if (lastVelocity == 0 || (now - lastVelocity) >= velocitySendRate) {
                connection.updateVelocity(rotation, getVelocity().x, getVelocity().y);
                lastVelocity = now;
            }
            connection.update();
        }
    }

    /**
     * Apply force to this player
     *
     * @param fx   force x
     * @param fy   force y
     * @param px   point x
     * @param py   point y
     * @param wake wake
     */
    public void applyForce(float fx, float fy, float px, float py, boolean wake) {
        getBody().applyForce(fx, fy, px, py, wake);
        connection.sendImmediately(new CPacketApplyEntityBodyForce(getProperties().entityId, fx, fy, px, py));
    }

}
