package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Sent in response to {@link gdx.lunar.protocol.packet.client.CPacketAuthentication}
 */
public class SPacketAuthentication extends Packet {

    public static final int PID = 3;

    private boolean isAllowed;
    private String notAllowedReason;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleAuthentication(new SPacketAuthentication(buf));
    }

    public SPacketAuthentication(ByteBufAllocator allocator, boolean isAllowed, String notAllowedReason) {
        super(allocator);
        this.isAllowed = isAllowed;
        this.notAllowedReason = notAllowedReason;
    }

    private SPacketAuthentication(ByteBuf buffer) {
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
        if (notAllowedReason != null) writeString(notAllowedReason);
    }

    @Override
    public void decode() {
        isAllowed = buffer.readBoolean();
        if (!isAllowed) notAllowedReason = readString();
    }

}
