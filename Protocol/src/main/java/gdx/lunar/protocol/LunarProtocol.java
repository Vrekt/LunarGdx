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
 * The protocol.
 */
public final class LunarProtocol {

    /**
     * Protocol version
     */
    public static int protocolVersion = 1;

    /**
     * A map register of all server packets
     */
    private static final Map<Integer, BiConsumer<ByteBuf, ServerPacketHandler>> SERVER_PACKETS = new HashMap<>();

    /**
     * A map register of all client packets
     */
    private static final Map<Integer, BiConsumer<ByteBuf, ClientPacketHandler>> CLIENT_PACKETS = new HashMap<>();

    /**
     * Map register of custom packets
     */
    private static final Map<Integer, Consumer<ByteBuf>> CUSTOM_PACKETS = new HashMap<>();

    public static void initialize() {
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
    public static void changeDefaultClientPacketHandlerFor(int pid, BiConsumer<ByteBuf, ClientPacketHandler> handler) {
        CLIENT_PACKETS.put(pid, handler);
    }

    /**
     * All pre-existing packets are registered with their respective {@link ServerPacketHandler}
     * If you wish to change this, you can do so. This must be done after calling {@code initialize} and
     * before any connection or protocol is used.
     *
     * @param pid     pid
     * @param handler handler
     */
    public static void changeDefaultServerPacketHandlerFor(int pid, BiConsumer<ByteBuf, ServerPacketHandler> handler) {
        SERVER_PACKETS.put(pid, handler);
    }

    /**
     * Add a client packet handler.
     *
     * @param pid     the pid
     * @param handler the handler.
     */
    public static void addClientPacket(int pid, BiConsumer<ByteBuf, ClientPacketHandler> handler) {
        CLIENT_PACKETS.put(pid, handler);
    }

    /**
     * Add a server packet handler.
     *
     * @param pid     the pid
     * @param handler the handler.
     */
    public static void addServerPacket(int pid, BiConsumer<ByteBuf, ServerPacketHandler> handler) {
        SERVER_PACKETS.put(pid, handler);
    }

    /**
     * Register a custom packet handler
     *
     * @param pid      pid
     * @param consumer consumer
     */
    public static void registerCustomPacket(int pid, Consumer<ByteBuf> consumer) {
        CUSTOM_PACKETS.put(pid, consumer);
    }

    /**
     * Initialize server side
     */
    private static void initializeServer() {
        SERVER_PACKETS.put(SPacketDisconnect.PID, (buf, handler) -> SPacketDisconnect.handle(handler, buf));
        SERVER_PACKETS.put(SPacketAuthentication.PID, (buf, handler) -> SPacketAuthentication.handle(handler, buf));
        SERVER_PACKETS.put(SPacketCreatePlayer.PID, (buf, handler) -> SPacketCreatePlayer.handle(handler, buf));
        SERVER_PACKETS.put(SPacketRemovePlayer.PID, (buf, handler) -> SPacketRemovePlayer.handle(handler, buf));
        SERVER_PACKETS.put(SPacketPlayerPosition.PID, (buf, handler) -> SPacketPlayerPosition.handle(handler, buf));
        SERVER_PACKETS.put(SPacketPlayerVelocity.PID, (buf, handler) -> SPacketPlayerVelocity.handle(handler, buf));
        SERVER_PACKETS.put(SPacketJoinWorld.PID, (buf, handler) -> SPacketJoinWorld.handle(handler, buf));
        SERVER_PACKETS.put(SPacketBodyForce.PID, (buf, handler) -> SPacketBodyForce.handle(handler, buf));
    }

    /**
     * Initialize client side
     */
    private static void initializeClient() {
        CLIENT_PACKETS.put(CPacketAuthentication.PID, (buf, handler) -> CPacketAuthentication.handle(handler, buf));
        CLIENT_PACKETS.put(CPacketDisconnect.PID, (buf, handler) -> CPacketDisconnect.handle(handler, buf));
        CLIENT_PACKETS.put(CPacketPosition.PID, (buf, handler) -> CPacketPosition.handle(handler, buf));
        CLIENT_PACKETS.put(CPacketVelocity.PID, (buf, handler) -> CPacketVelocity.handle(handler, buf));
        CLIENT_PACKETS.put(CPacketJoinWorld.PID, (buf, handler) -> CPacketJoinWorld.handle(handler, buf));
        CLIENT_PACKETS.put(CPacketWorldLoaded.PID, (buf, handler) -> CPacketWorldLoaded.handle(handler, buf));
        CLIENT_PACKETS.put(CPacketBodyForce.PID, (buf, handler) -> CPacketBodyForce.handle(handler, buf));
    }

    /**
     * Check if the packet exists
     *
     * @param pid the packet ID
     * @return {@code true} if so
     */
    public static boolean isClientPacket(int pid) {
        return CLIENT_PACKETS.containsKey(pid);
    }

    /**
     * Check if the provided {@code pid} is server.
     *
     * @param pid the pid
     * @return {@code true} if so
     */
    public static boolean isServerPacket(int pid) {
        return SERVER_PACKETS.containsKey(pid);
    }

    /**
     * Check if the provided {@code pid} if custom
     *
     * @param pid pid
     * @return {@code true} if so
     */
    public static boolean isCustomPacket(int pid) {
        return CUSTOM_PACKETS.containsKey(pid);
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
    public static void handleServerPacket(int pid, ByteBuf in, ServerPacketHandler handler, ChannelHandlerContext context) {
        try {
            if (isCustomPacket(pid)) {
                CUSTOM_PACKETS.get(pid).accept(in);
            } else if (isServerPacket(pid)) {
                SERVER_PACKETS.get(pid).accept(in, handler);
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
    public static void handleClientPacket(int pid, ByteBuf in, ClientPacketHandler handler, ChannelHandlerContext context) {
        try {
            if (isCustomPacket(pid)) {
                CUSTOM_PACKETS.get(pid).accept(in);
            } else if (isClientPacket(pid)) {
                CLIENT_PACKETS.get(pid).accept(in, handler);
            }
        } catch (Exception exception) {
            context.fireExceptionCaught(exception);
        }
    }

}
