package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * A packet sent to request to join a world.
 */
public class CPacketJoinWorld extends Packet {

    public static final int PID = 11;

    /**
     * The name of the world to join
     */
    private String worldName;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleJoinWorld(new CPacketJoinWorld(buf));
    }

    public CPacketJoinWorld(String worldName) {
        this.worldName = worldName;
    }

    private CPacketJoinWorld(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the world name requested to join
     */
    public String getWorldName() {
        return worldName;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(worldName);
    }

    @Override
    public void decode() {
        worldName = readString();
    }
}
