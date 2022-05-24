package gdx.lunar.server.configuration;

/**
 * Server configuration
 */
public abstract class ServerConfiguration {

    // Max amount of worlds allowed per thread.
    //  Max amount of players in general.
    public int maxWorldsPerThread, maxPlayers = 2000;

    //Max worlds allowed in general.
    // Max lobbies allowed in general.
    public int maxWorlds = 100, maxLobbies = 100;

    // Default amount of time to sleep after each tick
    // only if the server is not behind.
    public long tickSleepTime = 5;
    // allow players to join worlds before setting their username.
    public boolean allowJoinWorldBeforeSetUsername = false;
    public boolean allowChangeUsername = false;

    public void setTickSleepTime(long tickSleepTime) {
        this.tickSleepTime = tickSleepTime;
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

    public void setAllowJoinWorldBeforeSetUsername(boolean allowJoinWorldBeforeSetUsername) {
        this.allowJoinWorldBeforeSetUsername = allowJoinWorldBeforeSetUsername;
    }

    public boolean getAllowJoinWorldBeforeSetUsername() {
        return allowJoinWorldBeforeSetUsername;
    }

    public void setAllowChangeUsername(boolean allowChangeUsername) {
        this.allowChangeUsername = allowChangeUsername;
    }

}
