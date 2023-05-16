package lunar.shared.entity.player.mp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import lunar.shared.entity.player.AbstractLunarEntityPlayer;

public abstract class AbstractLunarEntityNetworkPlayer extends AbstractLunarEntityPlayer implements LunarEntityNetworkPlayer {

    protected boolean interpolatePosition, doPositionInterpolation;
    protected float interpolateToX, interpolateToY;

    // snap player to the correct position if distance >= interpolateDesyncDistance
    protected boolean snapToPositionIfDesync;
    // Default distance that player will interpolate to if they are too far away from server position.
    protected float interpolateDesyncDistance = 3.0f;

    public AbstractLunarEntityNetworkPlayer(Entity entity, boolean addDefaultComponents) {
        super(entity, addDefaultComponents);
    }

    public AbstractLunarEntityNetworkPlayer(boolean addDefaultComponents) {
        super(addDefaultComponents);
    }

    @Override
    public void setInterpolatePosition(boolean interpolatePosition) {
        this.interpolatePosition = interpolatePosition;
    }

    @Override
    public void setSnapToPositionIfDesynced(boolean snap) {
        this.snapToPositionIfDesync = snap;
    }

    @Override
    public void setDesyncDistanceToInterpolate(float distance) {
        this.interpolateDesyncDistance = distance;
    }

    @Override
    public void updatePosition(float x, float y, float angle) {
        final float dst = getPosition().dst2(x, y);

        // interpolate to position if too far away (de sync)
        if (dst >= interpolateDesyncDistance) {
            if (snapToPositionIfDesync) {
                setPosition(x, y, true);
            } else {
                doPositionInterpolation = true;
                interpolateToX = x;
                interpolateToY = y;
            }
        }
        setAngle(angle);
    }

    @Override
    public void updateVelocity(float x, float y, float angle) {
        getVelocity().set(x, y);
        setMoving(x != 0 || y != 0);
        setAngle(angle);
    }

    @Override
    public void update(float delta) {
        if (interpolatePosition && doPositionInterpolation) {
            final Vector2 interpolated = getInterpolatedPosition();

            interpolated.x = Interpolation.linear.apply(body.getPosition().x, interpolateToX, interpolationAlpha);
            interpolated.y = Interpolation.linear.apply(body.getPosition().y, interpolateToY, interpolationAlpha);

            // update body position.
            final float diffX = body.getPosition().x - interpolateToX;
            final float diffY = body.getPosition().y - interpolateToY;
            body.setLinearVelocity(diffX * interpolationAlpha, diffY * interpolationAlpha);
            setPosition(body.getPosition().x, body.getPosition().y, false);

            doPositionInterpolation = false;
            return;
        }

        // update velocity and set player position.
        body.setLinearVelocity(getVelocity());
        setPosition(body.getPosition().x, body.getPosition().y, false);
    }
}
