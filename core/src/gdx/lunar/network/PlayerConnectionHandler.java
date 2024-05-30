package gdx.lunar.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.protocol.GdxProtocol;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.*;
import io.netty.channel.Channel;
import lunar.shared.entity.player.adapter.NetworkPlayerAdapter;

/**
 * A default handler for quick use.
 */
public class PlayerConnectionHandler extends AbstractConnectionHandler {

    public PlayerConnectionHandler(Channel channel, GdxProtocol protocol) {
        super(channel, protocol);
    }

    /**
     * Handle a packet if it's registered in the handlers list
     *
     * @param packet the packet
     * @return {@code true} if this packet was handled.
     */
    protected boolean checkRegisteredHandlers(Packet packet) {
        if (handlers.containsKey(packet.getId())) {
            handlers.get(packet.getId()).handle(packet);
            return true;
        }
        return false;
    }

    /**
     * Check if the provided id is the local player
     * Used for avoiding packets the client sent and received back
     *
     * @param id the ID
     * @return {@code  true} if so
     */
    protected boolean isLocalPlayer(int id) {
        return id == player.getEntityId();
    }

    protected boolean doesPlayerExistInWorld(int entityId) {
        return player.isInWorld() && player.getWorld().hasPlayer(entityId);
    }

    /**
     * Check if the provided packet should be handled
     *
     * @param id     the entity ID
     * @param packet the packet
     * @return {@code true} if already handled, or should not be handled.
     */
    protected boolean shouldHandle(int id, Packet packet) {
        return !isLocalPlayer(id) && checkRegisteredHandlers(packet);
    }

    @Override
    public void handleAuthentication(S2CPacketAuthenticate packet) {
        if (checkRegisteredHandlers(packet)) return;
        if (!packet.isAuthenticationSuccessful()) {
            this.close();
        }
    }

    @Override
    public void handleDisconnect(S2CPacketDisconnected packet) {
        if (checkRegisteredHandlers(packet)) return;
        this.close();
    }

    @Override
    public void handleCreatePlayer(S2CPacketCreatePlayer packet) {
        if (shouldHandle(packet.getEntityId(), packet)) return;

        final NetworkPlayerAdapter player = new NetworkPlayerAdapter(true);
        player.loadEntity();
        player.disablePlayerCollision(true);
        player.setProperties(packet.getUsername(), packet.getId());
        player.spawnInWorld(this.player.getWorld(), new Vector2(packet.getX(), packet.getY()));
    }

    @Override
    public void handleRemovePlayer(S2CPacketRemovePlayer packet) {
        if (shouldHandle(packet.getEntityId(), packet)) return;

        if (doesPlayerExistInWorld(packet.getEntityId())) {
            player.getWorld().removePlayerInWorld(packet.getEntityId(), true);
        }
    }

    @Override
    public void handlePlayerPosition(S2CPacketPlayerPosition packet) {
        if (shouldHandle(packet.getEntityId(), packet)) return;
        if (doesPlayerExistInWorld(packet.getEntityId())) {
            player.getWorld().updatePlayerPositionInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
        }
    }

    @Override
    public void handlePlayerVelocity(S2CPacketPlayerVelocity packet) {
        if (shouldHandle(packet.getEntityId(), packet)) return;
        if (doesPlayerExistInWorld(packet.getEntityId())) {
            player.getWorld().updatePlayerVelocityInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
        }
    }

    @Override
    public void handlePing(S2CPacketPing packet) {
        checkRegisteredHandlers(packet);
    }

    @Override
    public void handleJoinWorld(S2CPacketJoinWorld packet) {
        checkRegisteredHandlers(packet);
    }

    @Override
    public void handleWorldInvalid(S2CPacketWorldInvalid packet) {
        checkRegisteredHandlers(packet);
    }

    @Override
    public void handleSetEntityProperties(S2CPacketSetEntityProperties packet) {
        checkRegisteredHandlers(packet);
    }

    @Override
    public void handleStartGame(S2CPacketStartGame packet) {
        if (checkRegisteredHandlers(packet)) return;

        if (packet.hasPlayers() && player.isInWorld()) {
            for (S2CPacketStartGame.BasicServerPlayer packetPlayer : packet.getPlayers()) {
                Gdx.app.log("PlayerConnectionHandler", "New player joining %s".formatted(packetPlayer.username));

                final NetworkPlayerAdapter player = new NetworkPlayerAdapter(true);
                player.loadEntity();
                player.disablePlayerCollision(true);
                player.setProperties(packetPlayer.username, packetPlayer.entityId);
                player.spawnInWorld(this.player.getWorld(), packetPlayer.position);
            }
        }
    }
}
