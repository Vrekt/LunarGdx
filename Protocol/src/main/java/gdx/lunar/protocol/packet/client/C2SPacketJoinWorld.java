package gdx.lunar.protocol.packet.client;

import gdx.lunar.protocol.handlers.ClientPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

/**
 * Request to join a world.
 */
public class C2SPacketJoinWorld extends GamePacket {

    public static final int PACKET_ID = 2224;

    //The name of the world to join
    protected String worldName;
    // username of the player trying to join
    protected String username;
    // client time or current client tick
    protected long clientTime;
    // indicates to send all current players within separate packets
    // instead of one large chunk.
    // TODO
    protected boolean batchPlayers;

    public static void handle(ClientPacketHandler handler, ByteBuf buffer) {
        handler.handleJoinWorld(new C2SPacketJoinWorld(buffer));
    }

    public C2SPacketJoinWorld(String worldName, String username, long clientTime) {
        this.worldName = worldName;
        this.username = username == null ? StringUtil.EMPTY_STRING : username;
        this.clientTime = clientTime;
    }

    public C2SPacketJoinWorld(ByteBuf buffer) {
        super(buffer);
    }

    public String getWorldName() {
        return worldName;
    }

    public String getUsername() {
        return username;
    }

    public long getClientTime() {
        return clientTime;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(worldName);
        writeString(username);
        buffer.writeLong(clientTime);
    }

    @Override
    public void decode() {
        worldName = readString();
        username = readString();
        clientTime = buffer.readLong();
    }
}
