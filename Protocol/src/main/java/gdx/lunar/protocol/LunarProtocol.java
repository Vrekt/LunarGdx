package gdx.lunar.protocol;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.protocol.packet.server.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The base protocol
 * 10-12-2021: Allow multiple instances of the base protocol
 */
public class LunarProtocol {

    /**
     * Protocol version
     */
    private int protocolVersion = 1;

    /**
     * A map register of all server packets
     */
    private final Map<Integer, BiConsumer<ByteBuf, ServerPacketHandler>> server = new HashMap<>();

    /**
     * A map register of all client packets
     */
    private final Map<Integer, BiConsumer<ByteBuf, ClientPacketHandler>> client = new HashMap<>();

    /**
     * Map register of custom packets
     */
    private final Map<Integer, Consumer<ByteBuf>> custom = new HashMap<>();

    /**
     * Creates a new instance.
     *
     * @param initialize if {@code true} server and client (default) packets will be automatically added
     */
    public LunarProtocol(boolean initialize) {
        if (initialize) {
            initializeServer();
            initializeClient();
        }
    }

    public void initialize() {
        initializeServer();
        initializeClient();
    }

    /**
     * All pre-existing packets are registered with their respective {@link ClientPacketHandler}
     * If you wish to change this, you can do so. This must be done after calling {@code initialize} and
     * before any connection or protocol is used.
     *
     * @param pid     pid
     * @param handler handler
     */
    public void changeDefaultClientPacketHandlerFor(int pid, BiConsumer<ByteBuf, ClientPacketHandler> handler) {
        client.put(pid, handler);
    }

    /**
     * All pre-existing packets are registered with their respective {@link ServerPacketHandler}
     * If you wish to change this, you can do so. This must be done after calling {@code initialize} and
     * before any connection or protocol is used.
     *
     * @param pid     pid
     * @param handler handler
     */
    public void changeDefaultServerPacketHandlerFor(int pid, BiConsumer<ByteBuf, ServerPacketHandler> handler) {
        server.put(pid, handler);
    }

    /**
     * Add a client packet handler.
     *
     * @param pid     the pid
     * @param handler the handler.
     */
    public void addClientPacket(int pid, BiConsumer<ByteBuf, ClientPacketHandler> handler) {
        client.put(pid, handler);
    }

    /**
     * Add a server packet handler.
     *
     * @param pid     the pid
     * @param handler the handler.
     */
    public void addServerPacket(int pid, BiConsumer<ByteBuf, ServerPacketHandler> handler) {
        server.put(pid, handler);
    }

    /**
     * Register a custom packet handler
     *
     * @param pid      pid
     * @param consumer consumer
     */
    public void registerCustomPacket(int pid, Consumer<ByteBuf> consumer) {
        custom.put(pid, consumer);
    }

    /**
     * Initialize server side
     * <p>
     * Default server IDs start with a 99.
     */
    private void initializeServer() {
        server.put(SPacketDisconnect.PID, (buf, handler) -> SPacketDisconnect.handle(handler, buf));
        server.put(SPacketAuthentication.PID, (buf, handler) -> SPacketAuthentication.handle(handler, buf));
        server.put(SPacketCreatePlayer.PID, (buf, handler) -> SPacketCreatePlayer.handle(handler, buf));
        server.put(SPacketRemovePlayer.PID, (buf, handler) -> SPacketRemovePlayer.handle(handler, buf));
        server.put(SPacketPlayerPosition.PID, (buf, handler) -> SPacketPlayerPosition.handle(handler, buf));
        server.put(SPacketPlayerVelocity.PID, (buf, handler) -> SPacketPlayerVelocity.handle(handler, buf));
        server.put(SPacketJoinWorld.PID, (buf, handler) -> SPacketJoinWorld.handle(handler, buf));
        server.put(SPacketApplyEntityBodyForce.PID, (buf, handler) -> SPacketApplyEntityBodyForce.handle(handler, buf));
        server.put(SPacketSpawnEntity.PID, (buf, handler) -> SPacketSpawnEntity.handle(handler, buf));
        server.put(SPacketSpawnEntityDenied.PID, (buf, handler) -> SPacketSpawnEntityDenied.handle(handler, buf));
        server.put(SPacketSetEntityProperties.PID, (buf, handler) -> SPacketSetEntityProperties.handle(handler, buf));
        server.put(SPacketCreateLobby.PID, (buf, handler) -> SPacketCreateLobby.handle(handler, buf));
        server.put(SPacketJoinLobbyDenied.PID, (buf, handler) -> SPacketJoinLobbyDenied.handle(handler, buf));
        server.put(SPacketJoinLobby.PID, (buf, handler) -> SPacketJoinLobby.handle(handler, buf));
        server.put(SPacketEnterInstance.PID, (buf, handler) -> SPacketEnterInstance.handle(handler, buf));
        server.put(SPacketPlayerEnterInstance.PID, (buf, handler) -> SPacketPlayerEnterInstance.handle(handler, buf));
        server.put(SPacketWorldInvalid.PID, (buf, handler) -> SPacketWorldInvalid.handle(handler, buf));
    }

    /**
     * Initialize client side
     * Default client IDs start with a 88.
     */
    private void initializeClient() {
        client.put(881, (buf, handler) -> CPacketAuthentication.handle(handler, buf));
        client.put(882, (buf, handler) -> CPacketDisconnect.handle(handler, buf));
        client.put(883, (buf, handler) -> CPacketPosition.handle(handler, buf));
        client.put(884, (buf, handler) -> CPacketVelocity.handle(handler, buf));
        client.put(885, (buf, handler) -> CPacketJoinWorld.handle(handler, buf));
        client.put(886, (buf, handler) -> CPacketWorldLoaded.handle(handler, buf));
        client.put(887, (buf, handler) -> CPacketApplyEntityBodyForce.handle(handler, buf));
        client.put(888, (buf, handler) -> CPacketRequestSpawnEntity.handle(handler, buf));
        client.put(889, (buf, handler) -> CPacketSetProperties.handle(handler, buf));
        client.put(8810, (buf, handler) -> CPacketCreateLobby.handle(handler, buf));
        client.put(8811, (buf, handler) -> CPacketJoinLobby.handle(handler, buf));
        client.put(8812, (buf, handler) -> CPacketEnterInstance.handle(handler, buf));
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
     * Check if the provided {@code pid} if custom
     *
     * @param pid pid
     * @return {@code true} if so
     */
    public boolean isCustomPacket(int pid) {
        return custom.containsKey(pid);
    }

    /**
     * Handle a server packet
     * The provided {@code in} buffer should be released by decoder.
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param handler the handler
     * @param context local context
     */
    public void handleServerPacket(int pid, ByteBuf in, ServerPacketHandler handler, ChannelHandlerContext context) {
        try {
            if (isCustomPacket(pid)) {
                custom.get(pid).accept(in);
            } else if (isServerPacket(pid)) {
                server.get(pid).accept(in, handler);
            }
        } catch (Exception exception) {
            context.fireExceptionCaught(exception);
        }
    }

    /**
     * Handle a client packet
     * The provided {@code in} buffer should be released by the decoder.
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param handler the handler
     * @param context local context
     */
    public void handleClientPacket(int pid, ByteBuf in, ClientPacketHandler handler, ChannelHandlerContext context) {
        try {
            if (isCustomPacket(pid)) {
                custom.get(pid).accept(in);
            } else if (isClientPacket(pid)) {
                client.get(pid).accept(in, handler);
            }
        } catch (Exception exception) {
            context.fireExceptionCaught(exception);
        }
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void dispose() {
        server.clear();
        client.clear();
        custom.clear();
    }

}
