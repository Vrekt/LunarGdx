package gdx.lunar.network.codec;

import com.badlogic.gdx.Gdx;
import gdx.lunar.network.AbstractConnectionHandler;
import gdx.lunar.protocol.GdxProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Handles decoding local server packets
 */
public class ServerProtocolPacketDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * The handler
     */
    private final AbstractConnectionHandler handler;
    private final GdxProtocol protocol;

    /**
     * Initialize this local decoder
     *
     * @param handler the handler
     */
    public ServerProtocolPacketDecoder(AbstractConnectionHandler handler, GdxProtocol protocol) {
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
                    handler.setLastPacketReceived(System.currentTimeMillis());
                    protocol.handleCustomPacket(pid, buf, ctx);
                } else if (protocol.isServerPacket(pid)) {
                    handler.setLastPacketReceived(System.currentTimeMillis());
                    protocol.handleServerPacket(pid, buf, handler, ctx);
                } else {
                    Gdx.app.log("ServerProtocolPacketDecoder", "Received unknown packet from server %d".formatted(pid));
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

