package gdx.lunar.server;

import gdx.lunar.server.game.entity.player.Player;
import gdx.lunar.server.world.World;
import gdx.lunar.server.world.WorldManager;
import gdx.lunar.server.world.impl.WorldManagerAdapter;
import gdx.lunar.server.world.lobby.LobbyWorldAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base implementation of the server.
 */
public abstract class LunarServer {

    private static LunarServer instance;

    /**
     * Max amount of worlds allowed per thread.
     * Max amount of players in general.
     */
    protected int maxWorldsPerThread, maxPlayers = 2000;

    /**
     * Max worlds allowed in general.
     * Max lobbies allowed in general.
     */
    protected int maxWorlds = 100, maxLobbies = 100;

    /**
     * Default amount of time to sleep after each tick
     * only if the server is not behind.
     */
    protected long tickSleepTime = 50;

    /**
     * All players connected to this server regardless of world or instance.
     */
    // TODO: Probably low performance with a COW.
    protected final List<Player> allPlayers = new CopyOnWriteArrayList<>();
    // map of all lobbies
    protected final Map<Integer, World> lobbies = new HashMap<>();
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
        this.worldManager = new WorldManagerAdapter();
    }

    /**
     * Set the world manager to use.
     *
     * @param worldManager world manager.
     */
    public void setWorldManager(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public void setMaxWorldsPerThread(int maxWorldsPerThread) {
        this.maxWorldsPerThread = maxWorldsPerThread;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMaxWorlds(int maxWorlds) {
        this.maxWorlds = maxWorlds;
    }

    public void setMaxLobbies(int maxLobbies) {
        this.maxLobbies = maxLobbies;
    }

    /**
     * Handle a player disconnect.
     * This could be caused by connection errors or kicking.
     *
     * @param player the player
     */
    public void handlePlayerDisconnect(Player player) {
        this.allPlayers.remove(player);
    }

    /**
     * Test if server is at capacity.
     *
     * @return {@code true} if the player can join.
     */
    public boolean canPlayerJoin() {
        return allPlayers.size() + 1 <= maxPlayers;
    }

    /**
     * Set player joined
     *
     * @param player the player
     */
    public void setPlayerJoined(Player player) {
        this.allPlayers.add(player);
    }

    /**
     * @return {@code true} if the player can create a lobby.
     */
    public boolean canCreateLobby() {
        return lobbies.size() + 1 <= maxLobbies;
    }

    /**
     * Same concept as {@link World#assignEntityId()}
     * Slightly larger ID range.
     *
     * @return the lobby id.
     */
    protected synchronized int assignLobbyId() {
        return lobbies.size() + 1 + ThreadLocalRandom.current().nextInt(1111, 9999);
    }

    /**
     * Create a new lobby within this server.
     *
     * @return if **not** overridden, returns {@link gdx.lunar.server.world.lobby.LobbyWorldAdapter} as default.
     */
    public World createNewLobby() {
        final int id = assignLobbyId();
        final World lobby = new LobbyWorldAdapter();
        lobby.setWorldLobbyId(id);

        this.lobbies.put(id, lobby);
        return lobby;
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
     * Update worlds and lobbies.
     */
    protected void worldTick() {
        final long now = System.currentTimeMillis();
        for (World value : worldManager.getWorlds()) value.tick();
        for (World lobby : lobbies.values()) lobby.tick();
        worldTickTime = System.currentTimeMillis() - now;
    }

    public static LunarServer getServer() {
        return instance;
    }
}
