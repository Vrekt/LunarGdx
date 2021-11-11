package gdx.examples.advanced.server;

import gdx.examples.advanced.AdvancedExampleMain;
import gdx.lunar.Lunar;
import gdx.lunar.protocol.packet.client.CPacketAuthentication;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AdvancedNetworkHandler extends ChannelInboundHandlerAdapter {

    private final AdvancedExampleMain main;

    public AdvancedNetworkHandler(AdvancedExampleMain main) {
        this.main = main;
    }

    /**
     * Invoked when the channel is first open, IE: we connected to the server.
     *
     * @param context context.
     */
    @Override
    public void channelActive(ChannelHandlerContext context) {
        this.main.notifyConnection();
        context.writeAndFlush(new CPacketAuthentication(context.alloc(), Lunar.gameVersion, Lunar.protocolVersion));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.main.notifyDisconnect();
        main.getConnection().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.main.notifyDisconnect();
        main.getConnection().close();
    }

}
