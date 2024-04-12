package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

/**
 * Notify clients to remove a multiplayer player from their game instance
 */
public class S2CPacketRemovePlayer extends GamePacket {

    public static final int PACKET_ID = 1121;

    protected int entityId;
    protected String username;

    public static void handle(ServerPacketHandler handler, ByteBuf buffer) {
        handler.handleRemovePlayer(new S2CPacketRemovePlayer(buffer));
    }

    public S2CPacketRemovePlayer(int entityId) {
        this(entityId, null);
    }

    public S2CPacketRemovePlayer(int entityId, String username) {
        this.entityId = entityId;
        this.username = (username == null ? StringUtil.EMPTY_STRING : username);
    }

    private S2CPacketRemovePlayer(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the ID
     */
    public int getEntityId() {
        return entityId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        writeString(username);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        username = readString();
    }

}
