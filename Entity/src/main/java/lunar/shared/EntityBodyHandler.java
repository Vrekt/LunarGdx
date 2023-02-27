package lunar.shared;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import lunar.shared.components.prop.EntityPropertiesComponent;

/**
 * A base definition handler for creating new box2d bodies
 */
public interface EntityBodyHandler {

    /**
     * Set the default body definition.
     * Not required
     *
     * @param definition definition
     */
    void setBodyDefinition(BodyDef definition);

    /**
     * Set the default fixture definition
     * Not required
     *
     * @param definition definition
     */
    void setFixtureDefinition(FixtureDef definition);

    /**
     * Set if the body has a fixed rotation
     *
     * @param fixedRotation {@code  true} if so
     */
    void setHasFixedRotation(boolean fixedRotation);

    /**
     * Set the density
     *
     * @param density density
     */
    void setDensity(float density);

    /**
     * Reset the definition, typically called after {@code  createBodyInWorld}
     */
    void resetDefinition();

    /**
     * Create a new {@link  Body} within the given {@code  world}
     *
     * @param world     world
     * @param x         spawn X
     * @param y         spawn Y
     * @param component the entity props
     * @return the new body
     */
    Body createBodyInWorld(World world, float x, float y, EntityPropertiesComponent component);

}
