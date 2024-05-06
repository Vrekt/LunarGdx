package lunar.shared.entity.player;

import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.network.AbstractConnectionHandler;
import gdx.lunar.utilities.PlayerSupplier;
import lunar.shared.entity.texture.LunarTexturedEntity;

/**
 * Represents a player that may either be local or networked.
 */
public interface LunarEntityPlayer extends LunarTexturedEntity, PlayerSupplier {

    /**
     * Get the network connection of this player
     *
     * @return the connection
     */
    AbstractConnectionHandler getConnection();

    /**
     * Set the network connection of this player
     * Default implementations will automatically inform {@link AbstractConnectionHandler} of this player
     *
     * @param connection the connection
     */
    void setConnection(AbstractConnectionHandler connection);

    /**
     * Disable or enable collision between other players
     *
     * @param collision state
     */
    void disablePlayerCollision(boolean collision);

    /**
     * @return {@code  true} if other player collision is disabled
     */
    boolean isPlayerCollisionDisabled();

    /**
     * Set the send rate of velocity and position packets in milliseconds
     * A value of {@code 50} will send velocity and/or position packets every 50 milliseconds.
     *
     * @param velocity the velocity milliseconds send time
     * @param position the position milliseconds send time
     */
    void setNetworkSendRateInMs(long velocity, long position);

    /**
     * Send related position and velocity packets if it's time to do so.
     */
    void updateNetworkPositionAndVelocity();

    /**
     * Define this entities {@link com.badlogic.gdx.physics.box2d.Body} and spawn it in the provided {@link World}
     *
     * @param world the world
     * @param x     the position X to spawn at
     * @param y     the position Y to spawn at
     */
    void defineEntity(World world, float x, float y);

}
