package gdx.lunar.network.provider;

import gdx.lunar.network.AbstractConnection;
import io.netty.channel.Channel;

/**
 * Basic interface allowing custom implementations of {@link AbstractConnection} to be used.
 */
public interface ConnectionProvider {

    /**
     * Create a new connection from the given channel
     *
     * @param channel the channel
     * @return the new connection.
     */
    AbstractConnection createConnection(Channel channel);

}
