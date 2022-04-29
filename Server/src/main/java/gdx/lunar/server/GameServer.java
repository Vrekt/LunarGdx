package gdx.lunar.server;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.configuration.LunarServerConfiguration;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.network.ServerAbstractConnection;

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
    public void handlePlayerDisconnect(LunarServerPlayerEntity player) {
        super.handlePlayerDisconnect(player);

        if (player.getWorld() != null) player.getWorld().removeEntityInWorld(player);
    }

    @Override
    public boolean handleJoinProcess(ServerAbstractConnection connection) {
        this.connections.add(connection);
        return true;
    }

    @Override
    public boolean isUsernameValidInWorld(String world, String username) {
        if (username == null) return false;
        return worldManager.worldExists(world) && !worldManager.getWorld(world).doesUsernameExist(username);
    }
}
