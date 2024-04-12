package gdx.lunar.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.utilities.PlayerSupplier;
import lunar.shared.contact.PlayerCollisionListener;
import lunar.shared.entity.LunarEntity;
import lunar.shared.entity.player.mp.LunarEntityNetworkPlayer;
import lunar.shared.entity.player.LunarEntityPlayer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a single game world that should be expanded upon
 */
public abstract class AbstractGameWorld<P extends LunarEntityNetworkPlayer, E extends LunarEntity> implements TypedGameWorld<P, E> {

    // network players and entities
    protected ConcurrentMap<Integer, P> players = new ConcurrentHashMap<>();
    protected ConcurrentMap<Integer, E> entities = new ConcurrentHashMap<>();

    protected final PlayerSupplier playerSupplier;
    protected final World world;

    protected WorldConfiguration configuration;
    protected Engine engine;

    protected final Vector2 spawn = new Vector2();

    protected float accumulator;
    protected String worldName;
    // current tick of this world
    protected float currentTick;

    /**
     * An empty default constructor. You should use the setters to define configuration next.
     *
     * @param playerSupplier the player supplier
     * @param world          the world
     * @param configuration  config
     * @param engine         engine
     */
    public AbstractGameWorld(PlayerSupplier playerSupplier, World world, WorldConfiguration configuration, Engine engine) {
        this.playerSupplier = playerSupplier;
        this.world = world;
        this.configuration = configuration;
        this.engine = engine;
    }

    /**
     * An empty default constructor. You should use the setters to define configuration next.
     *
     * @param playerSupplier the player supplier
     * @param world          the world
     * @param engine         engine
     */
    public AbstractGameWorld(PlayerSupplier playerSupplier, World world, Engine engine) {
        this.playerSupplier = playerSupplier;
        this.world = world;
        this.configuration = new WorldConfiguration();
        this.engine = engine;
    }

    public <T extends LunarEntityPlayer> T getPlayer() {
        return playerSupplier.getPlayer();
    }

    @Override
    public void setEntityEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    public Engine getEntityEngine() {
        return engine;
    }

    @Override
    public World getEntityWorld() {
        return world;
    }

    @Override
    public void setWorldSpawn(Vector2 position) {
        spawn.set(position);
    }

    @Override
    public Vector2 getWorldSpawn() {
        return spawn;
    }

    @Override
    public WorldConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(WorldConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void addDefaultPlayerCollisionListener() {
        if (world != null) world.setContactListener(new PlayerCollisionListener());
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    @Override
    public boolean isFull() {
        return players.size() >= configuration.maxPlayerCapacity;
    }

    @Override
    public boolean hasPlayer(int entityId) {
        return players.containsKey(entityId);
    }

    @Override
    public boolean hasEntity(int entityId) {
        return entities.containsKey(entityId);
    }

    @Override
    public void addPlayer(P player) {
        players.put(player.getEntityId(), player);
    }

    @Override
    public void addEntity(E entity) {
        entities.put(entity.getEntityId(), entity);
    }

    /**
     * Get a player by ID
     *
     * @param id their ID
     * @return the player
     */
    public P getPlayer(int id) {
        return players.get(id);
    }

    /**
     * Get an entity by ID
     *
     * @param id their ID
     * @return the entity
     */
    public E getEntity(int id) {
        return entities.get(id);
    }

    @Override
    public ConcurrentMap<Integer, P> getPlayers() {
        return players;
    }

    @Override
    public ConcurrentMap<Integer, E> getEntities() {
        return entities;
    }

    @Override
    public void spawnEntityInWorld(LunarEntity entity, Vector2 position) {
        addEntity((E) entity);
        entity.setPosition(position, true);
    }

    @Override
    public void spawnEntityInWorld(LunarEntity entity) {
        addEntity((E) entity);
        entity.setPosition(spawn, true);
    }

    @Override
    public void spawnPlayerInWorld(LunarEntityPlayer player, Vector2 position) {
        addPlayer((P) player);
        player.setPosition(position, true);
    }

    @Override
    public void spawnPlayerInWorld(LunarEntityPlayer player) {
        addPlayer((P) player);
        player.setPosition(spawn, true);
    }

    @Override
    public void removeEntityInWorld(int entityId) {
        if (hasEntity(entityId)) {
            // TODO Entity destroy
            getEntities().get(entityId).removeFromWorld();
            getEntities().remove(entityId);
        }
    }

    @Override
    public void removePlayerInWorld(int entityId, boolean destroy) {
        if (hasPlayer(entityId)) {
            if (destroy) getPlayers().get(entityId).removeFromWorld();
            getPlayers().remove(entityId);
        }
    }

    @Override
    public void updatePlayerProperties(int id, String name, int entityId) {
        if (hasPlayer(id)) {
            getPlayers().get(id).setProperties(name, entityId);
        }
    }

    @Override
    public float update(float delta) {
        final float capped = Math.min(delta, configuration.maxFrameTime);

        // step world
        if (configuration.handlePhysics) {
            stepPhysicsSimulation(capped);
        }

        // update all types of entities
        if (configuration.updateEntities)
            for (E entity : getEntities().values()) entity.update(capped);

        // update local
        if (configuration.updateLocalPlayer) {
            getPlayer().interpolatePosition();
            getPlayer().update(capped);
        }

        // update network
        if (configuration.updateNetworkPlayers) {
            for (P player : getPlayers().values()) {
                player.interpolatePosition();
                player.update(capped);
            }
        }

        if (configuration.updateEntityEngine) {
            engine.update(capped);
        }

        return capped;
    }

    @Override
    public void stepPhysicsSimulation(float delta) {
        accumulator += delta;

        while (accumulator >= configuration.stepTime) {
            // update the previous position of these players
            // for interpolation later, if enabled.
            if (configuration.updateNetworkPlayers) {
                for (P player : getPlayers().values()) {
                    player.getPreviousPosition().set(player.getPosition());
                }
            }

            // update our local player
            if (configuration.updateLocalPlayer) {
                getPlayer().getPreviousPosition().set(getPlayer().getPosition());
                getPlayer().setPosition(getPlayer().getBody().getPosition(), false);
            }

            world.step(configuration.stepTime, configuration.velocityIterations, configuration.positionIterations);
            accumulator -= configuration.stepTime;
            currentTick++;
        }
    }

    @Override
    public void updatePlayerPositionInWorld(int entityId, float x, float y, float angle) {
        getPlayers().get(entityId).updatePosition(x, y, angle);
    }

    @Override
    public void updatePlayerVelocityInWorld(int entityId, float x, float y, float angle) {
        getPlayers().get(entityId).updateVelocity(x, y, angle);
    }

    @Override
    public void dispose() {
        engine.removeAllEntities();
        engine.removeAllSystems();
        getPlayers().clear();
        getEntities().clear();
        world.dispose();
    }
}
