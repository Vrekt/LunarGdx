package gdx.lunar.server.netty.codec;

import gdx.lunar.protocol.GdxProtocol;
import gdx.lunar.server.network.connection.ServerAbstractConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Handles decoding packets sent from clients
 */
public class ClientProtocolPacketDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * The local session packet handler
     */
    private final ServerAbstractConnection handler;
    private final GdxProtocol protocol;

    public ClientProtocolPacketDecoder(ServerAbstractConnection handler, GdxProtocol protocol) {
        super(protocol.getMaxPacketFrameLength(), 0, 4);
        this.handler = handler;
        this.protocol = protocol;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) super.decode(ctx, in);
            if (buf != null) {
                // ignore the length of the packet.
                buf.readInt();
                // retrieve packet from PID
                final int pid = buf.readInt();
                if (protocol.isCustomPacket(pid)) {
                    protocol.handleCustomPacket(pid, buf, ctx);
                } else if (protocol.isClientPacket(pid)) {
                    handler.setLastPacketReceived(System.currentTimeMillis());
                    protocol.handleClientPacket(pid, buf, handler, ctx);
                }
            }
        } catch (Exception any) {
            ctx.fireExceptionCaught(any);
        } finally {
            if (buf != null) {
                buf.release();
            }
        }
        return null;
    }
}
