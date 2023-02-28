package gdx.lunar.server.network.connection.provider;

import gdx.lunar.server.network.connection.ServerAbstractConnection;
import io.netty.channel.socket.SocketChannel;

/**
 * Basic interface allowing custom implementations of {@link ServerAbstractConnection} to be used.
 */
public interface ConnectionProvider {

    /**
     * Create a new connection from the given channel
     *
     * @param channel the channel
     * @return the new connection.
     */
    ServerAbstractConnection createConnection(SocketChannel channel);

}
