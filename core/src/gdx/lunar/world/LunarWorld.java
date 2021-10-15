package gdx.lunar.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.entity.LunarEntity;
import gdx.lunar.entity.drawing.Rotation;
import gdx.lunar.entity.network.NetworkEntity;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;
import gdx.lunar.network.AbstractConnection;
import gdx.lunar.protocol.packet.client.CPacketBodyForce;
import gdx.lunar.protocol.packet.client.CPacketRequestSpawnEntity;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a networked game world.
 */
public abstract class LunarWorld implements Disposable {

    protected ConcurrentMap<Integer, LunarNetworkEntityPlayer> players = new ConcurrentHashMap<>();
    protected ConcurrentMap<Integer, LunarEntity> entities = new ConcurrentHashMap<>();

    protected final LunarEntityPlayer player;
    protected final World world;
    protected float worldScale;
    protected boolean handlePhysics, updatePlayer, updateNetworkPlayers;
    protected boolean updateEntities;

    protected float stepTime = 1.0f / 60.0f;
    protected float maxFrameTime = 0.25f;
    protected float accumulator;

    protected int velocityIterations = 8, positionIterations = 3;
    // if this world is being used as a lobby.
    protected int lobbyId;

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
    public LunarWorld(LunarEntityPlayer player, World world, float worldScale,
                      boolean handlePhysics, boolean updatePlayer, boolean updateNetworkPlayers,
                      boolean updateEntities) {
        this.player = player;
        this.world = world;
        this.worldScale = worldScale;
        this.handlePhysics = handlePhysics;
        this.updatePlayer = updatePlayer;
        this.updateNetworkPlayers = updateNetworkPlayers;
        this.updateEntities = updateEntities;
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

        this.handlePhysics = true;
        this.updatePlayer = true;
        this.updateNetworkPlayers = true;
        this.updateEntities = true;
    }

    public void setStepTime(float stepTime) {
        this.stepTime = stepTime;
    }

    public float getStepTime() {
        return stepTime;
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

    public void setUpdateEntities(boolean updateEntities) {
        this.updateEntities = updateEntities;
    }

    /**
     * Allows you to use internal collections in your project.
     * Should override {@code setPlayerInWorld}
     *
     * @param players c
     */
    @SuppressWarnings("unchecked")
    public <T extends LunarNetworkEntityPlayer> void setPlayersCollection(ConcurrentMap<Integer, T> players) {
        this.players = (ConcurrentMap<Integer, LunarNetworkEntityPlayer>) players;
    }

    /**
     * Allows you to use internal collections in your project.
     * Should override {@code setPlayerInWorld}
     *
     * @param entities c
     */
    @SuppressWarnings("unchecked")
    public <T extends LunarEntity> void setEntitiesCollection(ConcurrentMap<Integer, T> entities) {
        this.entities = (ConcurrentMap<Integer, LunarEntity>) entities;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getLobbyId() {
        return lobbyId;
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
     * Set an entity in this world
     *
     * @param entity the entity
     */
    public void setEntityInWorld(LunarEntity entity) {
        this.entities.put(entity.getEntityId(), entity);
    }

    /**
     * Set an entity in this world and broadcast to others.
     *
     * @param entity the entity
     */
    public void setEntityInWorldNetwork(AbstractConnection connection, LunarEntity entity) {
        // ensure we have a temporary entity ID before continuing.
        if (entity.getEntityId() == 0) {
            entity.setEntityId(ThreadLocalRandom.current().nextInt());
        }
        setEntityInWorld(entity);
        connection.send(new CPacketRequestSpawnEntity(connection.alloc(), entity.getName(), entity.getX(), entity.getY(), entity.getEntityId()));
    }

    /**
     * Check if a temporary entity exists by ID
     *
     * @param temporaryEntityId the temporary ID
     * @param entityId          the new entity ID
     * @return {@code true} if so
     */
    public boolean resetTemporaryEntityIfExists(int temporaryEntityId, int entityId) {
        if (this.entities.containsKey(temporaryEntityId)) {
            this.entities.get(temporaryEntityId).setEntityId(entityId);
            return true;
        }
        return false;
    }

    /**
     * The request was denied from the server, so remove it.
     *
     * @param temporaryEntityId the entity ID.
     */
    public void removeTemporaryEntity(int temporaryEntityId) {
        this.entities.remove(temporaryEntityId);
    }

    /**
     * Set a networked entity in this world.
     *
     * @param entity the entity
     */
    public void setEntityInWorld(NetworkEntity entity) {
        this.entities.put(entity.getEntityId(), entity);
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
     * Remove an entity from this world
     *
     * @param entity the entity
     */
    public void removeEntityFromWorld(LunarEntity entity) {
        this.entities.remove(entity.getEntityId(), entity);
    }

    /**
     * Remove an entity from this world
     *
     * @param entity the entity ID
     */
    public void removeEntityFromWorld(int entity) {
        this.entities.remove(entity);
    }

    /**
     * Get the position of a player
     *
     * @param player entity ID
     * @return the position or {@code null} if no player matching the ID was found.
     */
    public Vector2 getPositionOfPlayer(int player) {
        final LunarNetworkEntityPlayer p = this.players.get(player);
        if (p == null) return null;
        return p.getPosition();
    }

    /**
     * @param player the player
     * @return the player or {@code null} if no player matching the ID was found.
     */
    public LunarNetworkEntityPlayer getPlayer(int player) {
        return this.players.get(player);
    }

    /**
     * Apply force to the local players body and send over the network.
     *
     * @param connection the connection
     * @param fx         force X
     * @param fy         force Y
     * @param px         point X
     * @param py         point Y
     * @param wake       wake
     */
    public void applyForceToPlayerNetwork(AbstractConnection connection, float fx, float fy, float px, float py, boolean wake) {
        connection.send(new CPacketBodyForce(connection.alloc(), player.getEntityId(), fx, fy, px, py));
        this.player.getBody().applyForce(fx, fy, px, py, wake);
    }

    /**
     * Apply a force to another players body and send that over the network to others.
     *
     * @param player     the player
     * @param connection the connection
     * @param fx         force X
     * @param fy         force Y
     * @param px         point X
     * @param py         point Y
     * @param wake       wake
     */
    public void applyForceToOtherPlayerNetwork(int player, AbstractConnection connection, float fx, float fy, float px, float py, boolean wake) {
        applyForceToOtherPlayerNetwork(this.players.get(player), connection, fx, fy, px, py, wake);
    }

    /**
     * Apply a force to another players body and send that over the network to others.
     *
     * @param player     the player
     * @param connection the connection to use
     * @param fx         force X
     * @param fy         force Y
     * @param px         point X
     * @param py         point Y
     * @param wake       wake
     */
    public void applyForceToOtherPlayerNetwork(LunarNetworkEntityPlayer player, AbstractConnection connection, float fx, float fy, float px, float py, boolean wake) {
        if (player == null) return;

        connection.send(new CPacketBodyForce(connection.alloc(), player.getEntityId(), fx, fy, px, py));
        player.getBody().applyForce(fx, fy, px, py, wake);
    }

    /**
     * Apply a force to another entities body and send that over the network to others.
     *
     * @param entityId   the entities ID
     * @param connection the connection to use
     * @param fx         force X
     * @param fy         force Y
     * @param px         point X
     * @param py         point Y
     * @param wake       wake
     */
    public void applyForceToEntityNetwork(int entityId, AbstractConnection connection, float fx, float fy, float px, float py, boolean wake) {
        applyForceToEntityNetwork(this.entities.get(entityId), connection, fx, fy, px, py, wake);
    }

    /**
     * Apply a force to another entities body and send that over the network to others.
     *
     * @param entity     the entity
     * @param connection the connection to use
     * @param fx         force X
     * @param fy         force Y
     * @param px         point X
     * @param py         point Y
     * @param wake       wake
     */
    public void applyForceToEntityNetwork(LunarEntity entity, AbstractConnection connection, float fx, float fy, float px, float py, boolean wake) {
        if (entity == null) return;

        connection.send(new CPacketBodyForce(connection.alloc(), player.getEntityId(), fx, fy, px, py));
        player.getBody().applyForce(fx, fy, px, py, wake);
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
        final float delta = Math.min(d, maxFrameTime);

        if (handlePhysics) {
            accumulator += delta;

            while (accumulator >= stepTime) {
                if (updateNetworkPlayers) {
                    for (LunarNetworkEntityPlayer value : players.values()) {
                        value.preUpdate();
                    }
                }

                player.preUpdate();

                world.step(stepTime, velocityIterations, positionIterations);
                accumulator -= stepTime;
            }
        }
        if (updateEntities) for (LunarEntity value : entities.values()) value.update(delta);

        if (updatePlayer) {
            player.update(delta);
            player.interpolate(0.5f);
        }

        if (updateNetworkPlayers) {
            for (LunarNetworkEntityPlayer value : players.values()) {
                value.update(delta);
                value.interpolate(0.5f);
            }
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
