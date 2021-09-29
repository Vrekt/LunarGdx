package gdx.lunar.protocol.handler;

import gdx.lunar.protocol.packet.client.*;

/**
 * Handles packets that were sent from the client.
 */
public interface ClientPacketHandler {

    /**
     * Handle the client's authentication packet.
     *
     * @param packet the packet
     */
    void handleAuthentication(CPacketAuthentication packet);

    /**
     * Handle client disconnect
     *
     * @param packet the packet
     */
    void handleDisconnect(CPacketDisconnect packet);

    /**
     * Handle a players position update
     *
     * @param packet the packet
     */
    void handlePlayerPosition(CPacketPosition packet);

    /**
     * Handle a players velocity
     *
     * @param packet the packet
     */
    void handlePlayerVelocity(CPacketVelocity packet);

    /**
     * Handle a join world request.
     *
     * @param packet the packet
     */
    void handleJoinWorld(CPacketJoinWorld packet);

    /**
     * Handle world loaded
     *
     * @param packet the packet
     */
    void handleWorldLoaded(CPacketWorldLoaded packet);

    /**
     * Handle a body force
     *
     * @param packet the placket
     */
    void handleBodyForce(CPacketBodyForce packet);

}
