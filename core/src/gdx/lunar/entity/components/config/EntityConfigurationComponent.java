package gdx.lunar.entity.components.config;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

/**
 * Manages data for an entities basic configuration.
 * <p>
 * Their width, height and scaling. Or anything else later down the line.
 */
public class EntityConfigurationComponent implements Component, Pool.Poolable {

    // width, height, scaling
    public Vector3 size = new Vector3();

    // if offset should be used with Box2d.
    // This will put players inside the Box2d bounding box.
    public boolean offset = true;

    public void setConfig(float width, float height, float scaling) {
        size.set(width, height, scaling);
    }

    @Override
    public void reset() {
        size.set(0, 0, 0);
    }
}
