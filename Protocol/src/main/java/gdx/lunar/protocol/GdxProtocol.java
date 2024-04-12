package gdx.lunar.protocol;

import gdx.lunar.protocol.handlers.ClientPacketHandler;
import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.protocol.packet.server.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Basic protocol handler and interface.
 * Client Packet IDs (22xx)
 * Server Packet IDs (11xx)
 */
public class GdxProtocol {

    private final int protocolVersion;
    private final String protocolName;
    // max frame length allowed of a packet
    // Depending on how complex/and or large packets can get
    private int maxPacketFrameLength = 65536;


    private final Map<Integer, BiConsumer<ByteBuf, ServerPacketHandler>> server = new HashMap<>();
    private final Map<Integer, BiConsumer<ByteBuf, ClientPacketHandler>> client = new HashMap<>();
    private final Map<Integer, Consumer<ByteBuf>> custom = new HashMap<>();

    /**
     * Initialize a new protocol.
     *
     * @param protocolVersion    the current protocol version
     * @param protocolName       the protocol name
     * @param initializeDefaults if default packet handlers should be assigned.
     */
    public GdxProtocol(int protocolVersion, String protocolName, boolean initializeDefaults) {
        this.protocolVersion = protocolVersion;
        this.protocolName = protocolName;
        if (initializeDefaults) initializeDefaults();
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getProtocolName() {
        return protocolName;
    }

    /**
     * Set the max frame length allowed for packets
     *
     * @param maxPacketFrameLength the length
     */
    public void setMaxPacketFrameLength(int maxPacketFrameLength) {
        this.maxPacketFrameLength = maxPacketFrameLength;
    }

    public int getMaxPacketFrameLength() {
        return maxPacketFrameLength;
    }

    /**
     * @return a map of all server packet handlers
     */
    public Map<Integer, BiConsumer<ByteBuf, ServerPacketHandler>> getServerHandlers() {
        return server;
    }

    /**
     * @return a map of all client packet handlers
     */
    public Map<Integer, BiConsumer<ByteBuf, ClientPacketHandler>> getClientHandlers() {
        return client;
    }

    /**
     * @return a map of all custom packet handlers
     */
    public Map<Integer, Consumer<ByteBuf>> getCustomHandlers() {
        return custom;
    }

    /**
     * Initialize default packet handlers
     */
    private void initializeDefaults() {
        initializeClientHandlers();
        initializeServerHandlers();
    }

    private void initializeServerHandlers() {
        server.put(S2CPacketAuthenticate.PACKET_ID, (buf, handler) -> S2CPacketAuthenticate.handle(handler, buf));
        server.put(S2CPacketCreatePlayer.PACKET_ID, (buf, handler) -> S2CPacketCreatePlayer.handle(handler, buf));
        server.put(S2CPacketDisconnected.PACKET_ID, (buf, handler) -> S2CPacketDisconnected.handle(handler, buf));
        server.put(S2CPacketJoinWorld.PACKET_ID, (buf, handler) -> S2CPacketJoinWorld.handle(handler, buf));
        server.put(S2CPacketPing.PACKET_ID, (buf, handler) -> S2CPacketPing.handle(handler, buf));
        server.put(S2CPacketPlayerPosition.PACKET_ID, (buf, handler) -> S2CPacketPlayerPosition.handle(handler, buf));
        server.put(S2CPacketPlayerVelocity.PACKET_ID, (buf, handler) -> S2CPacketPlayerVelocity.handle(handler, buf));
        server.put(S2CPacketRemovePlayer.PACKET_ID, (buf, handler) -> S2CPacketRemovePlayer.handle(handler, buf));
        server.put(S2CPacketSetEntityProperties.PACKET_ID, (buf, handler) -> S2CPacketSetEntityProperties.handle(handler, buf));
        server.put(S2CPacketStartGame.PACKET_ID, (buf, handler) -> S2CPacketStartGame.handle(handler, buf));
        server.put(S2CPacketWorldInvalid.PACKET_ID, (buf, handler) -> S2CPacketWorldInvalid.handle(handler, buf));
    }

    private void initializeClientHandlers() {
        client.put(C2SPacketAuthenticate.PACKET_ID, (buf, handler) -> C2SPacketAuthenticate.handle(handler, buf));
        client.put(C2SPacketDisconnected.PACKET_ID, (buf, handler) -> C2SPacketDisconnected.handle(handler, buf));
        client.put(C2SPacketJoinWorld.PACKET_ID, (buf, handler) -> C2SPacketJoinWorld.handle(handler, buf));
        client.put(C2SPacketPing.PACKET_ID, (buf, handler) -> C2SPacketPing.handle(handler, buf));
        client.put(C2SPacketPlayerPosition.PACKET_ID, (buf, handler) -> C2SPacketPlayerPosition.handle(handler, buf));
        client.put(C2SPacketPlayerVelocity.PACKET_ID, (buf, handler) -> C2SPacketPlayerVelocity.handle(handler, buf));
        client.put(C2SPacketWorldLoaded.PACKET_ID, (buf, handler) -> C2SPacketWorldLoaded.handle(handler, buf));
    }

    /**
     * Check if the packet exists
     *
     * @param pid the packet ID
     * @return {@code true} if so
     */
    public boolean isClientPacket(int pid) {
        return client.containsKey(pid);
    }

    /**
     * Check if the provided {@code pid} is server.
     *
     * @param pid the pid
     * @return {@code true} if so
     */
    public boolean isServerPacket(int pid) {
        return server.containsKey(pid);
    }

    /**
     * Check if the provided {@code pid} is custom.
     *
     * @param pid the pid
     * @return {@code true} if so
     */
    public boolean isCustomPacket(int pid) {
        return custom.containsKey(pid);
    }

    /**
     * Change a server packet handler
     * This must be invoked after the protocol is initialized (only IF, {@code initializeDefaults} in the constructor is {@code true})
     *
     * @param pid     the pid
     * @param handler the new handler
     */
    public void changeServerPacketHandler(int pid, BiConsumer<ByteBuf, ServerPacketHandler> handler) {
        server.put(pid, handler);
    }

    /**
     * Change a client packet handler
     * This must be invoked after the protocol is initialized (only IF, {@code initializeDefaults} in the constructor is {@code true})
     *
     * @param pid     the pid
     * @param handler the new handler
     */
    public void changeClientPacketHandler(int pid, BiConsumer<ByteBuf, ClientPacketHandler> handler) {
        client.put(pid, handler);
    }

    /**
     * Register a new client packet.
     * Upon receiving the packet it should be passed through the generic {@code handle} method of {@link ClientPacketHandler}
     *
     * @param pid     the id
     * @param handler the handler
     */
    public void registerClientPacket(int pid, BiConsumer<ByteBuf, ClientPacketHandler> handler) {
        client.put(pid, handler);
    }

    /**
     * Register a new server packet.
     * Upon receiving the packet it should be passed through the generic {@code handle} method of {@link ServerPacketHandler}
     *
     * @param pid     the id
     * @param handler the handler
     */
    public void registerServerPacket(int pid, BiConsumer<ByteBuf, ServerPacketHandler> handler) {
        server.put(pid, handler);
    }

    /**
     * Register a custom packet
     *
     * @param pid the pid
     * @param buf the consumer
     */
    public void registerPacket(int pid, Consumer<ByteBuf> buf) {
        custom.put(pid, buf);
    }

    /**
     * Handle a server packet
     * The provided {@code in} buffer should be released by decoder.
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param handler the handler
     * @param context the context (allowed to be null)
     */
    public void handleServerPacket(int pid, ByteBuf in, ServerPacketHandler handler, ChannelHandlerContext context) {
        try {
            server.get(pid).accept(in, handler);
        } catch (Exception exception) {
            if (context != null) context.fireExceptionCaught(exception);
        }
    }

    /**
     * Handle a client packet
     * The provided {@code in} buffer should be released by the decoder.
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param handler the handler
     * @param context the context (allowed to be null)
     */
    public void handleClientPacket(int pid, ByteBuf in, ClientPacketHandler handler, ChannelHandlerContext context) {
        try {
            client.get(pid).accept(in, handler);
        } catch (Exception exception) {
            if (context != null) context.fireExceptionCaught(exception);
        }
    }

    /**
     * Handle a custom packet
     * The provided {@code in} buffer should be released by the decoder.
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param context the context (allowed to be null)
     */
    public void handleCustomPacket(int pid, ByteBuf in, ChannelHandlerContext context) {
        try {
            custom.get(pid).accept(in);
        } catch (Exception exception) {
            if (context != null) context.fireExceptionCaught(exception);
        }
    }

    public void dispose() {
        client.clear();
        server.clear();
    }

}
