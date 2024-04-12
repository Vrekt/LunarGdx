package gdx.lunar.server.game;

import gdx.lunar.protocol.GdxProtocol;
import gdx.lunar.server.configuration.DefaultServerConfiguration;
import gdx.lunar.server.entity.ServerPlayerEntity;
import gdx.lunar.server.network.connection.ServerAbstractConnection;

/**
 * Represents a default game server implementation.
 */
public class GameServer extends LunarServer {

    private final DefaultServerConfiguration configuration = new DefaultServerConfiguration();

    public GameServer(GdxProtocol protocol, String gameVersion) {
        super(protocol);
        this.gameVersion = gameVersion;
    }

    @Override
    public DefaultServerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public boolean authenticatePlayer(String version, int protocolVersion) {
        return !isFull() && version.equalsIgnoreCase(gameVersion) && protocolVersion == protocol.getProtocolVersion();
    }

    @Override
    public void handlePlayerDisconnect(ServerPlayerEntity player) {
        super.handlePlayerDisconnect(player);

        if (player.getWorld() != null) player.getWorld().removeEntityInWorld(player);
    }

    @Override
    public boolean addPlayerToServer(ServerAbstractConnection connection) {
        return this.connections.add(connection);
    }

    @Override
    public boolean isUsernameValidInWorld(String world, String username) {
        if (username == null || username.isEmpty()) return false;
        return worldManager.worldExists(world) && !worldManager.getWorld(world).doesUsernameExist(username);
    }
}
