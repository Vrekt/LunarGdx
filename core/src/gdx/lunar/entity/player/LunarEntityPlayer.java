package gdx.lunar.entity.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import gdx.lunar.entity.components.drawing.EntityAnimation;
import gdx.lunar.entity.components.drawing.EntityAnimationComponent;
import gdx.lunar.entity.drawing.LunarAnimatedEntity;
import gdx.lunar.entity.player.mp.LunarNetworkEntityPlayer;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.packet.client.CPacketApplyEntityBodyForce;
import gdx.lunar.world.LunarWorld;

/**
 * Represents a local or network player.
 */
public abstract class LunarEntityPlayer extends LunarAnimatedEntity {

    // animation comp system
    protected ComponentMapper<EntityAnimationComponent> animationComponent = ComponentMapper.getFor(EntityAnimationComponent.class);

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
        this.definition.fixedRotation = fixedRotation;
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

    public void setAnimationComponent(ComponentMapper<EntityAnimationComponent> animationComponent) {
        this.animationComponent = animationComponent;
    }

    public EntityAnimationComponent getAnimationComponent() {
        return animationComponent.get(entity);
    }

    public void registerAnimation(int id, EntityAnimation animation) {
        getAnimationComponent().animations.put(id, animation);
    }

    public void registerAnimation(int id, Animation<TextureRegion> animation) {
        getAnimationComponent().animations.put(id, new EntityAnimation(animation));
    }

    @Override
    public <P extends LunarEntityPlayer,
            N extends LunarNetworkEntityPlayer,
            E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> world, float x, float y) {
        getPosition().set(x, y);
        getPrevious().set(x, y);
        getInterpolated().set(x, y);

        this.definition = new BodyDef();
        this.fixture = new FixtureDef();

        if (definition.type == null) definition.type = BodyDef.BodyType.DynamicBody;
        if (!hasSetFixedRotation) definition.fixedRotation = true;
        definition.position.set(x, y);

        body = world.getWorld().createBody(definition);
        PolygonShape shape = null;

        if (fixture.shape == null) {
            shape = new PolygonShape();
            shape.setAsBox(getWidth() / 2f, getHeight() / 2f);
            fixture.shape = shape;
            if (!hasSetDensity) fixture.density = 1.0f;
        }

        body.createFixture(fixture).setUserData(this);
        if (shape != null) shape.dispose();

        world.spawnEntityInWorld(this, x, y);
        this.inWorld = true;
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> world) {
        spawnEntityInWorld(world, world.getSpawn().x, world.getSpawn().y);
    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void removeEntityInWorld(LunarWorld<P, N, E> world) {
        world.removeEntityInWorld(this);
        world.getWorld().destroyBody(body);
    }

    @Override
    public void update(float delta) {
        if (connection != null) connection.update();
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
