package gdx.lunar.network.adapter;

import com.badlogic.gdx.Gdx;
import gdx.lunar.ProtocolSettings;
import gdx.lunar.network.AbstractConnectionHandler;
import gdx.lunar.protocol.packet.client.C2SPacketAuthenticate;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * A basic adapter for handling exceptions and first connection.
 */
public class FirstInboundConnectionHandler extends ChannelInboundHandlerAdapter {

    /**
     * The connection of the current player.
     */
    private final AbstractConnectionHandler connection;

    public FirstInboundConnectionHandler(AbstractConnectionHandler connection) {
        this.connection = connection;
    }

    /**
     * Invoked when the channel is first open, IE: we connected to the server.
     *
     * @param context context.
     */
    @Override
    public void channelActive(ChannelHandlerContext context) {
        connection.sendImmediately(new C2SPacketAuthenticate(ProtocolSettings.gameVersion, ProtocolSettings.protocolVersion));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Gdx.app.log("InboundNetworkHandler", "Exception caught", cause);
        connection.close();
    }
}
