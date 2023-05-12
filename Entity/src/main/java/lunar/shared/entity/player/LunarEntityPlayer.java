package lunar.shared.entity.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.world.LunarWorld;
import gdx.lunar.utilities.PlayerSupplier;
import lunar.shared.entity.texture.LunarTexturedEntity;

/**
 * Represents a basic player
 */
public abstract class LunarEntityPlayer extends LunarTexturedEntity implements PlayerSupplier {

    // if other player collisions should be turned off.
    protected boolean ignoreOtherPlayerCollision;

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
        connection.setPlayerSupplier(this);
    }

    public AbstractConnection getConnection() {
        return connection;
    }

    /**
     * If this player should ignore collisions with other players.
     *
     * @param ignoreOtherPlayerCollision the state
     */
    public void setIgnoreOtherPlayerCollision(boolean ignoreOtherPlayerCollision) {
        this.ignoreOtherPlayerCollision = ignoreOtherPlayerCollision;
    }

    public void setNetworkSendRatesInMs(long velocitySendRate, long positionSendRate) {
        this.velocitySendRate = velocitySendRate;
        this.positionSendRate = positionSendRate;
    }

    /**
     * @return if this player ignores collision with other players.
     */
    public boolean doIgnorePlayerCollision() {
        return ignoreOtherPlayerCollision;
    }

    /**
     * Define this player's box2d entity
     *
     * @param world the box2d world
     * @param x     spawn x
     * @param y     spawn y
     */
    public void defineEntity(World world, float x, float y) {
        getPosition().set(x, y);
        getPrevious().set(x, y);
        setInterpolated(x, y);

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

    @Override
    public <T extends LunarEntityPlayer> T getPlayer() {
        return (T) this;
    }
}
