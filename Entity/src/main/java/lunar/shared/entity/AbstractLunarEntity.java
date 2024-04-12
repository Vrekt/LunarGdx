package lunar.shared.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import gdx.lunar.world.LunarWorld;
import lunar.shared.components.position.EntityPositionComponent;
import lunar.shared.components.position.EntityVelocityComponent;
import lunar.shared.components.prop.EntityPropertiesComponent;
import lunar.shared.mapping.GlobalEntityMapper;
import lunar.shared.utility.BasicDirection;
import lunar.shared.utility.EntityBodyCreator;
import lunar.shared.utility.EntityBodyHandler;

/**
 * Represents a basic entity
 */
public abstract class AbstractLunarEntity implements LunarEntity {

    // local game world in
    protected LunarWorld worldIn;
    protected boolean inWorld, moving;

    protected Entity entity;
    protected Body body;

    protected float interpolationAlpha = 1.0f;

    // handles creating new box2d bodies
    protected EntityBodyHandler definitionHandler = new EntityBodyCreator();

    public AbstractLunarEntity(Entity entity, boolean addDefaultComponents) {
        this(addDefaultComponents);
        this.entity = entity;
    }

    public AbstractLunarEntity(boolean addDefaultComponents) {
        if (addDefaultComponents) addComponents();
    }

    @Override
    public LunarWorld getWorld() {
        return worldIn;
    }

    @Override
    public void setWorld(LunarWorld worldIn) {
        this.worldIn = worldIn;
    }

    @Override
    public boolean isInWorld() {
        return inWorld;
    }

    @Override
    public void setInWorld(boolean inWorld) {
        this.inWorld = inWorld;
    }

    @Override
    public int getEntityId() {
        return getProperties().entityId;
    }

    @Override
    public void setEntityId(int entityId) {
        getProperties().entityId = entityId;
    }

    @Override
    public String getName() {
        return getProperties().entityName;
    }

    @Override
    public void setName(String name) {
        getProperties().entityName = name;
    }

    @Override
    public void setProperties(String name, int id) {
        getProperties().setProperties(id, name);
    }

    @Override
    public EntityBodyHandler getBodyHandler() {
        return definitionHandler;
    }

    @Override
    public void setBodyHandler(EntityBodyHandler handler) {
        this.definitionHandler = handler;
    }

    @Override
    public void setHasFixedRotation(boolean rotation) {
        if (definitionHandler != null) definitionHandler.setHasFixedRotation(rotation);
    }

    @Override
    public float getWidth() {
        return getProperties().width;
    }

    @Override
    public float getHeight() {
        return getProperties().height;
    }

    @Override
    public float getWorldScale() {
        return getProperties().scaling;
    }

    @Override
    public float getScaledWidth() {
        return getProperties().getScaledWidth();
    }

    @Override
    public float getScaledHeight() {
        return getProperties().getScaledHeight();
    }

    @Override
    public void setWidth(float width) {
        getProperties().width = width;
    }

    @Override
    public void setHeight(float height) {
        getProperties().height = height;
    }

    @Override
    public void setWorldScale(float scale) {
        getProperties().scaling = scale;
    }

    @Override
    public void setSize(float width, float height, float scale) {
        getProperties().setEntitySize(width, height, scale);
    }

    @Override
    public float getMoveSpeed() {
        return getProperties().getSpeed();
    }

    @Override
    public void setMoveSpeed(float speed) {
        getProperties().speed = speed;
    }

    @Override
    public float getHealth() {
        return getProperties().health;
    }

    @Override
    public void setHealth(float health) {
        getProperties().health = health;
    }

    @Override
    public float heal(float amount) {
        return getProperties().health += amount;
    }

    @Override
    public float damage(float amount) {
        return getProperties().health -= amount;
    }

    @Override
    public Vector2 getPosition() {
        return GlobalEntityMapper.position.get(entity).position;
    }

    @Override
    public float getX() {
        return getPosition().x;
    }

    @Override
    public float getY() {
        return getPosition().y;
    }

    @Override
    public Vector2 getVelocity() {
        return GlobalEntityMapper.velocity.get(entity).velocity;
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        getVelocity().set(velocity);
    }

    @Override
    public void setVelocity(float x, float y) {
        getVelocity().set(x, y);
    }

    @Override
    public void setPosition(Vector2 position, boolean transform) {
        getPosition().set(position);
        if (transform && body != null)
            body.setTransform(position, getAngle());
    }

    @Override
    public void setPosition(float x, float y, boolean transform) {
        getPosition().set(x, y);
        if (transform && body != null)
            body.setTransform(x, y, getAngle());
    }

    @Override
    public boolean isMoving() {
        return moving;
    }

    @Override
    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    @Override
    public Vector2 getPreviousPosition() {
        return GlobalEntityMapper.position.get(entity).previous;
    }


    @Override
    public void setPreviousPosition(Vector2 position) {
        getPreviousPosition().set(position);
    }

    @Override
    public void setPreviousPosition(float x, float y) {
        getPreviousPosition().set(x, y);
    }

    @Override
    public Vector2 getInterpolatedPosition() {
        return GlobalEntityMapper.position.get(entity).interpolated;
    }

    @Override
    public void setInterpolatedPosition(Vector2 position) {
        getInterpolatedPosition().set(position);
    }

    @Override
    public void setInterpolatedPosition(float x, float y) {
        getInterpolatedPosition().set(x, y);
    }

    @Override
    public void setInterpolationAlpha(float alpha) {
        this.interpolationAlpha = alpha;
    }

    @Override
    public void interpolatePosition() {
        interpolatePosition(Interpolation.linear, interpolationAlpha);
    }

    @Override
    public void interpolatePosition(float alpha) {
        interpolatePosition(Interpolation.linear, alpha);
    }

    @Override
    public void interpolatePosition(Interpolation interpolation, float alpha) {
        // TODO: Maybe set has moved
        final Vector2 previous = getPreviousPosition();
        final Vector2 current = body.getPosition();
        setInterpolatedPosition(interpolation.apply(previous.x, current.x, alpha), interpolation.apply(previous.y, current.y, alpha));
    }

    @Override
    public float getAngle() {
        return getProperties().angle;
    }

    @Override
    public void setAngle(float angle) {
        getProperties().angle = angle;
    }

    @Override
    public BasicDirection getDirection() {
        return getProperties().direction;
    }

    @Override
    public void setDirection(BasicDirection direction) {
        getProperties().direction = direction;
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public void setEntity(Entity entity, boolean disposeOldEntity) {
        if (disposeOldEntity) this.entity.removeAll();
        this.entity = entity;
    }

    @Override
    public void addComponents() {
        if (entity == null) entity = new Entity();
        entity.add(new EntityPropertiesComponent());
        entity.add(new EntityPositionComponent());
        entity.add(new EntityVelocityComponent());
    }

    @Override
    public EntityPropertiesComponent getProperties() {
        return GlobalEntityMapper.properties.get(entity);
    }

    @Override
    public void loadEntity() {

    }

    @Override
    public void dispose() {
        entity.removeAll();
        worldIn = null;
        inWorld = false;
    }
}
