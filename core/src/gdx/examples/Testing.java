package gdx.examples;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.entity.player.impl.LunarPlayer;
import gdx.lunar.network.PlayerConnection;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.world.impl.WorldAdapter;

public class Testing extends Game {

    private WorldAdapter world;

    @Override
    public void create() {
        try {
            Gdx.app.log("Testing", "Creating new testing game @ localhost:6969");

            final LunarProtocol protocol = new LunarProtocol(true);
            final LunarClientServer server = new LunarClientServer(new Lunar(), protocol, "localhost", 6969);
            server.connect().join();

            if (server.getConnection() == null) {
                exitOnError();
                return;
            }

            Gdx.app.log("Testing", "Connected to the server successfully.");
            final PlayerConnection connection = (PlayerConnection) server.getConnection();

            final LunarPlayer player = new LunarPlayer(true, "TestingPlayer123", connection);
            world = new WorldAdapter(player, new World(Vector2.Zero, true));

            connection.joinWorld("Athena", player.getName());
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
