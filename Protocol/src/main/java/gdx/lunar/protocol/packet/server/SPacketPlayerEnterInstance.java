package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

public class SPacketPlayerEnterInstance extends Packet {

    public static final int PID = 9916;

    private int entityId;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handlePlayerEnterInstance(new SPacketPlayerEnterInstance(buf));
    }

    /**
     * Initialize
     *
     * @param entityId ID
     */
    public SPacketPlayerEnterInstance(int entityId) {
        this.entityId = entityId;
    }

    private SPacketPlayerEnterInstance(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return ID
     */
    public int getEntityId() {
        return entityId;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
    }

}
