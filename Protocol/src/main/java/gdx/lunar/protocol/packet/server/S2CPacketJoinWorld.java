package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * Sent from the server to initialize joining a new world or sub-server.
 */
public class S2CPacketJoinWorld extends GamePacket {

    public static final int PACKET_ID = 1114;

    // name of the world the client was
    protected String worldName;
    // assigned entity ID of the new client joining
    protected int entityId;
    // current server time or server tick
    protected long serverTime;

    public static void handle(ServerPacketHandler handler, ByteBuf buffer) {
        handler.handleJoinWorld(new S2CPacketJoinWorld(buffer));
    }

    public S2CPacketJoinWorld(String worldName, int entityId, long serverTime) {
        this.worldName = worldName;
        this.entityId = entityId;
        this.serverTime = serverTime;
    }

    public S2CPacketJoinWorld(ByteBuf buffer) {
        super(buffer);
    }

    public String getWorldName() {
        return worldName;
    }

    public int getEntityId() {
        return entityId;
    }

    public long getServerTime() {
        return serverTime;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(worldName);
        buffer.writeInt(entityId);
        buffer.writeLong(serverTime);
    }

    @Override
    public void decode() {
        worldName = readString();
        entityId = buffer.readInt();
        serverTime = buffer.readLong();
    }
}
