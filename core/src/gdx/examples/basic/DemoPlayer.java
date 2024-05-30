package gdx.examples.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.adapter.PlayerAdapter;

/**
 * Represents a basic player.
 */
public final class DemoPlayer extends PlayerAdapter {

    private MultiplayerGameWorld world;

    public DemoPlayer(boolean initializeComponents, TextureRegion playerTexture) {
        super(initializeComponents);

        setMoveSpeed(6.0f);
        disablePlayerCollision(true);
        setNetworkSendRateInMs(10, 10);
        getTextureComponent().add("player", playerTexture);
        setSize(16, 16, (1 / 16.0f));
    }

    @Override
    public void setWorld(LunarWorld world) {
        this.world = (MultiplayerGameWorld) world;
    }

    @Override
    public LunarWorld getWorld() {
        return world;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        pollInput();
    }

    /**
     * Poll the input of the player
     */
    public void pollInput() {
        // do not update the body velocity
        // we do that ourselves in the super update method!
        setVelocity(0, 0, false);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            setAngle(0f);
            setVelocity(0.0f, -getMoveSpeed(), false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            setAngle(1f);
            setVelocity(0.0f, getMoveSpeed(), false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            setAngle(2f);
            setVelocity(getMoveSpeed(), 0.0f, false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            setAngle(3f);
            setVelocity(-getMoveSpeed(), 0.0f, false);
        }
    }

    public void render(SpriteBatch batch, float delta) {
        batch.draw(getTextureComponent().get("player"), getInterpolatedPosition().x, getInterpolatedPosition().y, getScaledWidth(), getScaledHeight());
    }

    @Override
    public void spawnInWorld(LunarWorld world, Vector2 position) {
        super.spawnInWorld(world, position);
        body.setFixedRotation(true);
    }
}
