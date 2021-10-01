package gdx.lunar.entity.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import gdx.lunar.entity.drawing.LunarDrawableEntity;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.drawing.render.DefaultPlayerRenderer;
import gdx.lunar.entity.drawing.render.LunarPlayerRenderer;
import gdx.lunar.world.LunarWorld;

/**
 * Could be a local player entity.
 */
public abstract class LunarEntityPlayer extends LunarDrawableEntity {

    /**
     * Player scaling.
     * Player width * scale
     * Player height * scale
     */
    private final float scale, width, height;

    /**
     * renderer this player is using.
     */
    protected LunarPlayerRenderer renderer;
    protected boolean ignorePlayerCollision;

    /**
     * Initialize a new player entity.
     *
     * @param entityId     the unique ID
     * @param playerScale  the scale of the player, usually relates to the world scale.
     * @param playerWidth  the width of the player or players texture.
     * @param playerHeight the height of the players or players texture.
     * @param rotation     the default rotation
     */
    public LunarEntityPlayer(int entityId, float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        super(entityId);

        this.scale = playerScale;
        this.width = playerWidth * scale;
        this.height = playerHeight * scale;
        this.rotation = rotation;
    }

    /**
     * Initialize a new player entity.
     *
     * @param playerScale  the scale of the player, usually relates to the world scale.
     * @param playerWidth  the width of the player or players texture.
     * @param playerHeight the height of the players or players texture.
     * @param rotation     the default rotation
     */
    public LunarEntityPlayer(float playerScale, float playerWidth, float playerHeight, Rotation rotation) {
        this(0, playerScale, playerWidth, playerHeight, rotation);
    }

    public void setIgnoreOtherPlayerCollision(boolean ignorePlayerCollision) {
        this.ignorePlayerCollision = ignorePlayerCollision;
    }

    public boolean ignorePlayerCollision() {
        return ignorePlayerCollision;
    }

    /**
     * Initialize the renderer this player is going to use.
     *
     * @param atlas       the atlas of textures.
     * @param offsetBox2d if player position should be offset to correct collision.
     */
    public void initializePlayerRendererAndLoad(TextureAtlas atlas, boolean offsetBox2d) {
        this.renderer = new DefaultPlayerRenderer(atlas, rotation, width, height, offsetBox2d);
        renderer.load();
    }

    /**
     * Initialize the renderer this player is going to use.
     * NOTE: This will **NOT** load the renderer.
     *
     * @param renderer the custom renderer.
     */
    public void initializePlayerRendererWith(LunarPlayerRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (renderer != null)
            renderer.render(delta, interpolated.x, interpolated.y, batch);
    }

    /**
     * Pre update this player to capture their current position.
     */
    public void preUpdate() {
        prevX = body.getPosition().x;
        prevY = body.getPosition().y;
    }

    /**
     * Interpolate the position.
     *
     * @param alpha alpha
     */
    public void interpolate(float alpha) {
        interpolated.x = Interpolation.linear.apply(prevX, position.x, alpha);
        interpolated.y = Interpolation.linear.apply(prevY, position.y, alpha);
    }

    /**
     * Interpolate the position.
     *
     * @param i     method
     * @param alpha alpha
     */
    public void interpolate(Interpolation i, float alpha) {
        interpolated.x = i.apply(prevX, position.x, alpha);
        interpolated.y = i.apply(prevY, position.y, alpha);
    }

    @Override
    public void spawnEntityInWorld(LunarWorld world, float x, float y) {
        if (this.worldIn != null) {
            this.worldIn.dispose();
        }

        this.worldIn = world;

        // set initial positions
        prevX = x;
        prevY = y;
        position.set(x, y);
        interpolated.set(x, y);

        // default body def for all player types (network + local)
        final BodyDef definition = new BodyDef();
        definition.type = BodyDef.BodyType.DynamicBody;
        definition.fixedRotation = true;
        definition.position.set(x, y);

        // create body and set the basic poly shape
        body = world.getPhysicsWorld().createBody(definition);
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();
    }

    @Override
    public void dispose() {
        if (this.renderer != null) renderer.dispose();
        renderer = null;
        worldIn = null;
    }
}
