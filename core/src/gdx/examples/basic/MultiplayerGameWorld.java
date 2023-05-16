package gdx.examples.basic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import gdx.lunar.protocol.packet.server.SPacketRemovePlayer;
import gdx.lunar.utilities.PlayerSupplier;
import gdx.lunar.world.WorldConfiguration;
import gdx.lunar.world.impl.WorldAdapter;
import lunar.shared.entity.player.impl.NetworkPlayer;

public final class MultiplayerGameWorld extends WorldAdapter {

    private final MultiplayerIntroductionGame game;
    private final DemoPlayer player;

    public MultiplayerGameWorld(PlayerSupplier playerSupplier, DemoPlayer player, World world, WorldConfiguration configuration, Engine engine, MultiplayerIntroductionGame game) {
        super(playerSupplier, world, configuration, engine);
        this.player = player;
        this.game = game;
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
    public void handleWorldJoin(SPacketJoinWorld world) {
        Gdx.app.log(MultiplayerIntroductionGame.TAG, "Joining local-world: " + world.getWorldName() + ", entity ID is " + world.getEntityId());

        // set our player's entity ID from world packet.
        player.setEntityId(world.getEntityId());
        // spawn local player in world
        player.defineEntity(this.getEntityWorld(), 0.0f, 0.0f);
        // tell the server we are good to go.
        player.getConnection().updateWorldLoaded();
        player.setInWorld(true);
        player.setWorldIn(this);
        game.ready = true;
    }

    /**
     * Handle a network player joining the local world
     *
     * @param packet the join packet
     */
    public void handlePlayerJoin(SPacketCreatePlayer packet) {
        Gdx.app.log(MultiplayerIntroductionGame.TAG, "Spawning new player " + packet.getUsername() + ":" + packet.getEntityId());

        final NetworkPlayer player = new NetworkPlayer(true);
        // load player assets.
        player.addRegion("player", game.getTexture());
        player.disablePlayerCollision(true);
        player.setProperties(packet.getUsername(), packet.getEntityId());
        // set your local game properties
        player.setSize(16, 16, (1 / 16.0f));
        // spawn player in your local world.
        player.spawnInWorld(this);
    }

    /**
     * Handle a network player leaving
     *
     * @param packet the packet
     */
    public void handlePlayerLeave(SPacketRemovePlayer packet) {
        Gdx.app.log(MultiplayerIntroductionGame.TAG, "Player " + packet.getEntityId() + " left.");
        getPlayer(packet.getEntityId()).dispose();
        removePlayerInWorld(packet.getEntityId());
    }

}
