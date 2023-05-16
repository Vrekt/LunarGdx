package lunar.shared.entity.player.mp;

import lunar.shared.entity.player.LunarEntityPlayer;

/**
 * Represents a network player
 */
public interface LunarEntityNetworkPlayer extends LunarEntityPlayer {

    /**
     * Enable or disable interpolating of a network players position
     *
     * @param interpolatePosition the state
     */
    void setInterpolatePosition(boolean interpolatePosition);

    /**
     * If {@code true} once a certain threshold is met of de-sync between positions
     * This player will be snapped to back to its intended position
     *
     * @param snap the state
     */
    void setSnapToPositionIfDesynced(boolean snap);

    /**
     * Set the distance required for de-sync between positions for the player to
     * snap-back to its intended or server position
     * Only required if {@code setSnapToPositionIfDesynced} if {@code true}
     * Usual values of this would be between 1.0 (harsh) and 3.0 (more lenient)
     *
     * @param distance the distance
     */
    void setDesyncDistanceToInterpolate(float distance);

    /**
     * Update position of this player from the server
     *
     * @param x     the X
     * @param y     the Y
     * @param angle angle or rotation
     */
    void updatePosition(float x, float y, float angle);

    /**
     * Update velocity of this player from the server
     *
     * @param x     the X
     * @param y     the Y
     * @param angle angle or rotation
     */
    void updateVelocity(float x, float y, float angle);

}
