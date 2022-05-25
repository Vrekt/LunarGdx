package gdx.lunar.network;

import gdx.lunar.network.types.ConnectionOption;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.*;
import gdx.lunar.world.LunarWorld;
import io.netty.channel.Channel;

/**
 * Default implementation of {@link gdx.lunar.network.AbstractConnection}
 */
public class PlayerConnection extends AbstractConnection {

    protected boolean authenticationFailed = false;

    public PlayerConnection(Channel channel, LunarProtocol protocol) {
        super(channel, protocol);
    }

    public boolean isAuthenticationFailed() {
        return authenticationFailed;
    }

    protected boolean inWorld() {
        return local.getInstance().worldIn != null;
    }

    protected LunarWorld<?, ?, ?> getWorldIn() {
        return local.getInstance().worldIn;
    }

    /**
     * Verify local player is in a world and a network player exists
     *
     * @param id network player ID
     * @return {@code true} if so
     */
    protected boolean verifyPlayerExists(int id) {
        return inWorld() && getWorldIn().hasNetworkPlayer(id);
    }

    /**
     * Handle an incoming packet to the handlers list.
     *
     * @param handler handler
     * @param packet  packet
     * @return {@code true} if handled.
     */
    protected boolean handle(ConnectionOption handler, Packet packet) {
        if (handlers.containsKey(handler)) {
            handlers.get(handler).accept(packet);
            return true;
        }
        return false;
    }

    @Override
    public void handleAuthentication(SPacketAuthentication packet) {
        if (!packet.isAllowed()) {
            this.authenticationFailed = true;
            this.close();
        } else {
            this.authenticationFailed = false;
        }
    }

    @Override
    public void handleDisconnect(SPacketDisconnect packet) {
        handle(ConnectionOption.HANDLE_DISCONNECT, packet);
        this.close();
    }

    @Override
    public void handleCreatePlayer(SPacketCreatePlayer packet) {
        handle(ConnectionOption.HANDLE_PLAYER_JOIN, packet);
    }

    @Override
    public void handleRemovePlayer(SPacketRemovePlayer packet) {
        handle(ConnectionOption.HANDLE_PLAYER_LEAVE, packet);
    }

    @Override
    public void handlePlayerPosition(SPacketPlayerPosition packet) {
        handle(ConnectionOption.HANDLE_PLAYER_POSITION, packet);
    }

    @Override
    public void handlePlayerVelocity(SPacketPlayerVelocity packet) {
        handle(ConnectionOption.HANDLE_PLAYER_VELOCITY, packet);
    }

    @Override
    public void handleJoinWorld(SPacketJoinWorld packet) {
        handle(ConnectionOption.HANDLE_JOIN_WORLD, packet);
    }

    @Override
    public void handleEntityBodyForce(SPacketApplyEntityBodyForce packet) {
        handle(ConnectionOption.HANDLE_PLAYER_FORCE, packet);
    }

    @Override
    public void handleSpawnEntity(SPacketSpawnEntity packet) {

    }

    @Override
    public void handleSpawnEntityDenied(SPacketSpawnEntityDenied packet) {

    }

    @Override
    public void handleSetEntityProperties(SPacketSetEntityProperties packet) {
        handle(ConnectionOption.HANDLE_SET_ENTITY_PROPERTIES, packet);
    }

    @Override
    public void handleWorldInvalid(SPacketWorldInvalid packet) {
        handle(ConnectionOption.HANDLE_WORLD_INVALID, packet);
    }

    @Override
    public void handleCreateLobby(SPacketCreateLobby packet) {

    }

    @Override
    public void handleJoinLobbyDenied(SPacketJoinLobbyDenied packet) {

    }

    @Override
    public void handleJoinLobby(SPacketJoinLobby packet) {

    }

    @Override
    public void handleEnterInstance(SPacketEnterInstance packet) {

    }

    @Override
    public void handlePlayerEnterInstance(SPacketPlayerEnterInstance packet) {

    }
}
