package gdx.lunar.world.v2;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.entity.playerv2.LunarEntity;
import gdx.lunar.entity.playerv2.LunarEntityPlayer;
import gdx.lunar.entity.playerv2.mp.LunarNetworkEntityPlayer;
import gdx.lunar.entity.system.animation.EntityAnimationSystem;
import gdx.lunar.entity.system.moving.EntityMovementSystem;
import gdx.lunar.network.AbstractConnection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a networked world players and entities exist in.
 *
 * @param <P> any local entity player instance type you wish.
 * @param <N> any local network entity instance type you wish
 * @param <E> any local entity instance type you wish
 */
public abstract class LunarWorld<P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> implements Disposable {

    // engine for this world.
    protected PooledEngine engine;

    // system
    protected EntityMovementSystem movementSystem;
    protected EntityAnimationSystem animationSystem;

    // network players and entities
    protected ConcurrentMap<Integer, N> players = new ConcurrentHashMap<>();
    protected ConcurrentMap<Integer, E> entities = new ConcurrentHashMap<>();

    // starting/spawn point of this world.
    protected final Vector2 spawn = new Vector2();

    // local player
    protected final P player;
    protected final World world;

    // config
    protected WorldConfiguration configuration;
    protected float accumulator;

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

        addWorldSystems();
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

        addWorldSystems();
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
     * Add world systems to the engine.
     */
    public void addWorldSystems() {
        movementSystem = new EntityMovementSystem();
        animationSystem = new EntityAnimationSystem();
        engine.addSystem(movementSystem);
        engine.addSystem(animationSystem);
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
     * @return network players in this world
     */
    public ConcurrentMap<Integer, N> getPlayers() {
        return players;
    }

    /**
     * @return entities in this world
     */
    public ConcurrentMap<Integer, E> getEntities() {
        return entities;
    }

    public Vector2 getSpawn() {
        return spawn;
    }

    public void setSpawn(Vector2 where) {
        this.spawn.set(where);
    }

    public void setSpawn(float x, float y) {
        this.spawn.set(x, y);
    }

    /**
     * Spawn the entity in this world
     *
     * @param entity the entity
     * @param x      location X
     * @param y      location Y
     */
    public void spawnEntityInWorld(LunarEntity entity, float x, float y) {
        engine.addEntity(entity.getEntity());
        selectType(entity);

        entity.getInstance().worldIn = this;
    }

    /**
     * This method will spawn the entity in this world at the spawn location
     *
     * @param entity the entity
     */
    public void spawnEntityInWorld(LunarEntity entity) {
        engine.addEntity(entity.getEntity());
        selectType(entity);

        entity.getInstance().worldIn = this;
    }

    /**
     * Add the entity to the internal lists
     *
     * @param entity entity
     */
    private void selectType(LunarEntity entity) {
        if (entity instanceof LunarNetworkEntityPlayer) {
            this.players.put(entity.getProperties().entityId, (N) entity);
        } else {
            this.entities.put(entity.getProperties().entityId, (E) entity);
        }
    }

    /**
     * Remove the entity from this world
     *
     * @param entity the entity
     */
    public void removeEntityInWorld(LunarEntity entity) {
        engine.removeEntity(entity.getEntity());
        if (entity instanceof LunarNetworkEntityPlayer) {
            this.players.remove(entity.getProperties().entityId);
        } else {
            this.entities.remove(entity.getProperties().entityId);
        }
    }

    /**
     * Remove the entity from this world
     *
     * @param entity   the entity
     * @param isPlayer if the entity is a player
     */
    public void removeEntityInWorld(int entity, boolean isPlayer) {
        if (isPlayer && this.players.containsKey(entity)) {
            final N player = this.players.get(entity);
            player.removeEntityInWorld(this);
        } else if (this.entities.containsKey(entity)) {
            final E e = this.entities.get(entity);
            e.removeEntityInWorld(this);
        }
    }

    public boolean hasNetworkPlayer(int id) {
        return this.players.containsKey(id);
    }

    public N getNetworkPlayer(int id) {
        return this.players.get(id);
    }

    /**
     * Update this world
     *
     * @param d delta time.
     */
    public void update(float d) {
        final float delta = Math.min(d, configuration.maxFrameTime);

        // step world
        if (configuration.handlePhysics) {
            stepPhysicsSimulation(delta);
        }

        // update all types of entities
        if (configuration.updateEntities)
            for (E value : entities.values()) value.update(delta);

        // update local
        if (configuration.updatePlayer) {
            player.interpolatePosition(0.5f);
            player.update(delta);
        }

        // update network
        if (configuration.updateNetworkPlayers) {
            for (LunarNetworkEntityPlayer player : players.values()) {
                player.interpolatePosition(0.5f);
                player.update(delta);
            }
        }

        if (configuration.updateEngine) {
            engine.update(delta);
        }
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
                    player.getPrevious().set(player.getBody().getPosition());
                }
            }

            player.getPrevious().set(player.getBody().getPosition());

            world.step(configuration.stepTime, configuration.velocityIterations, configuration.positionIterations);
            accumulator -= configuration.stepTime;
        }
    }

    @Override
    public void dispose() {
        players.clear();
        entities.clear();
        engine.clearPools();
    }
}
