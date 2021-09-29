package gdx.lunar.protocol.handler;

import gdx.lunar.protocol.packet.server.*;

/**
 * Handles packets that were sent from the server.
 */
public interface ServerPacketHandler {

    /**
     * Handle server disconnection
     *
     * @param packet the packet
     */
    void handleDisconnect(SPacketDisconnect packet);

    /**
     * Handle authentication response.
     *
     * @param packet the packet
     */
    void handleAuthentication(SPacketAuthentication packet);

    /**
     * Handle spawning a new player
     *
     * @param packet the packet
     */
    void handleCreatePlayer(SPacketCreatePlayer packet);

    /**
     * Handle removing a player entity
     *
     * @param packet the packet
     */
    void handleRemovePlayer(SPacketRemovePlayer packet);

    /**
     * Handle a player position update
     *
     * @param packet the packet
     */
    void handlePlayerPosition(SPacketPlayerPosition packet);

    /**
     * Handle a players velocity
     *
     * @param packet the packet
     */
    void handlePlayerVelocity(SPacketPlayerVelocity packet);

    /**
     * Handle the join world response
     *
     * @param packet the packet
     */
    void handleJoinWorld(SPacketJoinWorld packet);

}
