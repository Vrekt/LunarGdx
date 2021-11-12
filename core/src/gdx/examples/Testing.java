package gdx.examples;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.entity.player.impl.LunarPlayer;
import gdx.lunar.network.PlayerConnection;
import gdx.lunar.network.handlers.ConnectionHandlers;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.world.impl.WorldAdapter;

public class Testing extends Game {

    private LunarPlayer player;
    private WorldAdapter world;

    @Override
    public void create() {
        try {
            Gdx.app.log("Testing", "Creating new testing game @ localhost:6969");

            // initialize our default protocol and connect to the remote server,
            final LunarProtocol protocol = new LunarProtocol(true);
            final LunarClientServer server = new LunarClientServer(new Lunar(), protocol, "localhost", 6969);
            server.connect().join();

            // failed to connect, so exit() out.
            if (server.getConnection() == null) {
                exitOnError();
                return;
            }

            // retrieve our players connection and create a new world and local player.
            Gdx.app.log("Testing", "Connected to the server successfully.");
            final PlayerConnection connection = (PlayerConnection) server.getConnection();

            player = new LunarPlayer(true, "TestingPlayer123", connection);
            world = new WorldAdapter(player, new World(Vector2.Zero, true));

            // request the join the world, once accepted spawn our local entity in it.
            connection.joinWorld("Athena", player.getName());
            connection.registerHandler(ConnectionHandlers.JOIN_WORLD, packet -> player.spawnEntityInWorld(world));

        } catch (Exception any) {
            any.printStackTrace();
        }
    }

    @Override
    public void render() {
        world.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        super.dispose();

        world.dispose();
    }

    private void exitOnError() {
        Gdx.app.log("Testing", "Failed to connect or authenticate.");
        dispose();
        System.exit(0);
    }

}
