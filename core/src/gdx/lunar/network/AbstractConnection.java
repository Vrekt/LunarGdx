package gdx.lunar.network;

import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.network.handlers.ConnectionHandlers;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.PacketFactory;
import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.client.*;
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

    protected LunarEntityPlayer local;

    protected final Channel channel;
    protected LunarProtocol protocol;
    protected boolean isConnected;

    // packet flush interval.
    // default is 50 ms
    protected float updateInterval = .05f;
    protected long lastUpdate = System.currentTimeMillis();

    // queue of packets
    protected final ConcurrentLinkedQueue<Packet> queue = new ConcurrentLinkedQueue<>();
    protected final ExecutorService single = Executors.newCachedThreadPool();

    protected final Map<ConnectionHandlers, Consumer<Packet>> handlers = new HashMap<>();

    public AbstractConnection(Channel channel, LunarProtocol protocol) {
        this.channel = channel;
        this.protocol = protocol;
    }

    public void setLocalPlayer(LunarEntityPlayer local) {
        this.local = local;
    }

    /**
     * Set the protocol to use
     * 10-12-2021: Allow use of diff protocols
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
     *
     * @param handler the handler
     * @param c       the consumer
     */
    public void registerHandler(ConnectionHandlers handler, Consumer<Packet> c) {
        handlers.put(handler, c);
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
    public void updatePosition(Rotation rotation, float x, float y) {
        this.send(new CPacketPosition(rotation.ordinal(), x, y));
    }

    /**
     * Send player velocity packet
     *
     * @param rotation rotation
     * @param velX     X
     * @param velY     Y
     */
    public void updateVelocity(Rotation rotation, float velX, float velY) {
        this.send(new CPacketVelocity(rotation.ordinal(), velX, velY));
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

    public void createLobby() {
        this.send(new CPacketCreateLobby());
    }

    /**
     * Join a lobby by name
     *
     * @param lobbyName the name
     */
    public void sendJoinLobby(String lobbyName) {
        //  this.send(new CPacketJoinLobby(alloc(), lobbyName));
    }

    /**
     * Join a lobby by ID.
     *
     * @param lobbyId lobby id.
     */
    public void sendJoinLobby(int lobbyId) {
        //   this.send(new CPacketJoinLobby(alloc(), lobbyId));
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
        if (System.currentTimeMillis() - lastUpdate >= updateInterval * 1000) {
            single.execute(this::flush);
            lastUpdate = System.currentTimeMillis();
        }
    }

    protected void flush() {
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
}
