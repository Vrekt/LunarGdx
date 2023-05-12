package gdx.lunar.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.network.types.ConnectionOption;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.PacketFactory;
import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.utilities.PlayerSupplier;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Represents a basic connection.
 */
public abstract class AbstractConnection implements ServerPacketHandler, Disposable {

    protected PlayerSupplier playerSupplier;

    protected final Channel channel;
    protected LunarProtocol protocol;
    protected boolean isConnected;

    // packet flush interval.
    // default is 50 ms
    protected float updateInterval = 50.0f;
    protected long lastUpdate = System.currentTimeMillis();
    protected long lastPacketReceived;

    // queue of packets
    protected final ConcurrentLinkedQueue<Packet> queue = new ConcurrentLinkedQueue<>();
    // TODO: Um no
    protected final ExecutorService single = Executors.newCachedThreadPool();

    protected final Map<ConnectionOption, Handler> handlers = new HashMap<>();

    public AbstractConnection(Channel channel, LunarProtocol protocol) {
        this.channel = channel;
        this.protocol = protocol;
    }

    public long getLastPacketReceived() {
        return lastPacketReceived;
    }

    public void setLastPacketReceived(long lastPacketReceived) {
        this.lastPacketReceived = lastPacketReceived;
    }

    public void setPlayerSupplier(PlayerSupplier playerSupplier) {
        this.playerSupplier = playerSupplier;
    }

    /**
     * Set the protocol to use
     *
     * @param protocol protocol
     */
    public void setProtocol(LunarProtocol protocol) {
        this.protocol = protocol;
    }

    public void setUpdateInterval(float updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Register a custom handler to handle certain packets that are incoming.
     * This method is invoked from the network thread.
     *
     * @param handler the handler
     * @param c       the consumer
     */
    public void registerHandlerAsync(ConnectionOption handler, Consumer<Packet> c) {
        handlers.put(handler, new Handler(c, false));
    }

    /**
     * Register a custom handler to handle certain packets that are incoming.
     * This method is invoked from the Main GDX thread.
     *
     * @param handler the handler
     * @param c       the consumer
     */
    public void registerHandlerSync(ConnectionOption handler, Consumer<Packet> c) {
        handlers.put(handler, new Handler(c, true));
    }

    /**
     * Register a custom packet handler.
     *
     * @param pid     the pid
     * @param handler the handler
     */
    public <T> void registerPacket(int pid, PacketFactory<T> factory, Consumer<T> handler) {
        protocol.registerCustomPacket(pid, in -> handler.accept(factory.create(in)));
    }

    /**
     * Send player position packet
     *
     * @param rotation rotation
     * @param x        X
     * @param y        Y
     */
    public void updatePosition(float rotation, float x, float y) {
        this.sendImmediately(new CPacketPosition(rotation, x, y));
    }

    /**
     * Send player velocity packet
     *
     * @param rotation rotation
     * @param velX     X
     * @param velY     Y
     */
    public void updateVelocity(float rotation, float velX, float velY) {
        this.sendImmediately(new CPacketVelocity(rotation, velX, velY));
    }

    /**
     * Send disconnect packet
     */
    public void disconnect() {
        this.sendImmediately(new CPacketDisconnect());
    }

    /**
     * Send world loaded
     */
    public void updateWorldLoaded() {
        this.sendImmediately(new CPacketWorldLoaded());
    }

    /**
     * Send join world
     *
     * @param world the world name
     */
    public void joinWorld(String world, String username) {
        this.sendImmediately(new CPacketJoinWorld(world, username));
    }

    /**
     * Send set username of local player
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.send(new CPacketSetProperties(username));
    }

    /**
     * @return if this connection is connected in any way.
     */
    public boolean isConnected() {
        return isConnected;
    }

    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    /**
     * Queue's the provided packet.
     *
     * @param packet the packet
     */
    public void send(Packet packet) {
        packet.alloc(alloc());
        queue.add(packet);
    }

    /**
     * Send a packet now
     *
     * @param packet the packet
     */
    public void sendImmediately(Packet packet) {
        packet.alloc(alloc());
        channel.writeAndFlush(packet);
    }

    /**
     * Update this connection
     */
    public void update() {
        if (System.currentTimeMillis() - lastUpdate
                >= updateInterval) {
            single.execute(this::flush);
            lastUpdate = System.currentTimeMillis();
        }
    }

    public void flush() {
        for (Packet packet = queue.poll(); packet != null; packet = queue.poll()) {
            channel.write(packet);
        }

        channel.flush();
    }

    public void close() {
        channel.close();
    }

    @Override
    public void dispose() {
        single.shutdownNow();
        flush();
        close();
    }

    /**
     * A basic packet handler.
     */
    protected static final class Handler {
        private final Consumer<Packet> handler;
        private final boolean isSync;

        private Handler(Consumer<Packet> handler, boolean isSync) {
            this.handler = handler;
            this.isSync = isSync;
        }

        void handle(Packet packet) {
            if (isSync) {
                Gdx.app.postRunnable(() -> handler.accept(packet));
            } else {
                handler.accept(packet);
            }
        }

    }

}
