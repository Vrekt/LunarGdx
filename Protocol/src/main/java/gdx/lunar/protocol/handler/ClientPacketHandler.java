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
    void handleBodyForce(CPacketApplyEntityBodyForce packet);

    /**
     * Handle a request to spawn an entity in the world
     *
     * @param packet the packet
     */
    void handleRequestSpawnEntity(CPacketRequestSpawnEntity packet);

    /**
     * Handle setting entity/player properties
     *
     * @param packet packet
     */
    void handleSetProperties(CPacketSetProperties packet);

    /**
     * Handle creating a lobby.
     *
     * @param packet the packet
     */
    void handleCreateLobby(CPacketCreateLobby packet);

    /**
     * Handle a request to join a lobby
     *
     * @param packet the packet
     */
    void handleJoinLobby(CPacketJoinLobby packet);

    /**
     * Handle a request to create a networked tile
     *
     * @param packet the packet
     */
    void handleNetworkTile(CPacketNetworkedTile packet);

    /**
     * Handle a request to enter an instance
     *
     * @param packet the packet
     */
    void handleEnterInstance(CPacketEnterInstance packet);

    /**
     * Handle ping time packet
     *
     * @param packet the packet
     */
    void handlePing(CPacketPing packet);

}
