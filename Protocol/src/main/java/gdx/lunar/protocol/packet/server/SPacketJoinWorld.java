package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Tells the player they should join a world.
 */
public class SPacketJoinWorld extends Packet {

    public static final int PID = 997;

    // name of the world player should join
    private String worldName;
    // players new entity ID.
    private int entityId;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleJoinWorld(new SPacketJoinWorld(buf));
    }

    public SPacketJoinWorld(String worldName, int entityId) {
        this.worldName = worldName;
        this.entityId = entityId;
    }

    public SPacketJoinWorld(ByteBuf buffer) {
        super(buffer);
    }

    public String getWorldName() {
        return worldName;
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    public void encode() {
        writeId();
        writeString(worldName);
        buffer.writeInt(entityId);
    }

    @Override
    public void decode() {
        worldName = readString();
        entityId = buffer.readInt();
    }

    @Override
    public int getId() {
        return PID;
    }
}
