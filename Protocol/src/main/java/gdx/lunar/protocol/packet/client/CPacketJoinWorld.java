package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handler.ClientPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * A packet sent to request to join a world.
 */
public class CPacketJoinWorld extends Packet {

    public static final int PID = 885;

    /**
     * The name of the world to join
     */
    private String worldName;
    // username of the player trying to join
    private String username;

    public static void handle(ClientPacketHandler handler, ByteBuf buf) {
        handler.handleJoinWorld(new CPacketJoinWorld(buf));
    }

    public CPacketJoinWorld(String worldName, String username) {
        this.worldName = worldName;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    @Override
    public int getId() {
        return PID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(worldName);
        writeString(username);
    }

    @Override
    public void decode() {
        worldName = readString();
        username = readString();
    }
}
