package gdx.lunar.server.network;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.protocol.packet.server.SPacketAuthentication;
import gdx.lunar.protocol.packet.server.SPacketBodyForce;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import gdx.lunar.server.LunarServer;
import gdx.lunar.server.game.entity.player.Player;
import gdx.lunar.server.world.World;
import io.netty.channel.Channel;

/**
 * Represents the default player connection handler.
 */
public final class PlayerConnection extends AbstractConnection implements ClientPacketHandler {

    /**
     * The player who owns this connection.
     */
    private Player player;
    private boolean disconnected;

    public PlayerConnection(Channel channel) {
        super(channel);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void handleAuthentication(CPacketAuthentication packet) {
        System.err.println("Attempting to authenticate new client.");

        if (packet.getProtocolVersion() != LunarProtocol.protocolVersion) {
            // invalid protocol version, not allowed.
            send(new SPacketAuthentication(alloc(), false, "Outdated protocol version!"));
        } else {
            System.err.println("New connection successfully authenticated.");
            send(new SPacketAuthentication(alloc(), true, null));
        }
    }

    @Override
    public void handleDisconnect(CPacketDisconnect packet) {
        this.disconnected = true;

        System.err.println("Player disconnected.");

        if (player != null) {
            if (player.getWorld() != null) player.getWorld().removePlayerInWorld(player);
            player.getServer().handlePlayerDisconnect(player);
        }
        disconnect();
    }

    @Override
    public void handlePlayerPosition(CPacketPosition packet) {
        if (player != null && player.getWorld() != null) {
            player.getWorld().handlePlayerPosition(player, packet.getX(), packet.getY(), packet.getRotation());
        }
    }

    @Override
    public void handlePlayerVelocity(CPacketVelocity packet) {
        if (player != null && player.getWorld() != null) {
            player.getWorld().handlePlayerVelocity(player, packet.getVelocityX(), packet.getVelocityY(), packet.getRotation());
        }
    }

    @Override
    public void handleJoinWorld(CPacketJoinWorld packet) {
        final World world = LunarServer.getServer().getWorldManager().getWorld(packet.getWorldName());
        if (world == null) {
            send(new SPacketJoinWorld(alloc(), false, "Unknown world.", -1));
        } else if (world.isFull()) {
            send(new SPacketJoinWorld(alloc(), false, "World is full.", -1));
        } else {
            player = new Player("EntityName", world.assignEntityId(), LunarServer.getServer(), this);
            player.setWorldIn(world);

            // player will be set into world once they are actually loaded.
            send(new SPacketJoinWorld(alloc(), true, null, player.getEntityId()));
        }
    }

    @Override
    public void handleWorldLoaded(CPacketWorldLoaded packet) {
        if (player != null) {
            player.setLoaded(true);
            player.getWorld().spawnPlayerInWorld(player);
        }
    }

    @Override
    public void handleBodyForce(CPacketBodyForce packet) {
        if (player != null && player.getWorld() != null) {
            player.getWorld().broadcastPacketInWorld(new SPacketBodyForce(alloc(), packet));
        }
    }

    @Override
    public void connectionClosed() {
        if (disconnected) return;

        System.err.println("Player disconnected due to connection error.");
        if (player != null) {
            if (player.getWorld() != null) player.getWorld().removePlayerInWorld(player);
            player.getServer().handlePlayerDisconnect(player);
        }
        disconnect();
    }

    @Override
    public void disconnect() {
        channel.pipeline().remove(this);
        channel.close();
    }
}
