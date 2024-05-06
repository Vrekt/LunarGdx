package gdx.lunar.server.game;

import com.badlogic.gdx.Gdx;
import gdx.lunar.protocol.GdxProtocol;
import gdx.lunar.server.configuration.ServerConfiguration;
import gdx.lunar.server.entity.ServerPlayerEntity;
import gdx.lunar.server.game.utilities.Disposable;
import gdx.lunar.server.network.connection.ServerAbstractConnection;
import gdx.lunar.server.world.AbstractWorldManager;
import gdx.lunar.server.world.impl.WorldManager;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a model for a basic, single, game server.
 */
public abstract class LunarServer implements Disposable {

    private static LunarServer instance;

    protected final List<ServerPlayerEntity> allPlayers = new CopyOnWriteArrayList<>();
    // set of connections that are connected, but not in a world.
    protected final List<ServerAbstractConnection> connections = new CopyOnWriteArrayList<>();
    // the last time it took to tick all worlds.
    protected long worldTickTime;
    protected final AtomicBoolean running = new AtomicBoolean(true);
    protected final ScheduledExecutorService service;

    protected final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    // game version this server supports.
    protected String gameVersion = "1.0";

    protected GdxProtocol protocol;
    protected AbstractWorldManager worldManager;

    public LunarServer(GdxProtocol protocol) {
        instance = this;
        this.service = Executors.newScheduledThreadPool(0, Thread.ofVirtual().factory());
        this.worldManager = new WorldManager();
        this.protocol = protocol;
    }

    public GdxProtocol getProtocol() {
        return protocol;
    }

    /**
     * Attempt to authenticate a new player into the server
     *
     * @param version         the players game version
     * @param protocolVersion the player protocol version
     * @return {@code true} if the player is allowed to join, other-wise connection will be closed.
     */
    public abstract boolean authenticatePlayer(String version, int protocolVersion);

    /**
     * Handle the join process of a new player connection
     *
     * @param connection their connection
     * @return {@code true} if successful, other-wise connection will be closed.
     */
    public abstract boolean addPlayerToServer(ServerAbstractConnection connection);

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
    public void setWorldManager(AbstractWorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * @return the world manager
     */
    public AbstractWorldManager getWorldManager() {
        return worldManager;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public int getProtocolVersion() {
        return protocol.getProtocolVersion();
    }

    public long getWorldTickTime() {
        return worldTickTime;
    }

    /**
     * Set player joined
     * TODO: Possibly remove
     * TODO: Depending on config, maybe just handle max X amount of players per world
     * TODO: Or max X amount of players per server
     *
     * @param player the player
     */
    public void handlePlayerConnection(ServerPlayerEntity player) {
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
    public void handlePlayerDisconnect(ServerPlayerEntity player) {
        this.allPlayers.remove(player);
        this.connections.remove(player.getConnection());
    }

    /**
     * Test if server is at capacity.
     *
     * @return {@code true} if the player can join.
     */
    public boolean isFull() {
        return allPlayers.size() + 1 >= getConfiguration().maxPlayers;
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * Execute an async task to be run the next server tick
     *
     * @param task the task
     */
    public void executeAsyncTaskOnNextTick(Runnable task) {
        tasks.add(task);
    }

    /**
     * Execute an async task and execute it now.
     *
     * @param task the task
     */
    public void executeAsyncTaskNow(Runnable task) {
        service.schedule(task, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * Execute an async task after the provided delay has elapsed.
     *
     * @param task  the task
     * @param delay the delay
     */
    public void executeAsyncTaskLater(Runnable task, long delay) {
        service.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    public List<ServerPlayerEntity> getAllPlayers() {
        return allPlayers;
    }

    /**
     * Start this server.
     */
    public void start() {
        worldTickTime = 0;

        service.scheduleAtFixedRate(this::tick, 0L, (1000 / getConfiguration().ticksPerSecond), TimeUnit.MILLISECONDS);
    }

    /**
     * Suspend (pause) this server
     * TODO: ExecutorService thread is still running, perhaps in the future stop that too.
     */
    public void suspend() {
        running.set(false);
    }

    /**
     * Resume this server
     */
    public void resume() {
        running.set(true);
    }

    /**
     * Tick this server.
     */
    public void tick() {
        if (!running.get()) {
            return;
        }

        try {
            tickAllWorlds();
            runAllTasks();

            // cap max ticks to skip to 50.
            final long time = worldTickTime / 50;
            // amount of ticks to skip if falling behind.
            long ticksToSkip = time >= 50 ? 50 : time;

            if (ticksToSkip > 1) {
                Gdx.app.log("LunarServer", "WARNING: Running %d ms behind! Skipping %d ticks".formatted(worldTickTime, ticksToSkip));
            }

            if (ticksToSkip != 0) {
                while (ticksToSkip > 0) {
                    ticksToSkip--;
                    tickAllWorlds();
                }
                worldTickTime = System.currentTimeMillis();
            }

        } catch (Exception exception) {
            Gdx.app.log("LunarServer", "Exception caught during tick phase", exception);
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
     * Update all worlds within the server.
     */
    protected void tickAllWorlds() {
        final long now = System.currentTimeMillis();
        worldManager.update(worldTickTime);
        worldTickTime = System.currentTimeMillis() - now;
    }

    /**
     * Run all tasks
     */
    protected void runAllTasks() {
        while (tasks.peek() != null) {
            tasks.remove().run();
        }
    }

    public static LunarServer getServer() {
        return instance;
    }

    @Override
    public void dispose() {
        running.set(false);
        allPlayers.clear();
        connections.clear();
        tasks.clear();
        worldManager.dispose();
        service.shutdown();
    }
}
