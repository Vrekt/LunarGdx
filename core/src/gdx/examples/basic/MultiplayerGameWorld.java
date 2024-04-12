package gdx.examples.basic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.protocol.packet.server.S2CPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.S2CPacketJoinWorld;
import gdx.lunar.protocol.packet.server.S2CPacketRemovePlayer;
import gdx.lunar.protocol.packet.server.S2CPacketStartGame;
import gdx.lunar.utilities.PlayerSupplier;
import gdx.lunar.world.WorldConfiguration;
import gdx.lunar.world.impl.WorldAdapter;
import lunar.shared.entity.player.impl.LunarNetworkPlayer;

public final class MultiplayerGameWorld extends WorldAdapter {

    private final MultiplayerIntroductionGame game;
    private final DemoPlayer player;

    public MultiplayerGameWorld(PlayerSupplier playerSupplier, DemoPlayer player, World world, WorldConfiguration configuration, Engine engine, MultiplayerIntroductionGame game) {
        super(playerSupplier, world, configuration, engine);
        this.player = player;
        this.game = game;
    }

    @Override
    public void updatePlayerVelocityInWorld(int entityId, float x, float y, float angle) {
        super.updatePlayerVelocityInWorld(entityId, x, y, angle);
    }

    @Override
    public void updatePlayerPositionInWorld(int entityId, float x, float y, float angle) {
        super.updatePlayerPositionInWorld(entityId, x, y, angle);
    }

    /**
     * Handle local player joining a new world.
     *
     * @param world the new world packet
     */
    public void handleWorldJoin(S2CPacketJoinWorld world) {
        Gdx.app.log(MultiplayerIntroductionGame.TAG, "Joining local-world: " + world.getWorldName() + ", entity ID is " + world.getEntityId());

        // set our player's entity ID from world packet.
        player.setEntityId(world.getEntityId());
        // spawn local player in world
        player.defineEntity(this.getEntityWorld(), 0.0f, 0.0f);
        // tell the server we are good to go.
        player.getConnection().updateWorldHasLoaded();
        player.setInWorld(true);
        player.setWorld(this);
        game.ready = true;
    }

    /**
     * Handle a network player joining the local world
     *
     * @param packet the join packet
     */
    public void handlePlayerJoin(S2CPacketCreatePlayer packet) {
        Gdx.app.log(MultiplayerIntroductionGame.TAG, "Spawning new player " + packet.getUsername() + ":" + packet.getEntityId());

        final LunarNetworkPlayer player = new LunarNetworkPlayer(true);
        // load player assets.
        player.addRegion("player", new TextureRegion(game.getTexture()));
        player.disablePlayerCollision(true);
        player.setProperties(packet.getUsername(), packet.getEntityId());
        // set your local game properties
        player.setSize(16, 16, (1 / 16.0f));
        // spawn player in your local world.
        player.spawnInWorld(this);
    }

    public void handleStartGame(S2CPacketStartGame packet) {
        if (packet.hasPlayers()) {
            for (S2CPacketStartGame.BasicServerPlayer packetPlayer : packet.getPlayers()) {
                System.err.println("found existing player: " + packetPlayer.username);
                final LunarNetworkPlayer player = new LunarNetworkPlayer(true);
                player.addRegion("player", game.getTexture());
                player.disablePlayerCollision(true);
                player.setProperties(packetPlayer.username, packetPlayer.entityId);
                player.setSize(16, 16, (1 / 16.0f));
                player.spawnInWorld(this, packetPlayer.position);
            }
        }
    }

    /**
     * Handle a network player leaving
     *
     * @param packet the packet
     */
    public void handlePlayerLeave(S2CPacketRemovePlayer packet) {
        Gdx.app.log(MultiplayerIntroductionGame.TAG, "Player " + packet.getEntityId() + " left.");
        if (hasPlayer(packet.getEntityId())) {
            getPlayer(packet.getEntityId()).dispose();
            removePlayerInWorld(packet.getEntityId(), false);
        }
    }

}
