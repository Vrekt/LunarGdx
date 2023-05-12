package lunar.shared.entity.player.mp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import lunar.shared.entity.player.LunarEntityPlayer;

/**
 * Represents a player that is playing over the network.
 */
public abstract class LunarNetworkEntityPlayer extends LunarEntityPlayer {

    protected boolean interpolatePosition, doPositionInterpolation;
    protected float interpolateToX, interpolateToY;

    // snap player to the correct position if distance >= interpolateDesyncDistance
    protected boolean snapToPositionIfDesync;
    // Default distance that player will interpolate to if they are too far away from server position.
    protected float interpolateDesyncDistance = 3.0f;
    protected float interpolateAlpha = 0.5f;

    public LunarNetworkEntityPlayer(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public LunarNetworkEntityPlayer(boolean initializeComponents) {
        super(initializeComponents);
    }

    /**
     * Player will snap to their server location if distance is too far.
     *
     * @param snapToPositionIfDesync state
     */
    public void setSnapToPositionIfDesync(boolean snapToPositionIfDesync) {
        this.snapToPositionIfDesync = snapToPositionIfDesync;
    }

    /**
     * If interpolation should be used when de-sync occurs.
     *
     * @param doPositionInterpolation state
     */
    public void setInterpolatePosition(boolean doPositionInterpolation) {
        this.interpolatePosition = doPositionInterpolation;
    }

    /**
     * Set distance required to snap or interpolate.
     *
     * @param interpolateDesyncDistance state
     */
    public void setInterpolateDesyncDistance(float interpolateDesyncDistance) {
        this.interpolateDesyncDistance = interpolateDesyncDistance;
    }

    /**
     * Set alpha to use when interpolating
     *
     * @param interpolateAlpha alpha
     */
    public void setInterpolateAlpha(float interpolateAlpha) {
        this.interpolateAlpha = interpolateAlpha;
    }

    /**
     * Update this players position from the server.
     *
     * @param x     X
     * @param y     Y
     * @param angle angle
     */
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
        this.rotation = angle;
    }

    /**
     * Update velocity
     *
     * @param x     x
     * @param y     y
     * @param angle angle
     */
    public void updateVelocity(float x, float y, float angle) {
        getVelocity().set(x, y);
        setMoving(x != 0 || y != 0);
        this.rotation = angle;
    }

    /**
     * Update the box2d body force.
     *
     * @param x  x
     * @param y  y
     * @param px point x
     * @param py point y
     */
    public void updateBodyForce(float x, float y, float px, float py) {
        body.applyForce(x, y, px, py, true);
    }

    @Override
    public void update(float delta) {
        if (interpolatePosition && doPositionInterpolation) {
            final Vector2 interpolated = getInterpolated();
            ;

            interpolated.x = Interpolation.linear.apply(body.getPosition().x, interpolateToX, interpolateAlpha);
            interpolated.y = Interpolation.linear.apply(body.getPosition().y, interpolateToY, interpolateAlpha);

            // update body position.
            final float diffX = body.getPosition().x - interpolateToX;
            final float diffY = body.getPosition().y - interpolateToY;
            body.setLinearVelocity(diffX * interpolateAlpha, diffY * interpolateAlpha);
            setPosition(body.getPosition().x, body.getPosition().y, false);

            doPositionInterpolation = false;
            return;
        }

        // update velocity and set player position.
        body.setLinearVelocity(getVelocity());
        setPosition(body.getPosition().x, body.getPosition().y, false);
    }
}
