package lunar.shared.entity.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents the base of a network player.
 */
public abstract class LunarEntityNetworkPlayer extends LunarEntityPlayer {

    protected boolean interpolatePosition, doPositionInterpolation;
    protected float interpolateToX, interpolateToY;

    // snap player to the correct position if distance >= interpolateDesyncDistance
    protected boolean snapToPositionIfDesync;
    // Default distance that player will interpolate to if they are too far away from server position.
    protected float interpolateDesyncDistance = 3.0f;

    public LunarEntityNetworkPlayer(Entity entity, boolean addDefaultComponents) {
        super(entity, addDefaultComponents);
    }

    public LunarEntityNetworkPlayer(boolean addDefaultComponents) {
        super(addDefaultComponents);
    }

    /**
     * Enable or disable interpolating of a network players position
     *
     * @param interpolatePosition the state
     */
    public void setInterpolatePosition(boolean interpolatePosition) {
        this.interpolatePosition = interpolatePosition;
    }

    /**
     * If {@code true} once a certain threshold is met of de-sync between positions
     * This player will be snapped to back to its intended position
     *
     * @param snap the state
     */
    public void setSnapToPositionIfDesynced(boolean snap) {
        this.snapToPositionIfDesync = snap;
    }

    /**
     * Set the distance required for de-sync between positions for the player to
     * snap-back to its intended or server position
     * Only required if {@code setSnapToPositionIfDesynced} if {@code true}
     * Usual values of this would be between 1.0 (harsh) and 3.0 (more lenient)
     *
     * @param distance the distance
     */
    public void setDesyncDistanceToInterpolate(float distance) {
        this.interpolateDesyncDistance = distance;
    }

    /**
     * Update position of this player from the server
     *
     * @param x     the X
     * @param y     the Y
     * @param angle angle or rotation
     */
    public void updatePositionFromNetwork(float x, float y, float angle) {
        final float dst = getPosition().dst2(x, y);

        // interpolate to position if too far away (de sync)
        if (dst >= interpolateDesyncDistance) {
            if (snapToPositionIfDesync) {
                setAngle(angle);
                setPosition(x, y, true);
            } else {
                doPositionInterpolation = true;
                interpolateToX = x;
                interpolateToY = y;
            }
        }
        setAngle(angle);
    }

    /**
     * Update velocity of this player from the server
     *
     * @param x     the X
     * @param y     the Y
     * @param angle angle or rotation
     */
    public void updateVelocityFromNetwork(float x, float y, float angle) {
        getVelocity().set(x, y);
        setAngle(angle);
    }

    @Override
    public void update(float delta) {
        if (interpolatePosition && doPositionInterpolation) {
            final Vector2 interpolated = getInterpolatedPosition();

            interpolated.x = Interpolation.linear.apply(getPosition().x, interpolateToX, interpolationAlpha);
            interpolated.y = Interpolation.linear.apply(getPosition().y, interpolateToY, interpolationAlpha);

            // update body position.
            final float diffX = getPosition().x - interpolateToX;
            final float diffY = getPosition().y - interpolateToY;

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
