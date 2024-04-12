package gdx.examples.basic;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import gdx.lunar.LunarClientServer;
import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.protocol.GdxProtocol;
import gdx.lunar.protocol.packet.client.C2SPacketJoinWorld;
import gdx.lunar.protocol.packet.server.*;
import gdx.lunar.world.WorldConfiguration;
import lunar.shared.entity.player.impl.LunarNetworkPlayer;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a basic multiplayer demo.
 * <p>
 * Connect unlimited clients within a world with movement systems.
 */
public final class MultiplayerIntroductionGame extends Game {

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
    private WorldConfiguration configuration;
    private PooledEngine engine;
    public boolean ready;

    BitmapFont font;

    @Override
    public void create() {
        Gdx.app.log(TAG, "Initializing multiplayer demo.");

        font = new BitmapFont();
        font.getData().setScale(0.09f);

        // default assets in this project
        assets = new TextureAtlas(Gdx.files.internal("character.atlas"));

        batch = new SpriteBatch();
        initializeCameraAndViewport();

        // initialize our player with default components.
        player = new DemoPlayer(true, assets.findRegion("display"));
        player.setName("Player" + ThreadLocalRandom.current().nextInt(0, 999));

        // initialize our entity engine and world config
        configuration = new WorldConfiguration();
        engine = new PooledEngine();

        // initialize the world with 0 gravity
        world = new MultiplayerGameWorld(player, player, new World(Vector2.Zero, false), configuration, engine, this);
        world.addDefaultPlayerCollisionListener();

        // initialize our default protocol and connect to the remote server,
        final GdxProtocol protocol = new GdxProtocol(1, "1.0", true);
        final LunarClientServer server = new LunarClientServer(protocol, "localhost", 6969);
        // set provider because we want {@link PlayerConnectionHandler}
        server.setConnectionProvider(channel -> new PlayerConnectionHandler(channel, protocol));
        final boolean result = server.connectNoExceptions();

        // failed to connect, so exit() out.
        if (server.getConnection() == null || !result) {
            Gdx.app.exit();
            return;
        }

        // retrieve our players connection and create a new world and local player.
        Gdx.app.log(TAG, "Connected to the server successfully.");
        final PlayerConnectionHandler connection = (PlayerConnectionHandler) server.getConnection();
        player.setConnection(connection);

        // override a default handler in favor of our own
        // protocol.changeDefaultServerPacketHandlerFor(SPacketJoinWorld.PID, (buf, handler) -> handler.handleJoinWorld(new TestCustomJoinWorldPacketServer(buf)));

        // enable options we want Lunar to handle by default.
        // connection.enableOptions(
        //         ConnectionOption.HANDLE_PLAYER_POSITION,
        //         ConnectionOption.HANDLE_PLAYER_VELOCITY,
        //         ConnectionOption.HANDLE_AUTHENTICATION,
        //         ConnectionOption.HANDLE_PLAYER_FORCE);

        // register handlers we want to process ourselves instead of the default player connection
        connection.registerHandlerSync(S2CPacketJoinWorld.PACKET_ID, packet -> world.handleWorldJoin((S2CPacketJoinWorld) packet));
        connection.registerHandlerSync(S2CPacketCreatePlayer.PACKET_ID, packet -> world.handlePlayerJoin((S2CPacketCreatePlayer) packet));
        connection.registerHandlerSync(S2CPacketRemovePlayer.PACKET_ID, packet -> world.handlePlayerLeave((S2CPacketRemovePlayer) packet));
        connection.registerHandlerSync(S2CPacketWorldInvalid.PACKET_ID, packet -> System.err.println("World invalid!"));
        connection.registerHandlerSync(S2CPacketStartGame.PACKET_ID, packet -> world.handleStartGame((S2CPacketStartGame) packet));
        // TODO: Implement a join world timeout if you desire.

        // finally, request to join the world
        final C2SPacketJoinWorld packet = new C2SPacketJoinWorld("TutorialWorld", player.getName(), System.currentTimeMillis());
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
            // update the world.
            world.update(delta);

            // update our camera
            camera.position.set(player.getInterpolatedPosition().x, player.getInterpolatedPosition().y, 0f);
            camera.update();

            // begin batch
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            // render our player
            player.render(batch, delta);

            for (LunarNetworkPlayer player : world.getPlayers().values()) {
                batch.draw(player.getRegion("player"), player.getInterpolatedPosition().x, player.getInterpolatedPosition().y,
                        player.getScaledWidth(), player.getScaledHeight());
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
        world.dispose();
        batch.dispose();
        player.dispose();
    }
}
