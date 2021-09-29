package gdx.lunar.server.network;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Represents a basic connection.
 */
public abstract class AbstractConnection extends ChannelInboundHandlerAdapter implements ClientPacketHandler {

    /**
     * The current channel of this connection.
     */
    protected final Channel channel;

    /**
     * If we are connected in any way.
     */
    protected boolean isConnected;

    public AbstractConnection(Channel channel) {
        this.channel = channel;
    }

    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    /**
     * Disconnect
     */
    public abstract void disconnect();

    /**
     * Connection closed error
     */
    public abstract void connectionClosed();

    /**
     * @return if this connection is connected in any way.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Send a packet
     *
     * @param packet the packet
     */
    public void send(Packet packet) {
        channel.writeAndFlush(packet);
    }

    /**
     * Queue a packet
     *
     * @param packet the packet
     */
    public void queue(Packet packet) {
        channel.write(packet);
    }

    /**
     * Queue a direct buffer
     *
     * @param direct buffer
     */
    public void queue(ByteBuf direct) {
        channel.write(direct);
    }

    /**
     * Flush
     */
    public void flush() {
        channel.flush();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        connectionClosed();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        connectionClosed();
    }
}
