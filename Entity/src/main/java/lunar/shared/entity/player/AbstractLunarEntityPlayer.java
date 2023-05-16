package lunar.shared.entity.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.texture.AbstractLunarTexturedEntity;

/**
 * Default implementation of {@link LunarEntityPlayer}
 */
public abstract class AbstractLunarEntityPlayer extends AbstractLunarTexturedEntity implements LunarEntityPlayer {

    // if other player collisions should be turned off.
    protected boolean ignoreOtherPlayerCollision;

    // connection for this player
    protected AbstractConnection connection;
    protected long positionSendRate, velocitySendRate;
    protected long lastPosition, lastVelocity;

    public AbstractLunarEntityPlayer(Entity entity, boolean addDefaultComponents) {
        super(entity, addDefaultComponents);
    }

    public AbstractLunarEntityPlayer(boolean addDefaultComponents) {
        super(addDefaultComponents);
    }

    @Override
    public AbstractConnection getConnection() {
        return connection;
    }

    @Override
    public void setConnection(AbstractConnection connection) {
        this.connection = connection;
        connection.setPlayerSupplier(this);
    }

    @Override
    public void disablePlayerCollision(boolean collision) {
        this.ignoreOtherPlayerCollision = collision;
    }

    @Override
    public boolean isPlayerCollisionDisabled() {
        return ignoreOtherPlayerCollision;
    }

    @Override
    public void setNetworkSendRateInMs(long velocity, long position) {
        velocitySendRate = velocity;
        positionSendRate = position;
    }

    @Override
    public void defineEntity(World world, float x, float y) {
        getPosition().set(x, y);
        getPreviousPosition().set(x, y);
        getInterpolatedPosition().set(x, y);

        this.body = definitionHandler.createBodyInWorld(world, x, y, getProperties());
        this.body.setUserData(this);
        definitionHandler.resetDefinition();
    }

    @Override
    public void spawnInWorld(LunarWorld world, Vector2 position) {
        defineEntity(world.getEntityWorld(), position.x, position.y);
        world.spawnPlayerInWorld(this, position);
        this.inWorld = true;
    }

    @Override
    public void spawnInWorld(LunarWorld world) {
        this.spawnInWorld(world, world.getWorldSpawn());
    }

    @Override
    public void removeFromWorld(LunarWorld world) {
        if (body != null) {
            world.getEntityWorld().destroyBody(body);
            body = null;
        }
        this.inWorld = false;
    }

    @Override
    public void update(float delta) {
        if (connection != null) {
            final long now = System.currentTimeMillis();
            if (lastPosition == 0 || (now - lastPosition) >= positionSendRate) {
                connection.updatePosition(getAngle(), getX(), getY());
                lastPosition = now;
            }

            if (lastVelocity == 0 || (now - lastVelocity) >= velocitySendRate) {
                connection.updateVelocity(getAngle(), getVelocity().x, getVelocity().y);
                lastVelocity = now;
            }
            connection.update();
        }
    }

    @Override
    public <T extends LunarEntityPlayer> T getPlayer() {
        return (T) this;
    }
}
