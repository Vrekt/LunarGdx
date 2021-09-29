package gdx.lunar.server;

import gdx.lunar.server.game.entity.player.Player;
import gdx.lunar.server.world.World;
import gdx.lunar.server.world.WorldManager;
import gdx.lunar.server.world.impl.BasicWorldManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base implementation of the server.
 */
public abstract class LunarServer {

    private static LunarServer instance;

    /**
     * Max amount of worlds allowed per thread.
     * Max amount of players per world.
     * Max amount of players in general.
     */
    protected int maxWorldsPerThread, maxPlayersPerWorld, maxPlayers;
    /**
     * Max worlds allowed in general.
     */
    protected int maxWorlds;

    /**
     * Default amount of time to sleep after each tick
     * only if the server is not behind.
     */
    protected long tickSleepTime = 50;

    /**
     * All players connected to this server regardless of world or instance.
     */
    protected final Map<String, Player> allPlayers = new HashMap<>();

    /**
     * The last world tick time
     */
    protected long worldTickTime;

    /**
     * The amount of ticks to skip.
     */
    private long ticksToSkip;
    protected final AtomicBoolean running = new AtomicBoolean(true);
    protected final ExecutorService service;

    protected WorldManager worldManager;

    public LunarServer() {
        instance = this;
        this.service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.worldManager = new BasicWorldManager();
    }

    /**
     * Set the world manager to use.
     *
     * @param worldManager world manager.
     */
    public void setWorldManager(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * Handle a player disconnect.
     * This could be caused by connection errors or kicking.
     *
     * @param player the player
     */
    public abstract void handlePlayerDisconnect(Player player);

    /**
     * Start this server.
     */
    public void start() {
        worldTickTime = 0;

        service.execute(() -> {
            Thread.currentThread().setName("LunarWorldTick");
            Thread.currentThread().setUncaughtExceptionHandler((t, e) -> e.printStackTrace());

            while (running.get()) {
                try {
                    worldTick();

                    // cap max ticks to skip to 50.
                    final long time = worldTickTime / 50;
                    ticksToSkip = time >= 50 ? 50 : time;

                    if (ticksToSkip > 1) {
                        System.err.println("Running " + worldTickTime + " ms behind, skipping " + ticksToSkip + " ticks.");
                    }

                    if (ticksToSkip != 0) {
                        while (ticksToSkip > 0) {
                            ticksToSkip--;
                            worldTick();
                        }
                        worldTickTime = System.currentTimeMillis();
                    }

                    waitUntilNextTick();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    running.compareAndSet(true, false);
                }
            }
        });
    }

    /**
     * Stop the server.
     */
    public void stop() {
        running.compareAndSet(true, false);

        service.shutdownNow();
        worldManager.dispose();
    }

    /**
     * @return the world manager
     */
    public WorldManager getWorldManager() {
        return worldManager;
    }

    /**
     * Wait until the next tick
     * TODO: Not desirable but don't know what else to do.
     *
     * @throws InterruptedException e
     */
    private void waitUntilNextTick() throws InterruptedException {
        Thread.sleep(tickSleepTime);
    }

    /**
     * Update worlds
     */
    private void worldTick() {
        final long now = System.currentTimeMillis();
        for (World value : worldManager.getWorlds()) value.tick();
        worldTickTime = System.currentTimeMillis() - now;
    }

    public static LunarServer getServer() {
        return instance;
    }
}
