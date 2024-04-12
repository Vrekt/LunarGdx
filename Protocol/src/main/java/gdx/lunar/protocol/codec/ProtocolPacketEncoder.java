package gdx.lunar.protocol.codec;

import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Encodes incoming packets then appends the length + packet.
 * <p>
 * 10-12-2021: Allow this to be extended.
 */
@ChannelHandler.Sharable
public class ProtocolPacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        try {
            packet.encode();

            final int length = packet.getBuffer().readableBytes();
            out.writeInt(length);
            out.writeBytes(packet.getBuffer());
        } catch (Exception any) {
            ctx.fireExceptionCaught(any);
        } finally {
            packet.release();
        }
    }

}
