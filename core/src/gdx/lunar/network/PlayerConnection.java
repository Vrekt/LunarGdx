package gdx.lunar.network;

import com.badlogic.gdx.Gdx;
import gdx.lunar.Lunar;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;
import gdx.lunar.entity.player.impl.LunarNetworkPlayer;
import gdx.lunar.protocol.packet.Packet;
import gdx.lunar.protocol.packet.server.*;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a player connection to the server.
 */
public class PlayerConnection extends AbstractConnection {

    private final Lunar lunar;
    private LunarEntityPlayer player;

    private Consumer<LunarNetworkEntityPlayer> joinWorldListener;
    private final Map<Integer, Consumer<Packet>> packetHandlers = new HashMap<>();

    public PlayerConnection(Lunar lunar, Channel channel) {
        super(channel);

        this.lunar = lunar;
    }

    public void setPlayer(LunarEntityPlayer player) {
        this.player = player;
    }

    /**
     * Set the join world listener. This is invoked once other network players join the players world.
     *
     * @param joinWorldListener the consumer
     */
    public void setJoinWorldListener(Consumer<LunarNetworkEntityPlayer> joinWorldListener) {
        this.joinWorldListener = joinWorldListener;
    }

    @Override
    public void handleDisconnect(SPacketDisconnect packet) {
        this.close();
    }

    @Override
    public void handleAuthentication(SPacketAuthentication packet) {
        if (packet.isAllowed()) {
            System.err.println("Successfully authenticated with remote server.");
        } else {
            System.err.println("Failed to authenticate with server: " + packet.getNotAllowedReason());
            this.close();
        }
    }

    @Override
    public void handleCreatePlayer(SPacketCreatePlayer packet) {
        if (packet.getEntityId() == player.getEntityId()) return;

        // TODO: Maybe default global rotation.
        System.err.println("Spawning a new player with eid " + packet.getEntityId() + " and username " + packet.getUsername());
        final LunarNetworkPlayer player = new LunarNetworkPlayer(
                packet.getEntityId(),
                lunar.getPlayerProperties().scale,
                lunar.getPlayerProperties().width,
                lunar.getPlayerProperties().height,
                Rotation.FACING_UP);

        player.setUsername(packet.getUsername());
        Gdx.app.postRunnable(() -> {
            player.spawnEntityInWorld(this.player.getWorldIn(), packet.getX(), packet.getY());
            this.joinWorldListener.accept(player);
        });
    }

    @Override
    public void handleRemovePlayer(SPacketRemovePlayer packet) {
        if (player.getWorldIn() == null) return;

        Gdx.app.postRunnable(() -> player.getWorldIn().removePlayerFromWorld(packet.entityId()));
        System.err.println("Removed player: " + packet.entityId());
    }

    @Override
    public void handlePlayerPosition(SPacketPlayerPosition packet) {
        if (player.getWorldIn() == null) return;
        player.getWorldIn().updatePlayerPosition(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

    @Override
    public void handlePlayerVelocity(SPacketPlayerVelocity packet) {
        if (player.getWorldIn() == null) return;
        player.getWorldIn().updatePlayerVelocity(packet.getEntityId(), packet.getVelocityX(), packet.getVelocityY(), packet.getRotation());
    }

    @Override
    public void handleJoinWorld(SPacketJoinWorld packet) {
        if (packet.isAllowed()) {
            System.err.println("Allowed to join requested world.");
        } else {
            System.err.println("Failed to join the requested world because: " + packet.getNotAllowedReason());
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();
    }

}
