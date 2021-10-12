package gdx.examples.advanced;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import gdx.examples.advanced.entity.NetworkPlayer;
import gdx.examples.advanced.entity.Player;
import gdx.examples.advanced.packet.MyCustomPacket;
import gdx.examples.advanced.packet.MyCustomPositionPacketServer;
import gdx.examples.advanced.server.AdvancedNetworkHandler;
import gdx.examples.advanced.server.AdvancedPlayerConnection;
import gdx.examples.advanced.world.AdvancedLunarWorld;
import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.drawing.render.DefaultPlayerRenderer;
import gdx.lunar.entity.player.prop.PlayerProperties;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.client.CPacketWorldLoaded;
import gdx.lunar.protocol.packet.server.SPacketPlayerPosition;

import java.util.HashMap;
import java.util.Map;

/**
 * A more advanced example.
 */
public final class AdvancedExampleMain extends Game {

    private final Player player = new Player();

    private AdvancedLunarWorld lunarWorld;
    private TextureAtlas atlas;

    private AdvancedPlayerConnection connection;
    private DefaultPlayerRenderer renderer;

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch batch;

    @Override
    public void create() {
        // create a new lunar instance.
        final Lunar lunar = new Lunar();
        lunar.setGdxInitialized(true);

        // Initialize our default protocol
        final LunarProtocol protocol = new LunarProtocol(true);

        // Set up the local client server.
        final LunarClientServer server = new LunarClientServer(lunar, protocol, "localhost", 6969);
        // tell the server to use our custom player connection class.
        server.setProvider(channel -> new AdvancedPlayerConnection(channel, player, this, protocol));
        // tell the server to use our custom adapter.
        server.setInboundNetworkHandler(new AdvancedNetworkHandler(this));
        // connect
        server.connect().join();

        // grab our connection and tell the player to use it.
        this.connection = (AdvancedPlayerConnection) server.getConnection();
        player.setConnection(this.connection);

        // register custom packets
        server.getProtocol().changeDefaultServerPacketHandlerFor(SPacketPlayerPosition.PID, (in, handler)
                -> this.connection.handleCustomPacket(new MyCustomPositionPacketServer(in)));

        // register a unique custom packet.
        this.connection.registerPacket(99, MyCustomPacket::new, packet -> {
            // Do something with the packet.
        });

        // default world and camera scaling.
        final float scaling = 1 / 16.0f;

        // Initialize basic properties about how a player should be drawn.
        final PlayerProperties basic = new PlayerProperties(scaling, 16f, 18f);
        lunar.setPlayerProperties(basic);

        // Create our Box2D world.
        final World world = new World(Vector2.Zero, true);

        // tell the server we want to join a world.
        connection.sendJoinWorld("LunarWorld");

        // Supply this player with our textures.
        atlas = new TextureAtlas("character2.atlas");
        // Get all our assets from this atlas.

        // sort each walking animation by the direction they are facing.
        final Map<Rotation, String> walkingAnimations = new HashMap<>();
        final Map<Rotation, String> idleAnimations = new HashMap<>();

        walkingAnimations.put(Rotation.FACING_UP, "character_up");
        walkingAnimations.put(Rotation.FACING_DOWN, "character_down");
        walkingAnimations.put(Rotation.FACING_LEFT, "character_left");
        walkingAnimations.put(Rotation.FACING_RIGHT, "character_right");

        idleAnimations.put(Rotation.FACING_UP, "character_up_idle");
        idleAnimations.put(Rotation.FACING_DOWN, "character_down_idle");
        idleAnimations.put(Rotation.FACING_LEFT, "character_left_idle");
        idleAnimations.put(Rotation.FACING_RIGHT, "character_right_idle");

        // Use a different constructor since this art has different naming conventions.
        // Supply the atlas, default rotation, walking and idle art,
        // width and height of the player and if position should be offset
        // to fit inside box2d collision.
        renderer = new DefaultPlayerRenderer(
                atlas,
                Rotation.FACING_UP,
                walkingAnimations,
                idleAnimations,
                basic.width * scaling,
                basic.height * scaling,
                true);

        // now give this to the player.
        player.initializePlayerRendererWith(renderer);
        // load
        renderer.load();

        // Create a networked world for others to join us.
        // We tell the world let us handle physics and player updates.
        lunarWorld = new AdvancedLunarWorld(player, world);
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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        camera.setToOrtho(false, width / 16f / 2f, height / 16f / 2f);
    }

    public AdvancedPlayerConnection getConnection() {
        return connection;
    }

    public void notifyNoAuth() {
        // show some UI
    }

    public void notifyConnection() {
        // show some UI
    }

    public void notifyDisconnect() {
        // show some UI
    }

    public void notifyCantJoinWorld() {
        // show some UI
    }

    public void handlePlayerJoin(NetworkPlayer player) {
        // give this player a renderer.
        final DefaultPlayerRenderer renderer = initializeNewPlayerRenderer((1 / 16.0f), 18f, 16f);
        renderer.load();
        player.initializePlayerRendererWith(renderer);
    }

    public void handlePlayerLeave(NetworkPlayer player) {
        System.err.println("do something leave");
    }

    public DefaultPlayerRenderer initializeNewPlayerRenderer(float scaling, float width, float height) {
        // sort each walking animation by the direction they are facing.
        final Map<Rotation, String> walkingAnimations = new HashMap<>();
        final Map<Rotation, String> idleAnimations = new HashMap<>();

        walkingAnimations.put(Rotation.FACING_UP, "character_up");
        walkingAnimations.put(Rotation.FACING_DOWN, "character_down");
        walkingAnimations.put(Rotation.FACING_LEFT, "character_left");
        walkingAnimations.put(Rotation.FACING_RIGHT, "character_right");

        idleAnimations.put(Rotation.FACING_UP, "character_up_idle");
        idleAnimations.put(Rotation.FACING_DOWN, "character_down_idle");
        idleAnimations.put(Rotation.FACING_LEFT, "character_left_idle");
        idleAnimations.put(Rotation.FACING_RIGHT, "character_right_idle");

        // Use a different constructor since this art has different naming conventions.
        // Supply the atlas, default rotation, walking and idle art,
        // width and height of the player and if position should be offset
        // to fit inside box2d collision.
        return new DefaultPlayerRenderer(
                atlas,
                Rotation.FACING_UP,
                walkingAnimations,
                idleAnimations,
                width * scaling,
                height * scaling,
                true);
    }

}
