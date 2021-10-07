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

    /**
     * Handle apply body force
     *
     * @param packet packet
     */
    void handleBodyForce(SPacketBodyForce packet);

    /**
     * Spawn an entity
     *
     * @param packet the packet
     */
    void handleSpawnEntity(SPacketSpawnEntity packet);

    /**
     * A request to spawn an entity was denied
     *
     * @param packet the packet
     */
    void handleSpawnEntityDenied(SPacketSpawnEntityDenied packet);

    /**
     * Handle setting properties of an entity
     *
     * @param packet the packet
     */
    void handleSetEntityProperties(SPacketSetEntityProperties packet);

    /**
     * Handle creating a lobby (or denial)
     *
     * @param packet the packet
     */
    void handleCreateLobby(SPacketCreateLobby packet);

    /**
     * A request to join a lobby was denied
     *
     * @param packet the packet
     */
    void handleJoinLobbyDenied(SPacketJoinLobbyDenied packet);

    /**
     * Handle joining a lobby
     *
     * @param packet the packet
     */
    void handleJoinLobby(SPacketJoinLobby packet);

}
