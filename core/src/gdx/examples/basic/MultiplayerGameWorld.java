package gdx.examples.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import gdx.lunar.protocol.packet.server.SPacketRemovePlayer;
import gdx.lunar.world.impl.WorldAdapter;
import lunar.shared.entity.player.impl.LunarPlayer;
import lunar.shared.entity.player.impl.LunarPlayerMP;

public final class MultiplayerGameWorld extends WorldAdapter {

    BasicMultiplayerDemoGame game;

    public MultiplayerGameWorld(LunarPlayer player, World world, BasicMultiplayerDemoGame game) {
        super(player, world);
        this.game = game;
    }

    @Override
    public void renderWorld(SpriteBatch batch) {
        // This is not used here, but you could. Regardless we handle all rendering in main game loop.
    }

    /**
     * Handle local player joining a new world.
     *
     * @param world the new world packet
     */
    public void handleWorldJoin(SPacketJoinWorld world) {
        Gdx.app.log(BasicMultiplayerDemoGame.TAG, "Joining local-world: " + world.getWorldName() + ", entity ID is " + world.getEntityId());

        // execute network operations back on the main thread.
        Gdx.app.postRunnable(() -> {
            // set our player's entity ID from world packet.
            player.setEntityId(world.getEntityId());
            // spawn local player in world
            player.spawnEntityInWorld(this, 0.0f, 0.0f);
            // load into the world!
            // tell the server we are good to go.
            player.getConnection().updateWorldLoaded();

            // etc...
            game.ready = true;
        });
    }

    /**
     * Handle a network player joining the local world
     *
     * @param packet the join packet
     */
    public void handlePlayerJoin(SPacketCreatePlayer packet) {
        Gdx.app.log(BasicMultiplayerDemoGame.TAG, "Spawning new player " + packet.getUsername() + ":" + packet.getEntityId());

        final LunarPlayerMP player = new LunarPlayerMP(true);
        // load player assets.
        player.putRegion("player", game.getTexture());

        player.setIgnorePlayerCollision(true);
        player.getProperties().initialize(packet.getEntityId(), packet.getUsername());
        // set your local game properties
        player.setConfig(16, 16, (1 / 16.0f));
        // spawn player in your local world.
        player.spawnEntityInWorld(this.player.getWorldIn(), packet.getX(), packet.getY());
    }

    /**
     * Handle a network player leaving
     *
     * @param packet the packet
     */
    public void handlePlayerLeave(SPacketRemovePlayer packet) {
        Gdx.app.log(BasicMultiplayerDemoGame.TAG, "Player " + packet.getEntityId() + " left.");
        if (hasNetworkPlayer(packet.getEntityId())) {
            removeEntityInWorld(packet.getEntityId(), true);
        }
    }

}
