package gdx.examples.basic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import gdx.lunar.LunarClientServer;
import gdx.lunar.network.types.ConnectionOption;
import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.server.SPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import gdx.lunar.protocol.packet.server.SPacketRemovePlayer;
import lunar.shared.player.impl.LunarPlayerMP;

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

    private TextureAtlas assets;

    // a basic world.
    private MultiplayerGameWorld world;
    public boolean ready;

    @Override
    public void create() {
        Gdx.app.log(TAG, "Initializing multiplayer demo.");

        // default assets in this project
        assets = new TextureAtlas(Gdx.files.internal("character.atlas"));

        batch = new SpriteBatch();
        initializeCameraAndViewport();

        // initialize our player with default components.
        player = new DemoPlayer(true, assets.findRegion("display"));
        player.setEntityName("Player" + ThreadLocalRandom.current().nextInt(0, 999));

        // initialize the world with 0 gravity
        world = new MultiplayerGameWorld(player, new World(Vector2.Zero, true), this);
        // add default world systems
        world.addWorldSystems();
        // ignore player collisions
        world.addDefaultPlayerCollisionListener();
        // add a default instance
        world.addInstance(new MultiplayerGameInstance(player, new World(Vector2.Zero, true), 22));

        // initialize our default protocol and connect to the remote server,
        final LunarProtocol protocol = new LunarProtocol(true);
        final LunarClientServer server = new LunarClientServer(protocol, "localhost", 6969);
        // set provider because we want {@link PlayerConnectionHandler}
        server.setConnectionProvider(channel -> new PlayerConnectionHandler(channel, protocol));
        server.connectNoExceptions();

        // failed to connect, so exit() out.
        if (server.getConnection() == null) {
            throw new UnsupportedOperationException("No server.");
        }

        // retrieve our players connection and create a new world and local player.
        Gdx.app.log(TAG, "Connected to the server successfully.");

        final PlayerConnectionHandler connection = (PlayerConnectionHandler) server.getConnection();
        player.setConnection(connection);

        protocol.changeDefaultServerPacketHandlerFor(SPacketJoinWorld.PID, (buf, handler) -> {
            System.err.println("hello");
            handler.handleJoinWorld(new TestCustomJoinWorldPacketServer(buf));
        });

        // enable options we want Lunar to handle by default.
        connection.enableOptions(
                ConnectionOption.HANDLE_PLAYER_POSITION,
                ConnectionOption.HANDLE_PLAYER_VELOCITY,
                ConnectionOption.HANDLE_AUTHENTICATION,
                ConnectionOption.HANDLE_PLAYER_FORCE);

        // register handlers in the world, this could also be in the player class if you choose.
        connection.registerHandlerSync(ConnectionOption.HANDLE_JOIN_WORLD, packet -> world.handleWorldJoin((TestCustomJoinWorldPacketServer) packet));
        connection.registerHandlerSync(ConnectionOption.HANDLE_PLAYER_JOIN, packet -> world.handlePlayerJoin((SPacketCreatePlayer) packet));
        connection.registerHandlerSync(ConnectionOption.HANDLE_PLAYER_LEAVE, packet -> world.handlePlayerLeave((SPacketRemovePlayer) packet));
        // TODO: Implement a join world timeout if you desire.

        final TestCustomJoinWorldPacket packet = new TestCustomJoinWorldPacket("TutorialWorld", player.getName());
        packet.setTestField(true);
        player.getConnection().sendImmediately(packet);
    }

    public TextureRegion getTexture() {
        return assets.findRegion("display");
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
        camera.position.set(0.0f, 0.0f, 0.0f);
        camera.update();
    }

    @Override
    public void render() {
        ScreenUtils.clear(69 / 255f, 8f / 255f, 163f / 255, 0.5f);

        if (ready) {
            final float delta = Gdx.graphics.getDeltaTime();
            if (player.isInInstance()) {
                player.getWorlds().instanceIn.update(delta);
            } else {
                world.update(delta);
            }

            // update our camera
            camera.position.set(player.getInterpolated().x, player.getInterpolated().y, 0f);
            camera.update();

            // update the world.

            // begin batch
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            // render our player
            player.render(batch, delta);

            for (LunarPlayerMP player : world.getPlayers().values()) {
                batch.draw(player.getRegion("player"), player.getX(), player.getY(),
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
        player.getConnection().dispose();
        batch.dispose();
        player.dispose();
    }
}
