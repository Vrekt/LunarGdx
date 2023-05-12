package lunar.shared.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.world.LunarWorld;
import lunar.shared.utility.EntityBodyCreator;
import lunar.shared.utility.EntityBodyHandler;
import lunar.shared.components.position.EntityPositionComponent;
import lunar.shared.components.position.EntityVelocityComponent;
import lunar.shared.components.prop.EntityPropertiesComponent;
import lunar.shared.mapping.GlobalEntityMapper;

/**
 * A basic entity within the LunarProtocolSettings framework.
 */
public abstract class LunarEntity implements Disposable, Spawnable {

    // local game world in
    protected LunarWorld worldIn;

    // this entity
    protected Entity entity;

    // box2d body of this entity
    protected Body body;
    protected boolean inWorld, hasMoved;
    protected float rotation, moveSpeed = 1.0f, interpolationAmount = 1.0f;

    // handles creating new box2d bodies
    protected EntityBodyHandler definitionHandler = new EntityBodyCreator();

    public LunarEntity(Entity entity, boolean initializeComponents) {
        this.entity = entity;
        if (initializeComponents) addComponents();
    }

    public LunarEntity(boolean initializeComponents) {
        this.entity = new Entity();
        if (initializeComponents) addComponents();
    }

    public void setDefinitionHandler(EntityBodyHandler definitionHandler) {
        this.definitionHandler = definitionHandler;
    }

    public LunarWorld getWorldIn() {
        return worldIn;
    }

    public void setWorldIn(LunarWorld worldIn) {
        if (worldIn != null) this.inWorld = true;
        this.worldIn = worldIn;
    }

    public EntityBodyHandler getDefinitionHandler() {
        return definitionHandler;
    }

    public void setWidth(float width) {
        GlobalEntityMapper.properties.get(entity).size.x = width;
    }

    public void setHeight(float height) {
        GlobalEntityMapper.properties.get(entity).size.y = height;
    }

    public float getWidth() {
        return GlobalEntityMapper.properties.get(entity).size.x;
    }

    public float getHeight() {
        return GlobalEntityMapper.properties.get(entity).size.y;
    }

    public float getWidthScaled() {
        return GlobalEntityMapper.properties.get(entity).getScaledWidth();
    }

    public float getHeightScaled() {
        return GlobalEntityMapper.properties.get(entity).getScaledHeight();
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoving(boolean moving) {
        getProperties().isMoving = moving;
    }

    public void setProperties(String name, int id) {
        setEntityName(name);
        setEntityId(id);
    }

    public void setInterpolationAmount(float interpolationAmount) {
        this.interpolationAmount = interpolationAmount;
    }

    public float getInterpolationAmount() {
        return interpolationAmount;
    }

    /**
     * Load any assets with this entity.
     */
    public void load() {

    }

    /**
     * Set the size of this entity
     *
     * @param width  w
     * @param height h
     */
    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    /**
     * Set world scaling of this entity
     *
     * @param scaling scaling
     */
    public void setScaling(float scaling) {
        GlobalEntityMapper.properties.get(entity).size.z = scaling;
    }

    public float getScaling() {
        return GlobalEntityMapper.properties.get(entity).size.z;
    }

    /**
     * Set general configuration
     *
     * @param width   width
     * @param height  height
     * @param scaling (world) scaling
     */
    public void setSize(float width, float height, float scaling) {
        GlobalEntityMapper.properties.get(entity).setConfig(width, height, scaling);
    }

    public EntityPropertiesComponent getProperties() {
        return GlobalEntityMapper.properties.get(entity);
    }

    public void setEntityId(int entityId) {
        getProperties().entityId = entityId;
    }

    public int getEntityId() {
        return getProperties().entityId;
    }

    public void setEntityName(String name) {
        getProperties().entityName = name;
    }

    public String getName() {
        return getProperties().entityName;
    }

    /**
     * Add components to the internal entity.
     */
    protected void addComponents() {
        if (entity == null) entity = new Entity();
        entity.add(new EntityPropertiesComponent());
        entity.add(new EntityPositionComponent());
        entity.add(new EntityVelocityComponent());
    }

    /**
     * @return position of this entity.
     */
    public Vector2 getPosition() {
        return GlobalEntityMapper.position.get(entity).position;
    }

    public float getX() {
        return getPosition().x;
    }

    public float getY() {
        return getPosition().y;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public void setFixedRotation(boolean rotation) {
        definitionHandler.setHasFixedRotation(rotation);
    }

    /**
     * @return velocity of this entity.
     */
    public Vector2 getVelocity() {
        return GlobalEntityMapper.velocity.get(entity).velocity;
    }

    /**
     * @return previous position of this entity.
     */
    public Vector2 getPrevious() {
        return GlobalEntityMapper.position.get(entity).previous;
    }

    /**
     * @return interpolated position of this entity.
     */
    public Vector2 getInterpolated() {
        return GlobalEntityMapper.position.get(entity).interpolated;
    }

    /**
     * Set the position
     *
     * @param x         X
     * @param y         Y
     * @param transform if body transform should be used
     */
    public void setPosition(float x, float y, boolean transform) {
        getPosition().set(x, y);
        if (transform && body != null)
            body.setTransform(x, y, body.getTransform().getRotation());
    }

    /**
     * Set the position
     *
     * @param position  new pos
     * @param transform if body transform should be used
     */
    public void setPosition(Vector2 position, boolean transform) {
        getPosition().set(position);
        if (transform && body != null)
            body.setTransform(position.x, position.y, body.getTransform().getRotation());
    }

    /**
     * Set the velocity
     *
     * @param x         X
     * @param y         Y
     * @param transform if body transform should be used
     */
    public void setVelocity(float x, float y, boolean transform) {
        getVelocity().set(x, y);
        if (transform && body != null)
            body.setTransform(x, y, body.getTransform().getRotation());
    }

    public void setInterpolated(float x, float y) {
        getInterpolated().set(x, y);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Body getBody() {
        return body;
    }

    /**
     * @return the internal Ashley entity.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Set this entities Ashley entity
     *
     * @param entity     the entity
     * @param disposeOld if {@code true} the internal entity will be removed of,
     *                   you should handle removing this entity from the system.
     */
    public void setEntity(Entity entity, boolean disposeOld) {
        if (disposeOld) this.entity.removeAll();
        this.entity = entity;
    }

    /**
     * Interpolate the players position.
     */
    public void interpolatePosition() {
        interpolatePosition(Interpolation.linear, getInterpolationAmount());
    }

    /**
     * Interpolate the players position.
     *
     * @param alpha linear alpha
     */
    public void interpolatePosition(float alpha) {
        interpolatePosition(Interpolation.linear, alpha);
    }

    /**
     * Interpolate the players position.
     *
     * @param interpolation the interpolation to use
     * @param alpha         linear alpha
     */
    public void interpolatePosition(Interpolation interpolation, float alpha) {
        if (hasMoved) {
            final Vector2 previous = getPrevious();
            final Vector2 current = body.getPosition();
            setInterpolated(interpolation.apply(previous.x, current.x, alpha), interpolation.apply(previous.y, current.y, alpha));
        }
    }

    public EntityVelocityComponent getVelocityComponent() {
        return GlobalEntityMapper.velocity.get(entity);
    }

    public boolean isInWorld() {
        return inWorld;
    }

    public void setInWorld(boolean inWorld) {
        this.inWorld = inWorld;
    }

    /**
     * Update this entity
     *
     * @param delta the delta time
     */
    public abstract void update(float delta);

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

        // TODO
        //getWorldIn().getLocalConnection().sendImmediately(new CPacketApplyEntityBodyForce(getEntityId(), fx, fy, px, py));
    }

    @Override
    public void dispose() {
        entity.removeAll();
    }
}
