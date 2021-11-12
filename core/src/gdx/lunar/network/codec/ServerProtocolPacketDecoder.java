package gdx.lunar.network.codec;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.handler.ServerPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Handles decoding local server packets
 */
public class ServerProtocolPacketDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * The handler
     */
    private final ServerPacketHandler handler;
    private LunarProtocol protocol;

    /**
     * Initialize this local decoder
     *
     * @param handler the handler
     */
    public ServerProtocolPacketDecoder(ServerPacketHandler handler, LunarProtocol protocol) {
        super(Integer.MAX_VALUE, 0, 4);
        this.handler = handler;
        this.protocol = protocol;
    }

    public LunarProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(LunarProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        final ByteBuf buf = (ByteBuf) super.decode(ctx, in);
        if (buf != null) {
            // ignore the length of the packet.
            buf.readInt();
            // retrieve packet from PID
            final int pid = buf.readInt();
            protocol.handleServerPacket(pid, buf, handler, ctx);

            buf.release();
        } else {
            throw new DecoderException("ByteBuf from server is null.");
        }
        return null;
    }
}

