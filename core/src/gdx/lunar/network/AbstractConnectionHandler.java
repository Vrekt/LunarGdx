package gdx.lunar.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.protocol.GdxProtocol;
import gdx.lunar.protocol.PacketFactory;
import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.client.*;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import lunar.shared.entity.player.AbstractLunarEntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Handles the basic maintenance of a players connection
 */
public abstract class AbstractConnectionHandler implements ServerPacketHandler, Disposable {

    // the player this connection belongs to
    protected AbstractLunarEntityPlayer player;

    protected final Channel channel;
    protected GdxProtocol protocol;
    protected boolean isConnected;

    // packet flush interval.
    // default is 50 ms
    protected float updateInterval = 50.0f;
    protected long lastUpdate = System.currentTimeMillis();
    protected long lastPacketReceived;

    // queue of packets
    protected final ConcurrentLinkedQueue<Packet> queue = new ConcurrentLinkedQueue<>();
    // single threaded executor for flushing player connections
    protected final ExecutorService single = Executors.newSingleThreadExecutor();
    // map of handlers by packet ID.
    protected final Map<Integer, Handler> handlers = new HashMap<>();

    public AbstractConnectionHandler(Channel channel, GdxProtocol protocol) {
        this.channel = channel;
        this.protocol = protocol;
    }

    public long getLastPacketReceived() {
        return lastPacketReceived;
    }

    public void setLastPacketReceived(long lastPacketReceived) {
        this.lastPacketReceived = lastPacketReceived;
    }

    public void setPlayer(AbstractLunarEntityPlayer player) {
        this.player = player;
    }

    /**
     * Set the update interval this connection will use
     *
     * @param updateInterval the interval in milliseconds
     */
    public void setUpdateInterval(float updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Register a custom handler to handle certain packets that are incoming.
     * This method is invoked from the network thread.
     *
     * @param packetId the packet id
     * @param c        the consumer
     */
    public void registerHandlerAsync(int packetId, Consumer<Packet> c) {
        handlers.put(packetId, new Handler(c, false));
    }

    /**
     * Register a custom handler to handle certain packets that are incoming.
     * This method is invoked from the Main GDX thread.
     *
     * @param packetId the packet ID
     * @param c        the consumer
     */
    public void registerHandlerSync(int packetId, Consumer<Packet> c) {
        handlers.put(packetId, new Handler(c, true));
    }

    /**
     * Register a custom packet handler.
     *
     * @param pid     the pid
     * @param handler the handler
     */
    public <T> void registerPacket(int pid, PacketFactory<T> factory, Consumer<T> handler) {
        protocol.registerPacket(pid, in -> handler.accept(factory.create(in)));
    }

    /**
     * Update the server on this players' position
     *
     * @param x        x position
     * @param y        y position
     * @param rotation rotation
     */
    public void updatePosition(float x, float y, float rotation) {
        this.sendImmediately(new C2SPacketPlayerPosition(x, y, rotation));
    }

    /**
     * Update the server on this players' position
     *
     * @param position position
     * @param rotation rotation
     */
    public void updatePosition(Vector2 position, float rotation) {
        sendImmediately(new C2SPacketPlayerPosition(position, rotation));
    }

    /**
     * Update the server on this players' velocity.
     *
     * @param x        x velocity
     * @param y        y velocity
     * @param rotation rotation
     */
    public void updateVelocity(float x, float y, float rotation) {
        this.sendImmediately(new C2SPacketPlayerVelocity(x, y, rotation));
    }

    /**
     * Update the server on this players' velocity
     *
     * @param velocity velocity
     * @param rotation rotation
     */
    public void updateVelocity(Vector2 velocity, float rotation) {
        sendImmediately(new C2SPacketPlayerVelocity(velocity, rotation));
    }


    /**
     * Notify the server of this disconnect
     *
     * @param reason the reason or {@code null}
     */
    public void disconnect(String reason) {
        sendImmediately(new C2SPacketDisconnected(reason));
    }

    /**
     * Notify the server this clients world has loaded
     */
    public void updateWorldHasLoaded() {
        this.sendImmediately(new C2SPacketWorldLoaded());
    }

    /**
     * Attempt to join a world
     *
     * @param world    the world
     * @param username the username of this player
     * @param time     current client tick and or time
     */
    public void joinWorld(String world, String username, long time) {
        this.sendImmediately(new C2SPacketJoinWorld(world, username, time));
    }

    /**
     * Attempt to join a world.
     * unlike {@code joinWorld(world, username, time)} this method substitutes time for {@code System.currentTimeMillis}
     *
     * @param world    the world
     * @param username the username of this player
     */
    public void joinWorld(String world, String username) {
        this.sendImmediately(new C2SPacketJoinWorld(world, username, System.currentTimeMillis()));
    }

    /**
     * @return if connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * @return the alloc of this channel for sending packets
     */
    public ByteBufAllocator alloc() {
        return channel.alloc();
    }


    /**
     * Will send the provided {@code packet} to the send queue.
     * Used for situations where priority is low.
     *
     * @param packet the packet
     */
    public void sendToQueue(Packet packet) {
        packet.alloc(alloc());
        queue.add(packet);
    }


    /**
     * Send the provided {@code packet} immediately.
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

    /**
     * Flush the current packet queue.
     */
    public void flush() {
        if (queue.isEmpty()) return;

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

        public void handle(Packet packet) {
            if (isSync) {
                Gdx.app.postRunnable(() -> handler.accept(packet));
            } else {
                handler.accept(packet);
            }
        }

    }

}
