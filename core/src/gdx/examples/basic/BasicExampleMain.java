package gdx.examples.basic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import gdx.lunar.protocol.packet.client.CPacketJoinWorld;
import gdx.lunar.protocol.packet.client.CPacketWorldLoaded;
import gdx.lunar.world.BasicLunarWorld;

public final class BasicExampleMain extends Game {

    private LunarClientServer server;

    private BasicLunarWorld lunarWorld;
    private TextureAtlas atlas;

    private LunarPlayer player;

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch batch;

    public BasicExampleMain() {
    }

    @Override
    public void create() {
        // create a new lunar instance.
        final Lunar lunar = new Lunar();
        lunar.setGdxInitialized(true);

        // connect to remote server.
        server = new LunarClientServer(lunar, "localhost", 6969);
        server.connect().join();

        // get our connection
        final PlayerConnection connection = (PlayerConnection) server.getConnection();

        // default world and camera scaling.
        final float scaling = 1 / 16.0f;

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

        // tell the server we want to join a world.
        connection.send(new CPacketJoinWorld(connection.alloc(), "LunarWorld"));

        // Supply this player with our textures.
        atlas = new TextureAtlas("character.atlas");
        player.initializePlayerRendererAndLoad(atlas, true);

        // Supply all other players who join our world with those textures as-well.
        connection.setJoinWorldListener(networkPlayer -> networkPlayer.initializePlayerRendererAndLoad(atlas, true));

        // Create a networked world for others to join us.
        // We tell the world to handle physics updates and local player updates for us.
        lunarWorld = new BasicLunarWorld(player, world, scaling, true, true, true, true);
        // Spawn our player in the world.
        player.spawnEntityInWorld(lunarWorld, 2.0f, 2.0f);

        // Initialize our graphics for drawing.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / (scaling / 2.0f), Gdx.graphics.getHeight() / (scaling / 2.0f));
        viewport = new ExtendViewport(Gdx.graphics.getWidth() / scaling, Gdx.graphics.getHeight() / scaling);

        // Set the initial camera position
        camera.position.set(2.0f, 2.0f, 0.0f);
        camera.update();

        batch = new SpriteBatch();

        // let the server know we have loaded everything.
        connection.send(new CPacketWorldLoaded(connection.alloc()));
    }

    @Override
    public void render() {
        // update our camera
        camera.position.set(player.getInterpolated().x, player.getInterpolated().y, 0f);
        camera.update();
        update();

        // clear screen.
        Gdx.gl.glClearColor(69 / 255f, 8f / 255f, 163f / 255, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update the world.
        final float delta = Gdx.graphics.getDeltaTime();
        lunarWorld.update(delta);

        // begin batch
        batch.setProjectionMatrix(camera.projection);
        batch.begin();

        // render our world.
        lunarWorld.renderWorld(batch, delta);
        // render our player
        player.render(batch, delta);

        // done :)
        batch.end();
    }

    private void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            System.err.println("APPLYING FORCE");
            // test apply forces over the network.
            final Vector2 point = player.getBody().getWorldPoint(new Vector2(5.0f, -5));
            final float fx = player.getBody().getMass() * (player.getX() * 150);
            final float fy = player.getBody().getMass() * (player.getY() * 150);
            this.player.getWorldIn().applyForceToPlayerNetwork(player.getConnection(), fx, fy, point.x, point.y, true);
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
        lunarWorld.dispose();
        atlas.dispose();
        batch.dispose();
        server.dispose();
    }
}
