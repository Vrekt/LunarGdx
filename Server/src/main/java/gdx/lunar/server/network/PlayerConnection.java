package gdx.lunar.server.network;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.client.*;
import gdx.lunar.protocol.packet.server.*;
import gdx.lunar.server.LunarServer;
import gdx.lunar.server.game.entity.player.Player;
import gdx.lunar.server.world.World;
import io.netty.channel.Channel;

/**
 * Represents the default player connection handler.
 */
public class PlayerConnection extends AbstractConnection implements ClientPacketHandler {

    /**
     * The player who owns this connection.
     */
    private Player player;
    private boolean disconnected;

    private long lastEntityRequestReset = System.currentTimeMillis();
    private int totalEntityRequests;

    public PlayerConnection(Channel channel) {
        super(channel);
    }

    @Override
    public void handleAuthentication(CPacketAuthentication packet) {
        System.err.println("Attempting to authenticate new client.");

        if (packet.getProtocolVersion() != LunarProtocol.protocolVersion) {
            // invalid protocol version, not allowed.
            send(new SPacketAuthentication(alloc(), false, "Outdated protocol version!"));
            disconnect();
        } else if (!LunarServer.getServer().canPlayerJoin()) {
            send(new SPacketAuthentication(alloc(), false, "Server is full."));
            disconnect();
        } else {
            System.err.println("New connection successfully authenticated.");
            send(new SPacketAuthentication(alloc(), true, null));
            this.player = new Player(-1, LunarServer.getServer(), this);
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
            player.setEntityId(world.assignEntityId());
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
    public void handleRequestSpawnEntity(CPacketRequestSpawnEntity packet) {
        if (player != null && player.getWorld() != null) {
            if (player.getWorld().getEntities().size() + 1 >= player.getWorld().getMaxEntities()) {
                // too many entities in the players world.
                send(new SPacketSpawnEntityDenied(channel.alloc(), packet.getTemporaryEntityId(),
                        "Too many entities in this world."));
            } else {
                totalEntityRequests++;
                if (totalEntityRequests >= player.getWorld().getMaxEntityRequests()) {
                    // too many requests.
                    send(new SPacketSpawnEntityDenied(channel.alloc(), packet.getTemporaryEntityId(),
                            "Too many requests within a short period of time."));
                } else {
                    // player is good to go.
                    final int entityId = player.getWorld().assignEntityId();
                    send(new SPacketSpawnEntity(alloc(),
                            packet.getEntityName(),
                            packet.getX(),
                            packet.getY(),
                            packet.getTemporaryEntityId(),
                            entityId));
                }

                if (System.currentTimeMillis() - lastEntityRequestReset >= 1000) {
                    totalEntityRequests = 0;
                    lastEntityRequestReset = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public void handleSetProperties(CPacketSetProperties packet) {
        if (player != null) {
            player.setName(packet.getUsername());

            // broadcast this change.
            this.player.getWorld().broadcast(player.getEntityId(), new SPacketSetEntityProperties(alloc(), player.getEntityId(), packet.getUsername()));
        }
    }

    @Override
    public void handleCreateLobby(CPacketCreateLobby packet) {
        if (player.getServer().canCreateLobby()) {
            final World lobby = player.getServer().createNewLobby();
            player.setWorldIn(lobby);

            final int entityId = lobby.assignEntityId();
            player.setEntityId(entityId);
            player.setLoaded(true);
            player.getWorld().spawnPlayerInWorld(player);

            send(new SPacketCreateLobby(alloc(), entityId, lobby.getWorldLobbyId()));
        } else {
            send(new SPacketCreateLobby(alloc(), "Too many lobbies within the server."));
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
