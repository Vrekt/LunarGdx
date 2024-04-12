package gdx.lunar.protocol.handlers;

import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.*;

/**
 * Represents a packet handler handling packets from the server
 */
public interface ServerPacketHandler {

    /**
     * Handle the {@link S2CPacketAuthenticate}
     *
     * @param packet the packet
     */
    void handleAuthentication(S2CPacketAuthenticate packet);

    /**
     * Handle the {@link S2CPacketDisconnected}
     *
     * @param packet the packet
     */
    void handleDisconnect(S2CPacketDisconnected packet);

    /**
     * Handle the {@link S2CPacketPing}
     *
     * @param packet the packet
     */
    void handlePing(S2CPacketPing packet);

    /**
     * Handle the {@link S2CPacketWorldInvalid}
     *
     * @param packet the packet
     */
    void handleJoinWorld(S2CPacketJoinWorld packet);

    /**
     * Handle the {@link S2CPacketWorldInvalid}
     *
     * @param packet the packet
     */
    void handleWorldInvalid(S2CPacketWorldInvalid packet);

    /**
     * Handle the {@link S2CPacketSetEntityProperties}
     *
     * @param packet the packet
     */
    void handleSetEntityProperties(S2CPacketSetEntityProperties packet);

    /**
     * Handle the {@link S2CPacketCreatePlayer}
     *
     * @param packet the packet
     */
    void handleCreatePlayer(S2CPacketCreatePlayer packet);

    /**
     * Handle the {@link S2CPacketRemovePlayer}
     *
     * @param packet the packet
     */
    void handleRemovePlayer(S2CPacketRemovePlayer packet);

    /**
     * Handle the {@link S2CPacketPlayerPosition}
     *
     * @param packet the packet
     */
    void handlePlayerPosition(S2CPacketPlayerPosition packet);

    /**
     * Handle the {@link S2CPacketPlayerVelocity}
     *
     * @param packet the packet
     */
    void handlePlayerVelocity(S2CPacketPlayerVelocity packet);

    /**
     * Handle the {@link S2CPacketStartGame}
     *
     * @param packet the packet
     */
    void handleStartGame(S2CPacketStartGame packet);

    /**
     * Handle any
     *
     * @param packet the packet
     */
    default void handle(Packet packet) {

    }

}
