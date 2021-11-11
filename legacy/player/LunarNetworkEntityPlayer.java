package gdx.lunar.entity.player;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.world.LunarWorld;

/**
 * Represents a networked entity player.
 */
public abstract class LunarNetworkEntityPlayer extends LunarEntityPlayer {

    /**
     * If position should be interpolated
     */
    protected boolean doPositionInterpolation;
    protected float interpolateToX, interpolateToY;

    // snap player to the correct position if distance >= interpolateDesyncDistance
    protected boolean snapToPositionIfDesync;

    /**
     * Default distance that player will interpolate to if they are too far away from server position.
     */
    protected float interpolateDesyncDistance = 3.0f, worldStepTime;

    public LunarNetworkEntityPlayer(int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId, playerScale, playerWidth, playerHeight, rotation);
    }

    public LunarNetworkEntityPlayer(float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(playerScale, playerWidth, playerHeight, rotation);
    }

    public void setInterpolateDesyncDistance(float interpolateDesyncDistance) {
        this.interpolateDesyncDistance = interpolateDesyncDistance;
    }

    public void setSnapToPositionIfDesync(boolean snapToPositionIfDesync) {
        this.snapToPositionIfDesync = snapToPositionIfDesync;
    }

    @Override
    public void spawnEntityInWorld(LunarWorld world, float x, float y) {
        super.spawnEntityInWorld(world, x, y);
        world.setPlayerInWorld(this);
        this.worldStepTime = world.getStepTime();
    }

    /**
     * Update velocity from network packet.
     *
     * @param velocityX X
     * @param velocityY Y
     * @param rotation  rotation
     */
    public void updateVelocityFromNetwork(float velocityX, float velocityY, Rotation rotation) {
        this.rotation = rotation;
        velocity.set(velocityX, velocityY);
    }

    /**
     * Update position from network packet.
     *
     * @param x        x
     * @param y        y
     * @param rotation rotation
     */
    public void updatePositionFromNetwork(float x, float y, Rotation rotation) {
        this.rotation = rotation;
        final float dst = Vector2.dst(this.position.x, this.position.y, x, y);

        // interpolate to position if too far away (de sync)
        if (dst >= interpolateDesyncDistance) {
            if (snapToPositionIfDesync) {
                body.setTransform(x, y, 0.0f);
                setPosition(x, y);
            } else {
                doPositionInterpolation = true;
                interpolateToX = x;
                interpolateToY = y;
            }
        } else {
            setPosition(x, y);
        }
    }

    @Override
    public void update(float delta) {
        if (doPositionInterpolation) {
            interpolated.x = Interpolation.linear.apply(position.x, interpolateToX, 0.5f);
            interpolated.y = Interpolation.linear.apply(position.y, interpolateToY, 0.5f);

            // update body position.
            final float diffX = position.x - interpolateToX;
            final float diffY = position.y - interpolateToY;
            body.setLinearVelocity(diffX * worldStepTime, diffY * worldStepTime);
            setPosition(body.getPosition().x, body.getPosition().y);

            doPositionInterpolation = false;
            return;
        }

        // update velocity and set player position.
        body.setLinearVelocity(velocity.x, velocity.y);
        setPosition(body.getPosition().x, body.getPosition().y);

        // update rendering state
        if (renderer != null)
            renderer.update(delta, rotation, !velocity.isZero());
    }
}
