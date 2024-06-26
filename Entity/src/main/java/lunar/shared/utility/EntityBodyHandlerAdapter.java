package lunar.shared.utility;

import com.badlogic.gdx.physics.box2d.*;
import lunar.shared.components.EntityPropertiesComponent;

/**
 * A default definition creator for an entity
 */
public class EntityBodyHandlerAdapter implements EntityBodyHandler {

    private BodyDef definition;
    private FixtureDef fixture;
    private boolean hasSetFixedRotation, hasSetDensity;
    private boolean fixedRotation;
    private float density;

    @Override
    public void setBodyDefinition(BodyDef definition) {
        this.definition = definition;
    }

    @Override
    public void setFixtureDefinition(FixtureDef definition) {
        this.fixture = definition;
    }

    @Override
    public void setHasFixedRotation(boolean fixedRotation) {
        this.hasSetFixedRotation = true;
        this.fixedRotation = fixedRotation;
    }

    @Override
    public void setDensity(float density) {
        this.hasSetDensity = true;
        this.density = density;
    }

    @Override
    public void resetDefinition() {
        definition = null;
        fixture = null;
    }

    @Override
    public Body createBodyInWorld(World world, float x, float y, EntityPropertiesComponent configuration) {
        if (definition == null) {
            definition = new BodyDef();
            definition.type = BodyDef.BodyType.DynamicBody;
        }

        if (this.fixture == null)
            this.fixture = new FixtureDef();

        if (hasSetFixedRotation) {
            definition.fixedRotation = fixedRotation;
        }

        definition.position.set(x, y);

        final Body body = world.createBody(definition);
        PolygonShape shape = null;

        if (fixture.shape == null) {
            shape = new PolygonShape();
            shape.setAsBox(configuration.getScaledWidth() / 2f, configuration.getScaledHeight() / 2f);
            fixture.shape = shape;
            if (!hasSetDensity) {
                fixture.density = 1.0f;
            } else {
                fixture.density = density;
            }
        }

        body.createFixture(fixture);
        if (shape != null) shape.dispose();
        return body;
    }
}
