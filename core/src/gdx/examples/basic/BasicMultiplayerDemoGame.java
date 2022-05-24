package gdx.examples.basic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.network.PlayerConnection;
import gdx.lunar.network.handlers.ConnectionHandlers;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import lunar.shared.entity.player.impl.LunarPlayerMP;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a basic multiplayer demo.
 * <p>
 * Connect unlimited clients within a world with movement systems.
 */
public final class BasicMultiplayerDemoGame extends Game {

    public static final String TAG = "LunarGDX";

    // main drawing batch
    private SpriteBatch batch;
    // camera and default viewport
    private OrthographicCamera camera;
    private ExtendViewport viewport;

    // our local player
    private DemoPlayer player;

    // a basic world.
    private MultiplayerGameWorld world;
    public boolean ready;

    @Override
    public void create() {
        Gdx.app.log(TAG, "Initializing multiplayer demo.");

        // default assets in this project
        final TextureAtlas assets = new TextureAtlas(Gdx.files.internal("character.atlas"));

        batch = new SpriteBatch();
        initializeCameraAndViewport();

        // initialize our player with default components.
        player = new DemoPlayer(true, assets.findRegion("display"));
        player.setEntityName("Player" + ThreadLocalRandom.current().nextInt(0, 999));

        // initialize the world with 0 gravity
        world = new MultiplayerGameWorld(player, new World(Vector2.Zero, true), this);
        // add default world systems
        world.addWorldSystems();

        // initialize our default protocol and connect to the remote server,
        final LunarProtocol protocol = new LunarProtocol(true);
        final LunarClientServer server = new LunarClientServer(new Lunar(), protocol, "localhost", 6969);
        server.connect().join();

        // failed to connect, so exit() out.
        if (server.getConnection() == null) {
            throw new UnsupportedOperationException("No server.");
        }

        // retrieve our players connection and create a new world and local player.
        Gdx.app.log(TAG, "Connected to the server successfully.");
        final PlayerConnection connection = (PlayerConnection) server.getConnection();
        player.setConnection(connection);

        // register handlers in the world, this could also be in the player class if you choose.
        connection.registerHandler(ConnectionHandlers.JOIN_WORLD, packet -> world.handleWorldJoin((SPacketJoinWorld) packet));
        connection.registerHandler(ConnectionHandlers.CREATE_PLAYER, packet -> world.handlePlayerJoin((SPacketCreatePlayer) packet));

        // once connected, spawn into local world but request server first.
        // world handles spawning the player.
        // remote server MUST have a world registered with the name.
        player.getConnection().joinWorld("TutorialWorld", player.getName());

        // TODO: Implement a join world timeout if you desire.
    }

    /**
     * Initializes camera and viewport. Default scaling is 16.
     */
    private void initializeCameraAndViewport() {
        // Initialize our graphics for drawing.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / ((1 / 16.0f) / 2.0f), Gdx.graphics.getHeight() / ((1 / 16.0f) / 2.0f));
        viewport = new ExtendViewport(Gdx.graphics.getWidth() / (1 / 16.0f), Gdx.graphics.getHeight() / (1 / 16.0f));

        // Set the initial camera position
        camera.position.set(0.0f, 2.0f, 0.0f);
        camera.update();
    }

    @Override
    public void render() {
        ScreenUtils.clear(69 / 255f, 8f / 255f, 163f / 255, 0.5f);

        if (ready) {
            world.update(Gdx.graphics.getDeltaTime());

            // update our camera
            camera.position.set(player.getInterpolated().x, player.getInterpolated().y, 0f);
            camera.update();

            // update the world.
            final float delta = Gdx.graphics.getDeltaTime();

            // begin batch
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            // render our player
            player.render(batch, delta);

            // render other entities, with default player textures
            for (LunarPlayerMP player : world.getPlayers().values()) {
                batch.draw(this.player.getRegion("player"), player.getX(), player.getY(),
                        player.getWidthScaled(), player.getHeightScaled());
            }

            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        camera.setToOrtho(false, width / 16f / 2f, height / 16f / 2f);
    }

    @Override
    public void pause() {
        Gdx.app.log(TAG, "Paused.");
    }

    @Override
    public void resume() {
        Gdx.app.log(TAG, "Resumed.");
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
    }
}
