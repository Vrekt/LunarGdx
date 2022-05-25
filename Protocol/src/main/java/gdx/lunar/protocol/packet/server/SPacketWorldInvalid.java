package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Lets the client know the world doesn't exist or an error occured.
 */
public class SPacketWorldInvalid extends Packet {

    public static final int PID = 9917;

    private String worldName, reason;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handleWorldInvalid(new SPacketWorldInvalid(buf));
    }

    public SPacketWorldInvalid(String worldName, String reason) {
        this.worldName = worldName;
        this.reason = reason;
    }

    public SPacketWorldInvalid(ByteBuf buffer) {
        super(buffer);
    }

    public String getWorldName() {
        return worldName;
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
        writeString(worldName == null ? "" : worldName);
        writeString(reason == null ? "" : reason);
    }

    @Override
    public void decode() {
        worldName = readString();
        reason = readString();
    }

}
