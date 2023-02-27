package gdx.lunar.world;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.instance.LunarInstance;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.packet.server.SPacketPlayerPosition;
import gdx.lunar.protocol.packet.server.SPacketPlayerVelocity;
import lunar.shared.contact.PlayerCollisionListener;
import lunar.shared.entity.LunarEntity;
import lunar.shared.player.LunarEntityPlayer;
import lunar.shared.player.mp.LunarNetworkEntityPlayer;
import lunar.shared.systems.moving.EntityMovementSystem;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a networked world players and entities exist in.
 *
 * @param <P> any local entity player instance type you wish.
 * @param <N> any local network entity instance type you wish
 * @param <E> any local entity instance type you wish
 */
public abstract class LunarWorld<P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity>
        extends AbstractWorld<N, E> implements Disposable {

    // engine for this world.
    protected PooledEngine engine;

    // system
    protected EntityMovementSystem movementSystem;

    // local player
    protected final P player;
    protected final World world;

    // config
    protected WorldConfiguration configuration;
    protected float accumulator;

    // all the instances within this world
    protected CopyOnWriteArrayList<LunarInstance<P, N, E>> instancesInWorld = new CopyOnWriteArrayList<>();

    /**
     * Initialize a new game world.
     *
     * @param player               the player
     * @param world                the box2d world
     * @param worldScale           the scaling of the world.
     * @param handlePhysics        if true, this world will manage updating the Box2d world.
     * @param updatePlayer         if the local player should be updated.
     * @param updateNetworkPlayers if network players should be updated.
     * @param updateEntities       if entities should be updated.
     */
    public LunarWorld(P player, World world, float worldScale,
                      boolean handlePhysics, boolean updatePlayer, boolean updateNetworkPlayers,
                      boolean updateEntities, PooledEngine engine) {
        this.player = player;
        this.world = world;
        this.engine = engine;

        this.configuration = new WorldConfiguration();
        configuration.worldScale = worldScale;
        configuration.handlePhysics = handlePhysics;
        configuration.updatePlayer = updatePlayer;
        configuration.updateNetworkPlayers = updateNetworkPlayers;
        configuration.updateEntities = updateEntities;
    }

    /**
     * An empty default constructor. You should use the setters to define configuration next.
     *
     * @param player the player
     * @param world  the world
     */
    public LunarWorld(P player, World world, WorldConfiguration configuration, PooledEngine engine) {
        this.player = player;
        this.world = world;
        this.configuration = configuration;
        this.engine = engine;
    }

    /**
     * An empty default constructor. You should use the setters to define configuration next.
     *
     * @param player the player
     * @param world  the world
     */
    public LunarWorld(P player, World world) {
        this(player, world, new WorldConfiguration(), new PooledEngine());
    }

    public void addInstance(LunarInstance<P, N, E> instance) {
        this.instancesInWorld.add(instance);
    }

    public LunarInstance<P, N, E> getInstanceFromId(int instanceId) {
        return instancesInWorld.stream().filter(instance -> instance.getInstanceId() == instanceId).findFirst().orElse(null);
    }

    /**
     * Add the default {@link  PlayerCollisionListener} to ignore player collisions
     */
    public void addDefaultPlayerCollisionListener() {
        world.setContactListener(new PlayerCollisionListener());
    }

    public AbstractConnection getLocalConnection() {
        return player.getConnection();
    }

    public void setEngine(PooledEngine engine) {
        this.engine = engine;
    }

    public PooledEngine getEngine() {
        return engine;
    }

    /**
     * @return {@code  true} if the player limit of this world has not been reached
     */
    public boolean canEnterWorld() {
        return !(this.players.size() >= configuration.maxPlayerCapacity);
    }

    /**
     * Load this world or instance
     */
    public void load() {

    }

    /**
     * Add world systems to the engine.
     */
    public void addWorldSystems() {
        engine.addSystem(movementSystem = new EntityMovementSystem());
    }

    /**
     * @return configuration for this world
     */
    public WorldConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @return Box2d world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Update this world
     *
     * @param d delta time.
     * @return capped frame time
     */
    public float update(float d) {
        final float delta = Math.min(d, configuration.maxFrameTime);

        // step world
        if (configuration.handlePhysics) {
            stepPhysicsSimulation(delta);
        }

        // update all types of entities
        if (configuration.updateEntities)
            for (E entity : entities.values()) entity.update(delta);

        // update local
        if (configuration.updatePlayer) {
            player.interpolatePosition(player.getInterpolationAmount());
            player.update(delta);
        }

        // update network
        if (configuration.updateNetworkPlayers) {
            for (LunarNetworkEntityPlayer player : players.values()) {
                player.interpolatePosition(player.getInterpolationAmount());
                player.update(delta);
            }
        }

        if (configuration.updateEngine) {
            engine.update(delta);
        }

        return delta;
    }

    /**
     * Step physics simulation using the {@code configuration}
     *
     * @param delta delta time
     */
    public void stepPhysicsSimulation(float delta) {
        accumulator += delta;

        while (accumulator >= configuration.stepTime) {
            if (configuration.updateNetworkPlayers) {
                for (N player : players.values()) {
                    player.getPrevious().set(player.getPosition());
                }
            }

            if (configuration.updatePlayer) {
                player.getPrevious().set(player.getPosition());
                player.setPosition(player.getBody().getPosition(), false);
            }

            world.step(configuration.stepTime, configuration.velocityIterations, configuration.positionIterations);
            accumulator -= configuration.stepTime;
        }
    }

    /**
     * Update a players position
     *
     * @param id    their entity ID
     * @param x     their X
     * @param y     their Y
     * @param angle their current angle
     */
    public void updatePlayerPosition(int id, float x, float y, float angle) {
        if (hasNetworkPlayer(id)) {
            getNetworkPlayer(id).updatePosition(x, y, angle);
        }
    }

    /**
     * Update a players velocity
     *
     * @param id    their entity ID
     * @param x     their X
     * @param y     their Y
     * @param angle their current angle
     */
    public void updatePlayerVelocity(int id, float x, float y, float angle) {
        if (hasNetworkPlayer(id)) {
            getNetworkPlayer(id).updateVelocity(x, y, angle);
        }
    }

    /**
     * Update a players position from a raw packet
     *
     * @param packet the packet
     */
    public void updatePlayerPosition(SPacketPlayerPosition packet) {
        if (hasNetworkPlayer(packet.getEntityId())) {
            getNetworkPlayer(packet.getEntityId()).updatePosition(packet.getX(), packet.getY(), packet.getRotation());
        }
    }

    /**
     * Update a players velocity from a raw packet
     *
     * @param packet the packet
     */
    public void updatePlayerVelocity(SPacketPlayerVelocity packet) {
        if (hasNetworkPlayer(packet.getEntityId())) {
            getNetworkPlayer(packet.getEntityId()).updateVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getRotation());
        }
    }

    @Override
    public void dispose() {
        players.clear();
        entities.clear();
        engine.clearPools();
        engine.removeAllSystems();
        world.dispose();
    }
}
