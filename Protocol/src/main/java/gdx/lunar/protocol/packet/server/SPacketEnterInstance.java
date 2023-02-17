package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * A packet for telling a player to join an instance
 */
public class SPacketEnterInstance extends Packet {

    public static final int PID = 9915;

    private int instanceId;
    private boolean isAllowed, isFull;
    private String notAllowedReason;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleEnterInstance(new SPacketEnterInstance(buf));
    }

    public SPacketEnterInstance(int instanceId, boolean isAllowed, boolean isFull, String notAllowedReason) {
        this.instanceId = instanceId;
        this.isAllowed = isAllowed;
        this.isFull = isFull;
        this.notAllowedReason = notAllowedReason;
    }

    private SPacketEnterInstance(ByteBuf buffer) {
        super(buffer);
    }

    public int getInstanceId() {
        return instanceId;
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public boolean isFull() {
        return isFull;
    }

    public String getNotAllowedReason() {
        return notAllowedReason;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(instanceId);
        buffer.writeBoolean(isAllowed);
        buffer.writeBoolean(isFull);
        if (notAllowedReason != null) {
            writeString(notAllowedReason);
        }
    }

    @Override
    public void decode() {
        instanceId = buffer.readInt();
        isAllowed = buffer.readBoolean();
        isFull = buffer.readBoolean();
        if (!isAllowed) {
            notAllowedReason = readString();
        }
    }

}
