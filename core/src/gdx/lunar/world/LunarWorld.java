package gdx.lunar.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a networked game world.
 */
public abstract class LunarWorld implements Disposable {

    /**
     * All players within this world.
     */
    protected final ConcurrentMap<Integer, LunarNetworkEntityPlayer> players = new ConcurrentHashMap<>();

    protected final LunarEntityPlayer player;
    protected final World world;
    protected float worldScale;
    protected boolean handlePhysics, updatePlayer, updateNetworkPlayers;

    protected float stepTime = 1.0f / 60.0f;
    protected float maxFrameTime = 0.25f;
    protected float accumulator;

    protected int velocityIterations = 8, positionIterations = 3;

    /**
     * Initialize a new game world.
     *
     * @param player               the player
     * @param world                the box2d world
     * @param worldScale           the scaling of the world.
     * @param handlePhysics        if true, this world will manage updating the Box2d world.
     * @param updatePlayer         if the local player should be updated.
     * @param updateNetworkPlayers if network players should be updated.
     */
    public LunarWorld(LunarEntityPlayer player, World world, float worldScale,
                      boolean handlePhysics, boolean updatePlayer, boolean updateNetworkPlayers) {
        this.player = player;
        this.world = world;
        this.worldScale = worldScale;
        this.handlePhysics = handlePhysics;
        this.updatePlayer = updatePlayer;
        this.updateNetworkPlayers = updateNetworkPlayers;
    }

    /**
     * An empty default constructor. You should use the setters to define configuration next.
     *
     * @param player the player
     * @param world  the world
     */
    public LunarWorld(LunarEntityPlayer player, World world) {
        this.player = player;
        this.world = world;
    }

    public void setStepTime(float stepTime) {
        this.stepTime = stepTime;
    }

    public void setMaxFrameTime(float maxFrameTime) {
        this.maxFrameTime = maxFrameTime;
    }

    public void setVelocityIterations(int velocityIterations) {
        this.velocityIterations = velocityIterations;
    }

    public void setPositionIterations(int positionIterations) {
        this.positionIterations = positionIterations;
    }

    public void setHandlePhysics(boolean handlePhysics) {
        this.handlePhysics = handlePhysics;
    }

    public void setUpdatePlayer(boolean updatePlayer) {
        this.updatePlayer = updatePlayer;
    }

    public void setUpdateNetworkPlayers(boolean updateNetworkPlayers) {
        this.updateNetworkPlayers = updateNetworkPlayers;
    }

    /**
     * @return the Box2d world.
     */
    public World getPhysicsWorld() {
        return world;
    }

    /**
     * @return players in this world.
     */
    public ConcurrentMap<Integer, LunarNetworkEntityPlayer> getPlayers() {
        return players;
    }

    /**
     * Set player in this world
     *
     * @param player the player
     */
    public void setPlayerInWorld(LunarNetworkEntityPlayer player) {
        this.players.put(player.getEntityId(), player);
    }

    /**
     * Remove a player from this world
     *
     * @param player the player
     */
    public void removePlayerFromWorld(LunarNetworkEntityPlayer player) {
        this.players.remove(player.getEntityId(), player);
    }

    /**
     * Remove a player from this world
     *
     * @param player the player ID.
     */
    public void removePlayerFromWorld(int player) {
        this.players.remove(player);
    }

    /**
     * Update a players position
     *
     * @param entityId the entity ID
     * @param x        x
     * @param y        y
     * @param rotation rotation
     */
    public void updatePlayerPosition(int entityId, float x, float y, int rotation) {
        final LunarNetworkEntityPlayer player = players.get(entityId);
        if (player != null) {
            player.updatePositionFromNetwork(x, y, Rotation.values()[rotation]);
        }
    }

    /**
     * Update a players velocity
     *
     * @param entityId  entity ID
     * @param velocityX velocity X
     * @param velocityY velocity Y
     * @param rotation  the rotation
     */
    public void updatePlayerVelocity(int entityId, float velocityX, float velocityY, int rotation) {
        final LunarNetworkEntityPlayer player = players.get(entityId);
        if (player != null) {
            player.updateVelocityFromNetwork(velocityX, velocityY, Rotation.values()[rotation]);
        }
    }

    /**
     * Update this world
     *
     * @param d delta time.
     */
    public void update(float d) {
        if (updateNetworkPlayers) {
            for (LunarNetworkEntityPlayer value : players.values()) value.preUpdate();
        }

        final float delta = Math.min(d, maxFrameTime);

        if (handlePhysics) {
            accumulator += delta;

            while (accumulator >= stepTime) {
                player.preUpdate();

                world.step(stepTime, velocityIterations, positionIterations);
                accumulator -= stepTime;
            }
        }

        if (updatePlayer) {
            player.update(delta);
            player.interpolate(0.5f);
        }

        for (LunarNetworkEntityPlayer value : players.values()) {
            value.update(delta);
            value.interpolate(0.5f);
        }
    }

    /**
     * Render this world
     *
     * @param batch the batch
     * @param delta the delta
     */
    public abstract void renderWorld(SpriteBatch batch, float delta);

    @Override
    public void dispose() {
        this.players.clear();
        this.world.dispose();
    }
}
