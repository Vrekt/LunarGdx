package gdx.lunar.entity.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import gdx.lunar.entity.drawing.LunarAnimatedEntity;
import gdx.lunar.entity.player.mp.LunarNetworkEntityPlayer;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.packet.client.CPacketApplyEntityBodyForce;
import gdx.lunar.world.LunarWorld;

/**
 * Represents a local or network player.
 */
public abstract class LunarEntityPlayer extends LunarAnimatedEntity {

    // if other player collisions should be turned off.
    protected boolean ignorePlayerCollision;

    // definition for this player.
    protected BodyDef definition;
    protected FixtureDef fixture;
    // if user specified custom rotation or density
    protected boolean hasSetFixedRotation, hasSetDensity;

    // connection for this player
    protected AbstractConnection connection;

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

    /**
     * @return if this player ignores collision with other players.
     */
    public boolean isIgnorePlayerCollision() {
        return ignorePlayerCollision;
    }

    /**
     * @return definition for this entity player.
     */
    public BodyDef getDefinition() {
        return definition;
    }

    /**
     * @return fixture
     */
    public FixtureDef getFixture() {
        return fixture;
    }

    /**
     * Set the body definition type
     *
     * @param type type
     */
    public void setBodyType(BodyDef.BodyType type) {
        this.definition.type = type;
    }

    /**
     * Set if fixed rotation
     *
     * @param fixedRotation state
     */
    public void setFixedRotation(boolean fixedRotation) {
        hasSetFixedRotation = true;
    }

    /**
     * Set density of the players fixture
     *
     * @param density density
     */
    public void setDensity(float density) {
        this.fixture.density = density;
        hasSetDensity = true;
    }

    /**
     * Set the shape of this player
     *
     * @param shape the shape
     */
    public void setPlayerShape(Shape shape) {
        this.fixture.shape = shape;
    }

    @Override
    public <P extends LunarEntityPlayer,
            N extends LunarNetworkEntityPlayer,
            E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> world, float x, float y) {
        getPosition().set(x, y);
        getPrevious().set(x, y);
        setInterpolated(x, y);

        if (definition == null) {
            definition = new BodyDef();
            definition.type = BodyDef.BodyType.DynamicBody;
        }

        this.fixture = new FixtureDef();

        if (!hasSetFixedRotation) definition.fixedRotation = true;
        definition.position.set(x + getWidthScaled() / 2f, y + getHeightScaled() / 2f);

        body = world.getWorld().createBody(definition);
        PolygonShape shape = null;

        if (fixture.shape == null) {
            shape = new PolygonShape();
            shape.setAsBox(getWidthScaled() / 2f, getHeightScaled() / 2f);
            fixture.shape = shape;
            if (!hasSetDensity) fixture.density = 1.0f;
        }

        body.createFixture(fixture).setUserData(this);
        if (shape != null) shape.dispose();

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
    public void update(float delta) {
        if (connection != null) {
            connection.update();
            connection.updatePosition(rotation, getX(), getY());
            connection.updateVelocity(rotation, getVelocity().x, getVelocity().y);
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
        connection.send(new CPacketApplyEntityBodyForce(getProperties().entityId, fx, fy, px, py));
    }

}
