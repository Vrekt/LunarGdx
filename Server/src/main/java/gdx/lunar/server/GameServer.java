package gdx.lunar.server;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.configuration.LunarServerConfiguration;
import gdx.lunar.server.game.entity.player.LunarPlayer;
import gdx.lunar.server.network.AbstractConnection;

/**
 * Represents a default game server implementation.
 */
public class GameServer extends LunarServer {

    private final LunarServerConfiguration configuration = new LunarServerConfiguration();

    public GameServer(LunarProtocol protocol, String gameVersion) {
        super(protocol);
        this.gameVersion = gameVersion;
    }

    public GameServer(int threads, LunarProtocol protocol, String gameVersion) {
        super(threads, protocol);
        this.gameVersion = gameVersion;
    }

    @Override
    public LunarServerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public boolean handlePlayerAuthentication(String version, int protocolVersion) {
        return !isFull() && version.equalsIgnoreCase(gameVersion) && protocolVersion == protocol.getProtocolVersion();
    }

    @Override
    public void handlePlayerDisconnect(LunarPlayer player) {
        super.handlePlayerDisconnect(player);

        if (player.getWorld() != null)
            player.getWorld().removePlayerInWorld(player);
    }

    @Override
    public boolean handleJoinProcess(AbstractConnection connection) {
        this.connections.add(connection);
        return true;
    }
}
