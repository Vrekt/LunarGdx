package gdx.lunar.server;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.configuration.ServerConfiguration;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.game.utilities.Disposable;
import gdx.lunar.server.network.ServerAbstractConnection;
import gdx.lunar.server.world.WorldManager;
import gdx.lunar.server.world.impl.WorldManagerAdapter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a model for a basic, single, game server.
 */
public abstract class LunarServer implements Disposable {

    private static LunarServer instance;

    // TODO: Probably low performance with a COW.
    protected final List<LunarServerPlayerEntity> allPlayers = new CopyOnWriteArrayList<>();
    // set of connections that are connected, but not in a world.
    protected final List<ServerAbstractConnection> connections = new CopyOnWriteArrayList<>();
    // the last time it took to tick all worlds.
    protected long worldTickTime;
    protected final AtomicBoolean running = new AtomicBoolean(true);
    protected final ExecutorService service;

    // game version this server supports.
    protected String gameVersion = "1.0";

    protected LunarProtocol protocol;
    protected WorldManager worldManager;

    public LunarServer(LunarProtocol protocol) {
        instance = this;
        this.service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.worldManager = new WorldManagerAdapter();
        this.protocol = protocol;
    }

    /**
     * Initialize
     *
     * @param threads  amount of threads to use within server executor.
     * @param protocol current protocol in use.
     */
    public LunarServer(int threads, LunarProtocol protocol) {
        instance = this;
        this.service = Executors.newFixedThreadPool(threads);
        this.worldManager = new WorldManagerAdapter();
        this.protocol = protocol;
    }

    /**
     * Attempt to authenticate a new player into the server
     *
     * @param version         the players game version
     * @param protocolVersion the player protocol version
     * @return {@code true} if the player is allowed to join, other-wise connection will be closed.
     */
    public abstract boolean handlePlayerAuthentication(String version, int protocolVersion);

    /**
     * Handle the join process of a new player connection
     *
     * @param connection their connection
     * @return {@code true} if successful, other-wise connection will be closed.
     */
    public abstract boolean handleJoinProcess(ServerAbstractConnection connection);

    /**
     * Check if a username is valid.
     *
     * @param username the username
     * @param world    the world
     * @return {@code true} if so
     */
    public abstract boolean isUsernameValidInWorld(String world, String username);

    /**
     * Retrieve the server configuration
     *
     * @return the configuration
     */
    public abstract ServerConfiguration getConfiguration();

    /**
     * Set the world manager to use.
     *
     * @param worldManager world manager.
     */
    public void setWorldManager(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * @return the world manager
     */
    public WorldManager getWorldManager() {
        return worldManager;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * Set player joined
     *
     * @param player the player
     */
    public void handlePlayerConnection(LunarServerPlayerEntity player) {
        this.allPlayers.add(player);
    }

    public void removePlayerConnection(ServerAbstractConnection connection) {
        this.connections.remove(connection);
    }

    public void handleConnection(ServerAbstractConnection connection) {
        this.connections.add(connection);
    }

    /**
     * Handle a player disconnection.
     *
     * @param player the player
     */
    public void handlePlayerDisconnect(LunarServerPlayerEntity player) {
        this.allPlayers.remove(player);
        this.connections.remove(player.getServerConnection());
    }

    /**
     * Test if server is at capacity.
     *
     * @return {@code true} if the player can join.
     */
    public boolean isFull() {
        return allPlayers.size() + 1 >= getConfiguration().maxPlayers;
    }

    public List<LunarServerPlayerEntity> getAllPlayers() {
        return allPlayers;
    }

    /**
     * Start this server.
     */
    public void start() {
        worldTickTime = 0;

        service.execute(() -> {
            Thread.currentThread().setName("LunarServerTick");
            Thread.currentThread().setUncaughtExceptionHandler((t, e) -> e.printStackTrace());

            while (running.get()) {
                tick();
            }
        });
    }

    /**
     * Tick this server.
     */
    public void tick() {
        try {
            tickAllWorlds();

            // cap max ticks to skip to 50.
            final long time = worldTickTime / 50;
            // amount of ticks to skip if falling behind.
            long ticksToSkip = time >= 50 ? 50 : time;

            if (ticksToSkip > 1) {
                System.err.println("[WARNING]: Running " + worldTickTime + " ms behind, skipping " + ticksToSkip + " ticks.");
            }

            if (ticksToSkip != 0) {
                while (ticksToSkip > 0) {
                    ticksToSkip--;
                    tickAllWorlds();
                }
                worldTickTime = System.currentTimeMillis();
            }

            waitUntilNextTick();
        } catch (Exception exception) {
            exception.printStackTrace();
            running.compareAndSet(true, false);
        }
    }

    /**
     * Stop the server.
     */
    public void stop() {
        running.compareAndSet(true, false);

        service.shutdownNow();
        this.dispose();
    }

    /**
     * Wait until the next tick
     * TODO: Not desirable, should be overridden if different implementation is desired.
     *
     * @throws InterruptedException e
     */
    protected void waitUntilNextTick() throws InterruptedException {
        Thread.sleep(getConfiguration().tickSleepTime);
    }

    /**
     * Update all worlds within the server.
     */
    protected void tickAllWorlds() {
        final long now = System.currentTimeMillis();
        worldManager.update(worldTickTime);
        worldTickTime = System.currentTimeMillis() - now;
    }

    public static LunarServer getServer() {
        return instance;
    }

    @Override
    public void dispose() {
        worldManager.dispose();
        allPlayers.clear();
        running.set(false);
        service.shutdown();
    }
}
