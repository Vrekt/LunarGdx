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
        run(disconnectionHandler);
        this.close();
    }

    @Override
    public void handleAuthentication(SPacketAuthentication packet) {
        if (!packet.isAllowed()) {
            run(disconnectionHandler);
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
        if (lunar.getPlayerProperties() == null)
            lunar.setPlayerProperties(new PlayerProperties((1 / 16.0f), 16.0f, 16.0f));

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
    }

    @Override
    public void handlePlayerPosition(SPacketPlayerPosition packet) {
        if (player.getWorldIn() == null) return;
        System.err.println("pos from " + packet.getEntityId());

        player.getWorldIn().updatePlayerPosition(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

    @Override
    public void handlePlayerVelocity(SPacketPlayerVelocity packet) {
        if (player.getWorldIn() == null) return;
        System.err.println("vel from " + packet.getEntityId());
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
        }
    }

    @Override
    public void handleBodyForce(SPacketBodyForce packet) {
        final LunarNetworkEntityPlayer other = this.player.getWorldIn().getPlayer(packet.getEntityId());
        if (other != null) {

            // apply a force to the other body sync.
            run(() -> other.getBody().applyForce(packet.getForceX(), packet.getForceY(), packet.getPointX(), packet.getPointY(), true));
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
    }

    @Override
    public void handleSetEntityProperties(SPacketSetEntityProperties packet) {
        final LunarNetworkEntityPlayer player = this.player.getWorldIn().getPlayer(packet.getEntityId());
        if (player != null) player.setName(packet.getEntityName());
    }

    @Override
    public void handleCreateLobby(SPacketCreateLobby packet) {
        if (packet.isAllowed()) {
            if (this.lobbyWorld != null) {
                lobbyWorld.setLobbyId(packet.getLobbyId());
                this.player.setEntityId(packet.getEntityId());

                // TODO: Were gonna need to decide where the player should be.
                run(() -> this.player.spawnEntityInWorld(lobbyWorld, lobbyWorld.getWorldSpawn().x, lobbyWorld.getWorldSpawn().y));
            }

            if (this.joinLobbyHandler != null) {
                // post this sync.
                Gdx.app.postRunnable(() -> joinLobbyHandler.run());
            }
        }
    }

    @Override
    public void handleJoinLobbyDenied(SPacketJoinLobbyDenied packet) {

    }

    @Override
    public void handleJoinLobby(SPacketJoinLobby packet) {
        if (this.lobbyWorld != null) {
            lobbyWorld.setLobbyId(packet.getLobbyId());
            this.player.setEntityId(packet.getEntityId());

            // TODO: Were gonna need to decide where the player should be.
            run(() -> this.player.spawnEntityInWorld(lobbyWorld, lobbyWorld.getWorldSpawn().x, lobbyWorld.getWorldSpawn().y));
        }

        if (this.joinLobbyHandler != null) {
            // post this sync.
            Gdx.app.postRunnable(() -> joinLobbyHandler.run());
        }
    }

    @Override
    public void handleEnterInstance(SPacketEnterInstance packet) {

    }

    @Override
    public void handlePlayerEnterInstance(SPacketPlayerEnterInstance packet) {
        player.getWorldIn().getPlayer(packet.getEntityId()).spawnEntityInInstance(player.getInstanceIn(), player.getX(), player.getY(), true);
    }

    @Override
    public void disconnect() {
        super.disconnect();
    }

}
