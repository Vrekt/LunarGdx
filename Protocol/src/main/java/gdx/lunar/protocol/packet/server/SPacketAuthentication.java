package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

public class SPacketAuthentication extends Packet {

    public static final int PID = 992;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleAuthentication(new SPacketAuthentication(buf));
    }

    protected boolean isAllowed;

    public SPacketAuthentication(boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    public SPacketAuthentication(ByteBuf buffer) {
        super(buffer);
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeBoolean(isAllowed);
    }

    @Override
    public void decode() {
        isAllowed = buffer.readBoolean();
    }

    @Override
    public int getId() {
        return PID;
    }
}
