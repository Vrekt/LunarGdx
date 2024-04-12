package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;

/**
 * A request to join a world yielded a world that was not found, or invalid.
 */
public class S2CPacketWorldInvalid extends GamePacket {

    public static final int PACKET_ID = 1115;

    protected String worldName, reason;

    public static void handle(ServerPacketHandler handler, ByteBuf buffer) {
        handler.handleWorldInvalid(new S2CPacketWorldInvalid(buffer));
    }

    public S2CPacketWorldInvalid(String worldName, String reason) {
        this.worldName = worldName;
        this.reason = reason;
    }

    public S2CPacketWorldInvalid(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the name of the world that was requested to join
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * @return the reason the world was invalid.
     */
    public String getReason() {
        return reason;
    }

    @Override
    public int getId() {
        return PACKET_ID;
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
