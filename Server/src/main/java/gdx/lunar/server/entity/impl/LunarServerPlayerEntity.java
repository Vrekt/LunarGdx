package gdx.lunar.server.entity.impl;

import gdx.lunar.server.entity.ServerPlayerEntity;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.network.connection.ServerAbstractConnection;
import gdx.lunar.protocol.packet.server.S2CPacketDisconnected;

/**
 * Base implementation of a player entity within the server
 */
public class LunarServerPlayerEntity extends LunarServerEntity implements ServerPlayerEntity {

    protected ServerAbstractConnection connection;
    protected boolean isLoaded;

    public LunarServerPlayerEntity(LunarServer server, ServerAbstractConnection connection) {
        super(server);
        this.connection = connection;
    }

    @Override
    public ServerAbstractConnection getConnection() {
        return connection;
    }

    @Override
    public void setIsLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void kick(String reason) {
        connection.sendImmediately(new S2CPacketDisconnected(reason));
        connection.disconnect();
    }

    @Override
    public void dispose() {
        super.dispose();
        connection = null;
        isLoaded = false;
    }
}
