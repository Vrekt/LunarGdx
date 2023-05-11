package gdx.lunar.server.world.config;

/**
 * A per world configuration
 */
public class ServerWorldConfiguration {

    // max packets to process per tick.
    // max capacity allowed in the world.
    protected int maxPacketsPerTick = 100, capacity = 100;

    // max entities allowed in the world.
    protected int maxEntities = 100;

    // how long a player can go until timing out (no packet sends)
    protected long playerTimeoutMs = 3000;

    // world will handle ticking itself and update all players each tick
    public boolean doDefaultTicking = true;

    public ServerWorldConfiguration(int maxPacketsPerTick, int capacity, int maxEntities, long playerTimeoutMs) {
        this.maxPacketsPerTick = maxPacketsPerTick;
        this.capacity = capacity;
        this.maxEntities = maxEntities;
        this.playerTimeoutMs = playerTimeoutMs;
    }

    public ServerWorldConfiguration() {
    }

    public int getMaxPacketsPerTick() {
        return maxPacketsPerTick;
    }

    public void setMaxPacketsPerTick(int maxPacketsPerTick) {
        this.maxPacketsPerTick = maxPacketsPerTick;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMaxEntities() {
        return maxEntities;
    }

    public void setMaxEntities(int maxEntities) {
        this.maxEntities = maxEntities;
    }

    public long getPlayerTimeoutMs() {
        return playerTimeoutMs;
    }

    public void setPlayerTimeoutMs(long playerTimeoutMs) {
        this.playerTimeoutMs = playerTimeoutMs;
    }
}
