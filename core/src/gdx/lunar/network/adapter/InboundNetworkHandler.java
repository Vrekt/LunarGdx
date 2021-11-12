package gdx.lunar.network.adapter;

import gdx.lunar.Lunar;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.packet.client.CPacketAuthentication;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * A basic adapter for handling exceptions and first connection.
 */
public class InboundNetworkHandler extends ChannelInboundHandlerAdapter {

    /**
     * The connection of the current player.
     */
    private final AbstractConnection connection;

    public InboundNetworkHandler(AbstractConnection connection) {
        this.connection = connection;
    }

    /**
     * Invoked when the channel is first open, IE: we connected to the server.
     *
     * @param context context.
     */
    @Override
    public void channelActive(ChannelHandlerContext context) {
        connection.sendImmediately(new CPacketAuthentication(Lunar.gameVersion, Lunar.protocolVersion));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        connection.close();
    }
}
