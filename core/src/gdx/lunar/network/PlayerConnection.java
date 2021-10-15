package gdx.lunar.network;

import com.badlogic.gdx.Gdx;
import gdx.lunar.Lunar;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.network.NetworkEntity;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;
import gdx.lunar.entity.player.impl.LunarNetworkPlayer;
import gdx.lunar.entity.player.prop.PlayerProperties;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.client.CPacketSetProperties;
import gdx.lunar.protocol.packet.server.*;
import gdx.lunar.world.LunarWorld;
import io.netty.channel.Channel;

/**
 * Represents a player connection to the server.
 */
public class PlayerConnection extends AbstractConnection {

    protected final Lunar lunar;
    protected LunarEntityPlayer player;

    // default lobby world this player would want to join
    protected Runnable joinLobbyHandler;
    protected LunarWorld lobbyWorld;

    public PlayerConnection(Lunar lunar, LunarProtocol protocol, Channel channel) {
        super(channel, protocol);

        this.lunar = lunar;
    }

    public void setPlayer(LunarEntityPlayer player) {
        this.player = player;
    }

    /**
     * Set the action to run when a player joins or creates a lobby.
     *
     * @param action the action
     */
    public void setJoinLobbyHandler(Runnable action) {
        this.joinLobbyHandler = action;
    }

    /**
     * Set the default lobby world that will be used when a player joins or creates a lobby.
     * This is not needed, you could do this via {@code setJoinLobbyHandler} and handle it there.
     *
     * @param world the world.
     */
    public void setDefaultLobbyWorld(LunarWorld world) {
        this.lobbyWorld = world;
    }

    @Override
    public void handleDisconnect(SPacketDisconnect packet) {
        this.close();
    }

    @Override
    public void handleAuthentication(SPacketAuthentication packet) {
        if (packet.isAllowed()) {
            Lunar.log("PlayerConnection", "Successfully authenticated with remote server.");
        } else {
            Lunar.log("PlayerConnection", "Failed to authenticate with server: " + packet.getNotAllowedReason());
            this.close();
        }
    }

    @Override
    public void handleCreatePlayer(SPacketCreatePlayer packet) {
        if (packet.getEntityId() == player.getEntityId()) return;

        // handle custom
        if (createPlayerHandler != null) {
            Gdx.app.postRunnable(() -> createPlayerHandler.accept(packet));
            return;
        }

        // TODO: Maybe default global rotation.
        Lunar.log("PlayerConnection", "Spawning a new player with eid " + packet.getEntityId() + " and username " + packet.getUsername());
        if (lunar.getPlayerProperties() == null) {
            Lunar.log("PlayerConnection", "WARNING: PlayerProperties is null, setting default.");
            lunar.setPlayerProperties(new PlayerProperties((1 / 16.0f), 16.0f, 16.0f));
        }

        final LunarNetworkPlayer player = new LunarNetworkPlayer(
                packet.getEntityId(),
                lunar.getPlayerProperties().scale,
                lunar.getPlayerProperties().width,
                lunar.getPlayerProperties().height,
                Rotation.FACING_UP);

        player.setName(packet.getUsername());
        Gdx.app.postRunnable(() -> {
            player.spawnEntityInWorld(this.player.getWorldIn(), packet.getX(), packet.getY());
            if (this.joinWorldListener != null) this.joinWorldListener.accept(player);
        });
    }

    @Override
    public void handleRemovePlayer(SPacketRemovePlayer packet) {
        if (player.getWorldIn() == null
                || packet.getEntityId() == this.player.getEntityId()) return;

        Gdx.app.postRunnable(() -> player.getWorldIn().removePlayerFromWorld(packet.getEntityId()));
        Lunar.log("PlayerConnection", "Removed player: " + packet.getEntityId());
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
            this.player.setEntityId(packet.getEntityId());

            // send username of local player.
            if (this.player.getName() != null) {
                send(new CPacketSetProperties(alloc(), player.getName()));
            }
            Lunar.log("PlayerConnection", "Allowed to join requested world.");
        } else {
            Lunar.log("PlayerConnection", "Failed to join the requested world because: " + packet.getNotAllowedReason());
        }
    }

    @Override
    public void handleBodyForce(SPacketBodyForce packet) {
        final LunarNetworkEntityPlayer other = this.player.getWorldIn().getPlayer(packet.getEntityId());
        if (other != null) {
            other.getBody().applyForce(packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY(), true);
        }
    }

    @Override
    public void handleSpawnEntity(SPacketSpawnEntity packet) {
        if (!player.getWorldIn().resetTemporaryEntityIfExists(packet.getTemporaryEntityId(), packet.getEntityId())) {
            // entity is not already in the world so spawn it.
            final NetworkEntity entity = new NetworkEntity(packet.getEntityName(), packet.getEntityId());
            entity.setPosition(packet.getX(), packet.getY());

            player.getWorldIn().setEntityInWorld(entity);
        }
    }

    @Override
    public void handleSpawnEntityDenied(SPacketSpawnEntityDenied packet) {
        player.getWorldIn().removeTemporaryEntity(packet.getTemporaryEntityId());
        Lunar.log("PlayerConnection", "Request to spawn an entity was denied because: " + packet.getReason());
    }

    @Override
    public void handleSetEntityProperties(SPacketSetEntityProperties packet) {
        final LunarNetworkEntityPlayer player = this.player.getWorldIn().getPlayer(packet.getEntityId());
        if (player != null) player.setName(packet.getEntityName());
    }

    @Override
    public void handleCreateLobby(SPacketCreateLobby packet) {
        if (packet.isAllowed()) {
            Lunar.log("PlayerConnection", "Creating a new lobby with the ID " + packet.getLobbyId());
            if (this.lobbyWorld != null) {
                lobbyWorld.setLobbyId(packet.getLobbyId());
                this.player.setEntityId(packet.getEntityId());

                // TODO: Were gonna need to decide where the player should be.
                this.player.spawnEntityInWorld(lobbyWorld, 0.0f, 0.0f);
            }

            if (this.joinLobbyHandler != null) {
                // post this sync.
                Gdx.app.postRunnable(() -> joinLobbyHandler.run());
            }
        } else {
            Lunar.log("PlayerConnection", "Cannot create a new lobby because: " + packet.getNotAllowedReason());
        }
    }

    @Override
    public void handleJoinLobbyDenied(SPacketJoinLobbyDenied packet) {
        Lunar.log("PlayerConnection", "Cannot join lobby because: " + packet.getReason());
    }

    @Override
    public void handleJoinLobby(SPacketJoinLobby packet) {
        Lunar.log("PlayerConnection", "Joining lobby: " + packet.getLobbyName() + ":" + packet.getLobbyId());

        if (this.lobbyWorld != null) {
            lobbyWorld.setLobbyId(packet.getLobbyId());
            this.player.setEntityId(packet.getEntityId());

            // TODO: Were gonna need to decide where the player should be.
            this.player.spawnEntityInWorld(lobbyWorld, 0.0f, 0.0f);
        }

        if (this.joinLobbyHandler != null) {
            // post this sync.
            Gdx.app.postRunnable(() -> joinLobbyHandler.run());
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();
    }

}
