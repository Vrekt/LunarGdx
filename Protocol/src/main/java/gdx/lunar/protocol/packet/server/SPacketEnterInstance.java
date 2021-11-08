package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * A packet for telling a player to join an instance
 */
public class SPacketEnterInstance extends Packet {

    public static final int PID = 29;

    private boolean isAllowed;
    private String notAllowedReason;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleEnterInstance(new SPacketEnterInstance(buf));
    }

    public SPacketEnterInstance(ByteBufAllocator allocator, boolean isAllowed, String notAllowedReason) {
        super(allocator);
        this.isAllowed = isAllowed;
        this.notAllowedReason = notAllowedReason;
    }

    private SPacketEnterInstance(ByteBuf buffer) {
        super(buffer);
    }

    public boolean isAllowed() {
        return isAllowed;
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
        buffer.writeBoolean(isAllowed);
        if (notAllowedReason != null) {
            writeString(notAllowedReason);
        }
    }

    @Override
    public void decode() {
        isAllowed = buffer.readBoolean();
        if (!isAllowed) {
            notAllowedReason = readString();
        }
    }

}
