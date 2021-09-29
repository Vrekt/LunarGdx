package gdx.lunar.server.netty.codec;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.network.AbstractConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Handles decoding packets sent from clients
 */
public final class ClientProtocolPacketDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * The local session packet handler
     */
    private final AbstractConnection handler;

    public ClientProtocolPacketDecoder(AbstractConnection handler) {
        super(Integer.MAX_VALUE, 0, 4);
        this.handler = handler;
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
                final int pid = buf.readByte() & 0xFF;
                if (LunarProtocol.isClientPacket(pid)) {
                    handler.setLastPacketReceived(System.currentTimeMillis());
                    LunarProtocol.handleClientPacket(pid, buf, handler, ctx);
                }
            }
        } catch (Exception any) {
            any.printStackTrace();
            ctx.fireExceptionCaught(any);
        } finally {
            if (buf != null) buf.release();
        }
        return null;
    }
}
