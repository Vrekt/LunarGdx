package gdx.lunar.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntMap;
import lunar.shared.contact.PlayerCollisionListener;
import lunar.shared.entity.LunarEntity;
import lunar.shared.entity.player.LunarEntityNetworkPlayer;
import lunar.shared.entity.player.LunarEntityPlayer;

/**
 * Represents a single game world that should be expanded upon
 */
public abstract class AbstractGameWorld<P extends LunarEntityNetworkPlayer, E extends LunarEntity>
        implements TypedGameWorld<P, E> {

    // network players and entities
    protected IntMap<P> players = new IntMap<>();
    protected IntMap<E> entities = new IntMap<>();

    protected World world;

    protected WorldConfiguration configuration;
    protected Engine engine;

    protected final Vector2 worldOrigin = new Vector2();

    protected float accumulator;
    protected String worldName;

    /**
     * An empty default constructor. You should use the setters to define configuration next.
     *
     * @param world         the world
     * @param configuration config
     * @param engine        engine
     */
    public AbstractGameWorld(World world, WorldConfiguration configuration, Engine engine) {
        this.world = world;
        this.configuration = configuration;
        this.engine = engine;
    }

    /**
     * An empty default constructor. You should use the setters to define configuration next.
     *
     * @param world  the world
     * @param engine engine
     */
    public AbstractGameWorld(World world, Engine engine) {
        this.world = world;
        this.configuration = new WorldConfiguration();
        this.engine = engine;
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
    public void setWorldOrigin(Vector2 position) {
        worldOrigin.set(position);
    }

    @Override
    public Vector2 getWorldOrigin() {
        return worldOrigin;
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
        return players.size >= configuration.maxPlayerCapacity;
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
    public IntMap<P> getPlayers() {
        return players;
    }

    @Override
    public IntMap<E> getEntities() {
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
        entity.setPosition(worldOrigin, true);
    }

    @Override
    public void spawnPlayerInWorld(LunarEntityPlayer player, Vector2 position) {
        addPlayer((P) player);
        player.setPosition(position, true);
    }

    @Override
    public void spawnPlayerInWorld(LunarEntityPlayer player) {
        addPlayer((P) player);
        player.setPosition(worldOrigin, true);
    }

    @Override
    public void removeEntityInWorld(int entityId, boolean destroy) {
        if (hasEntity(entityId)) {
            final E entity = getEntities().remove(entityId);
            entities.remove(entityId);

            entity.removeFromWorld();
            if (destroy) entity.dispose();
        }
    }

    @Override
    public void removePlayerInWorld(int entityId, boolean destroy) {
        if (hasPlayer(entityId)) {
            final P player = getPlayers().get(entityId);
            players.remove(entityId);

            player.removeFromWorld();

            if (destroy) player.dispose();
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

            world.step(configuration.stepTime, configuration.velocityIterations, configuration.positionIterations);
            accumulator -= configuration.stepTime;
        }
    }

    @Override
    public void updatePlayerPositionInWorld(int entityId, float x, float y, float angle) {
        getPlayers().get(entityId).updatePositionFromNetwork(x, y, angle);
    }

    @Override
    public void updatePlayerVelocityInWorld(int entityId, float x, float y, float angle) {
        getPlayers().get(entityId).updateVelocityFromNetwork(x, y, angle);
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
