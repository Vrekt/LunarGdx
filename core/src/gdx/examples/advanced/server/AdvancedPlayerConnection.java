package gdx.examples.advanced.server;

import com.badlogic.gdx.Gdx;
import gdx.examples.advanced.AdvancedExampleMain;
import gdx.examples.advanced.entity.NetworkPlayer;
import gdx.examples.advanced.entity.Player;
import gdx.examples.advanced.packet.MyCustomPositionPacketServer;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.packet.server.*;
import io.netty.channel.Channel;

public class AdvancedPlayerConnection extends AbstractConnection {

    private final Player player;
    private final AdvancedExampleMain main;

    public AdvancedPlayerConnection(Channel channel, Player player, AdvancedExampleMain main) {
        super(channel);

        this.player = player;
        this.main = main;
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
            main.notifyNoAuth();
            this.close();
        }
    }

    @Override
    public void handleCreatePlayer(SPacketCreatePlayer packet) {
        if (packet.getEntityId() == player.getEntityId()) return;

        final NetworkPlayer player = new NetworkPlayer(packet.getEntityId());
        player.setUsername(packet.getUsername());

        Gdx.app.postRunnable(() -> {
            player.spawnEntityInWorld(this.player.getWorldIn(), packet.getX(), packet.getY());
            main.handlePlayerJoin(player);
        });
    }

    @Override
    public void handleRemovePlayer(SPacketRemovePlayer packet) {
        if (player.getWorldIn() == null) return;

        Gdx.app.postRunnable(() -> {
            main.handlePlayerLeave((NetworkPlayer) player.getWorldIn().getPlayers().get(packet.entityId()));
            player.getWorldIn().removePlayerFromWorld(packet.entityId());
        });
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
            main.notifyCantJoinWorld();
        }
    }

    public void handleCustomPacket(MyCustomPositionPacketServer packet) {

    }

}
