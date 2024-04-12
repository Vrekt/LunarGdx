package gdx.lunar.server.network.connection;

import gdx.lunar.protocol.PacketFactory;
import gdx.lunar.protocol.handlers.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.server.game.LunarServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.function.Consumer;

/**
 * Represents a basic connection.
 */
public abstract class ServerAbstractConnection extends ChannelInboundHandlerAdapter implements ClientPacketHandler {

    protected final Channel channel;
    protected boolean isConnected;
    private long lastPacketReceived;
    protected LunarServer server;

    public ServerAbstractConnection(Channel channel, LunarServer server) {
        this.channel = channel;
        this.server = server;
    }

    /**
     * Register a custom packet handler.
     *
     * @param pid     the pid
     * @param handler the handler
     */
    public <T> void registerPacket(int pid, PacketFactory<T> factory, Consumer<T> handler) {
        server.getProtocol().registerPacket(pid, in -> handler.accept(factory.create(in)));
    }

    public long getLastPacketReceived() {
        return lastPacketReceived;
    }

    public void setLastPacketReceived(long lastPacketReceived) {
        this.lastPacketReceived = lastPacketReceived;
    }

    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    /**
     * Update this connection.
     */
    public void update() {

    }

    /**
     * Disconnect
     */
    public abstract void disconnect();

    /**
     * Connection closed error
     *
     * @param exception the possible exception or {@code  null} if none
     */
    public abstract void connectionClosed(Throwable exception);

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
    public void sendImmediately(Packet packet) {
        packet.alloc(alloc());
        channel.writeAndFlush(packet);
    }

    /**
     * Queue a packet
     *
     * @param packet the packet
     */
    public void queue(Packet packet) {
        packet.alloc(alloc());
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
        connectionClosed(null);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        connectionClosed(cause);
    }
}
