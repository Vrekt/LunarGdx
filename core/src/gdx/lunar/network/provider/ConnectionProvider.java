package gdx.lunar.network.provider;

import gdx.lunar.network.AbstractConnectionHandler;
import io.netty.channel.Channel;

/**
 * Basic interface allowing custom implementations of {@link AbstractConnectionHandler} to be used.
 */
public interface ConnectionProvider {

    /**
     * Create a new connection from the given channel
     *
     * @param channel  the channel
     * @return the new connection.
     */
    AbstractConnectionHandler createConnection(Channel channel);

}
