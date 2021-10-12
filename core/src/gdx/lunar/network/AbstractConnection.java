package gdx.lunar.network;

import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.PacketFactory;
import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.client.*;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

import java.util.function.Consumer;

/**
 * Represents a basic connection.
 */
public abstract class AbstractConnection implements ServerPacketHandler {

    /**
     * The current channel of this connection.
     */
    protected final Channel channel;
    protected LunarProtocol protocol;
    protected boolean isConnected;

    public AbstractConnection(Channel channel, LunarProtocol protocol) {
        this.channel = channel;
        this.protocol = protocol;
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
    public void sendPlayerPosition(Rotation rotation, float x, float y) {
        this.send(new CPacketPosition(channel.alloc(), rotation.ordinal(), x, y));
    }

    /**
     * Send player velocity packet
     *
     * @param rotation rotation
     * @param velX     X
     * @param velY     Y
     */
    public void sendPlayerVelocity(Rotation rotation, float velX, float velY) {
        this.send(new CPacketVelocity(channel.alloc(), velX, velY, rotation.ordinal()));
    }

    /**
     * Send disconnect packet
     */
    public void sendDisconnect() {
        this.send(new CPacketDisconnect(channel.alloc()));
    }

    /**
     * Send world loaded
     */
    public void sendWorldLoaded() {
        this.send(new CPacketWorldLoaded(channel.alloc()));
    }

    /**
     * Send join world
     *
     * @param world the world name
     */
    public void sendJoinWorld(String world) {
        this.send(new CPacketJoinWorld(channel.alloc(), world));
    }

    /**
     * Send set username of local player
     *
     * @param username the username
     */
    public void sendSetUsername(String username) {
        this.send(new CPacketSetProperties(channel.alloc(), username));
    }

    /**
     * Send a request to create a lobby.
     */
    public void sendCreateLobby() {
        this.send(new CPacketCreateLobby(channel.alloc()));
    }

    /**
     * Join a lobby by name
     *
     * @param lobbyName the name
     */
    public void sendJoinLobby(String lobbyName) {
        this.send(new CPacketJoinLobby(alloc(), lobbyName));
    }

    /**
     * Join a lobby by ID.
     *
     * @param lobbyId lobby id.
     */
    public void sendJoinLobby(int lobbyId) {
        this.send(new CPacketJoinLobby(alloc(), lobbyId));
    }

    /**
     * @return if this connection is connected in any way.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Disconnect via packets.
     */
    public void disconnect() {
        channel.writeAndFlush(new CPacketDisconnect(channel.alloc()));
    }

    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    public void send(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void close() {
        channel.close();
    }

}
