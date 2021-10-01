package gdx.examples.lobby;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.entity.contact.PlayerCollisionListener;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.impl.LunarPlayer;
import gdx.lunar.entity.player.prop.PlayerProperties;
import gdx.lunar.network.PlayerConnection;
import gdx.lunar.world.lobby.GameLobbyWorldAdapter;

import java.util.concurrent.ThreadLocalRandom;

public class TestingLobby extends Game {
    private LunarClientServer server;

    private GameLobbyWorldAdapter lunarLobby;
    private TextureAtlas atlas;

    private LunarPlayer player;

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch batch;

    private PlayerConnection connection;

    private boolean ready;

    public TestingLobby() {
    }

    @Override
    public void create() {
        // create a new lunar instance.
        final Lunar lunar = new Lunar();
        lunar.setGdxInitialized(true);

        // default world and camera scaling.
        final float scaling = 1 / 16.0f;

        // Initialize our graphics for drawing.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / (scaling / 2.0f), Gdx.graphics.getHeight() / (scaling / 2.0f));
        viewport = new ExtendViewport(Gdx.graphics.getWidth() / scaling, Gdx.graphics.getHeight() / scaling);

        // Set the initial camera position
        camera.position.set(2.0f, 2.0f, 0.0f);
        camera.update();

        // connect to remote server.
        server = new LunarClientServer(lunar, "localhost", 6969);
        server.connect().join();

        // get our connection
        connection = (PlayerConnection) server.getConnection();

        // Initialize basic properties about how a player should be drawn.
        final PlayerProperties basic = new PlayerProperties(scaling, 16f, 18f);
        lunar.setPlayerProperties(basic);

        // Create our Box2D world.
        final World world = new World(Vector2.Zero, true);
        world.setContactListener(new PlayerCollisionListener());

        // Create our local player.
        player = new LunarPlayer(scaling, basic.width, basic.height, Rotation.FACING_UP);
        player.setConnection(connection);
        connection.setPlayer(player);

        // create a basic lobby world.
        lunarLobby = new GameLobbyWorldAdapter(player, world, scaling);

        // create a new lobby.
        connection.setDefaultLobbyWorld(lunarLobby);
        connection.setJoinLobbyHandler(this::setup);

        connection.sendCreateLobby();
    }

    private void setup() {
        // Supply this player with our textures.
        atlas = new TextureAtlas("character.atlas");
        player.initializePlayerRendererAndLoad(atlas, true);

        // Supply all other players who join our world with those textures as-well.
        connection.setJoinWorldListener(networkPlayer -> networkPlayer.initializePlayerRendererAndLoad(atlas, true));

        // set a random name for this player
        // this MUST be set after sending the join world packet.
        player.setName("Player" + ThreadLocalRandom.current().nextInt(111, 999));

        batch = new SpriteBatch();
        this.ready = true;
    }

    @Override
    public void render() {
        if (ready) {
            // update our camera
            camera.position.set(player.getInterpolated().x, player.getInterpolated().y, 0f);
            camera.update();

            // clear screen.
            Gdx.gl.glClearColor(69 / 255f, 8f / 255f, 163f / 255, 0.5f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // update the world.
            final float delta = Gdx.graphics.getDeltaTime();
            lunarLobby.update(delta);

            // begin batch
            batch.setProjectionMatrix(camera.projection);
            batch.begin();

            // render our world.
            lunarLobby.renderWorld(batch, delta);
            // render our player
            player.render(batch, delta);

            // done :)
            batch.end();
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        camera.setToOrtho(false, width / 16f / 2f, height / 16f / 2f);
    }

    @Override
    public void dispose() {
        player.dispose();
        lunarLobby.dispose();
        atlas.dispose();
        batch.dispose();
        server.dispose();
    }
}
