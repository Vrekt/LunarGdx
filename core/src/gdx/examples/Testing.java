package gdx.examples;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.entity.player.impl.LunarPlayer;
import gdx.lunar.entity.player.impl.LunarPlayerMP;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.network.PlayerConnection;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.packet.client.CPacketWorldLoaded;
import gdx.lunar.world.impl.WorldAdapter;

public class Testing extends Game {

    private Player player;
    private WorldAdapter world;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch batch;

    private boolean r;

    private Animation<TextureRegion> up_idle;

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

            player = new Player(true, "TestingPlayer123", connection);
            player.getConfig().setConfig(16, 16, (1 / 16.0f));

            world = new WorldAdapter(player, new World(Vector2.Zero, true));
            world.addWorldSystems();

            // request the join the world, once accepted spawn our local entity in it.
            connection.joinWorld("Athena", player.getName());
            player.spawnEntityInWorld(world, 2.0f, 2.0f);

            final TextureAtlas atlas = new TextureAtlas("character.atlas");

            final Animation<TextureRegion> up = new Animation<>(.25f, atlas.findRegion("walking_up", 1), atlas.findRegion("walking_up", 2));
            player.texture = up.getKeyFrames()[0];
            final Animation<TextureRegion> down = new Animation<>(.25f, atlas.findRegion("walking_down", 1), atlas.findRegion("walking_down", 2));
            final Animation<TextureRegion> left = new Animation<>(.25f, atlas.findRegion("walking_left", 1), atlas.findRegion("walking_left", 2));
            final Animation<TextureRegion> right = new Animation<>(.25f, atlas.findRegion("walking_right", 1), atlas.findRegion("walking_right", 2));
            up.setPlayMode(Animation.PlayMode.LOOP);
            down.setPlayMode(Animation.PlayMode.LOOP);
            left.setPlayMode(Animation.PlayMode.LOOP);
            right.setPlayMode(Animation.PlayMode.LOOP);

            final Animation<TextureRegion> upIdle = new Animation<>(0.0f, atlas.findRegion("walking_up_idle"));
            final Animation<TextureRegion> downIdle = new Animation<>(0.0f, atlas.findRegion("walking_down_idle"));
            final Animation<TextureRegion> leftIdle = new Animation<>(0.0f, atlas.findRegion("walking_left_idle"));
            final Animation<TextureRegion> rightIdle = new Animation<>(0.0f, atlas.findRegion("walking_right_idle"));

            // Initialize our graphics for drawing.
            camera = new OrthographicCamera();
            camera.setToOrtho(false, Gdx.graphics.getWidth() / ((1 / 16.0f) / 2.0f), Gdx.graphics.getHeight() / ((1 / 16.0f) / 2.0f));
            viewport = new ExtendViewport(Gdx.graphics.getWidth() / (1 / 16.0f), Gdx.graphics.getHeight() / (1 / 16.0f));

            // Set the initial camera position
            camera.position.set(2.0f, 2.0f, 0.0f);
            camera.update();

            batch = new SpriteBatch();

            // let the server know we have loaded everything.
            connection.send(new CPacketWorldLoaded());
            r = true;
        } catch (Exception any) {
            any.printStackTrace();
        }
    }

    @Override
    public void render() {
        if (r) {
            world.update(Gdx.graphics.getDeltaTime());

            // update our camera
            camera.position.set(player.getInterpolated().x, player.getInterpolated().y, 0f);
            camera.update();

            // clear screen.
            Gdx.gl.glClearColor(69 / 255f, 8f / 255f, 163f / 255, 0.5f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // update the world.
            final float delta = Gdx.graphics.getDeltaTime();

            // begin batch
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            // render our player
            player.render(batch, delta);

            // render other entities
            for (LunarPlayerMP player : world.getPlayers().values()) {
                batch.draw(this.player.texture, player.getX(), player.getY(),
                        player.getConfig().size.x * player.getConfig().size.z, player.getConfig().size.y * player.getConfig().size.z);
            }

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
    public void pause() {

    }

    @Override
    public void resume() {

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

    public static final class Player extends LunarPlayer {
        public TextureRegion texture;

        public Player(boolean initializeComponents, String name, AbstractConnection connection) {
            super(initializeComponents, name, connection);
        }

        @Override
        public void pollInput() {
            setMoving(true);

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                getVelocity().set(-moveSpeed, 0f);
                rotation = 6f;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                getVelocity().set(moveSpeed, 0f);
                rotation = 7f;
            } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                getVelocity().set(0f, moveSpeed);
                rotation = 4f;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                getVelocity().set(0f, -moveSpeed);
                rotation = 5f;
            } else {
                setMoving(false);
                getVelocity().set(0, 0);
            }
        }

        @Override
        public void render(SpriteBatch batch, float delta) {
            batch.draw(texture, getX(), getY(), getConfig().size.x * getConfig().size.z, getConfig().size.y * getConfig().size.z);
        }
    }

}
