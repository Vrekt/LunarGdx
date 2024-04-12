package gdx.lunar.protocol.handlers;

import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.client.*;

/**
 * A base implementation of a handler for client packets
 */
public interface ClientPacketHandler {

    /**
     * Handle the {@link C2SPacketAuthenticate}
     *
     * @param packet the packet
     */
    void handleAuthentication(C2SPacketAuthenticate packet);

    /**
     * Handle the {@link C2SPacketDisconnected}
     *
     * @param packet the packet
     */
    void handleDisconnected(C2SPacketDisconnected packet);

    /**
     * Handle the {@link C2SPacketPing}
     *
     * @param packet the packet
     */
    void handlePing(C2SPacketPing packet);

    /**
     * Handle the {@link C2SPacketJoinWorld}
     *
     * @param packet the packet
     */
    void handleJoinWorld(C2SPacketJoinWorld packet);

    /**
     * Handle the {@link C2SPacketWorldLoaded}
     *
     * @param packet the packet
     */
    void handleWorldLoaded(C2SPacketWorldLoaded packet);

    /**
     * Handle the {@link C2SPacketPlayerPosition}
     *
     * @param packet the packet
     */
    void handlePlayerPosition(C2SPacketPlayerPosition packet);

    /**
     * Handle the {@link C2SPacketPlayerVelocity}
     *
     * @param packet the packet
     */
    void handlePlayerVelocity(C2SPacketPlayerVelocity packet);

    /**
     * Handle any
     *
     * @param packet the packet
     */
    default void handle(Packet packet) {

    }

}
