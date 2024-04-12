package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * The spawn entity request was denied.
 */
public class SPacketSpawnEntityDenied extends Packet {

    public static final int PID = 9910;

    protected int temporaryEntityId;
    protected String reason;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleSpawnEntityDenied(new SPacketSpawnEntityDenied(buf));
    }

    public SPacketSpawnEntityDenied(int temporaryEntityId, String reason) {
        this.temporaryEntityId = temporaryEntityId;
        this.reason = reason;
    }

    public SPacketSpawnEntityDenied(ByteBuf buffer) {
        super(buffer);
    }

    public int getTemporaryEntityId() {
        return temporaryEntityId;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(temporaryEntityId);
        writeString(reason);
    }

    @Override
    public void decode() {
        this.temporaryEntityId = buffer.readInt();
        this.reason = readString();
    }
}
