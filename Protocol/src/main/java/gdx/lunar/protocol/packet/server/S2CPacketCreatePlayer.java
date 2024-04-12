package gdx.lunar.protocol.packet.server;

import gdx.lunar.protocol.handlers.ServerPacketHandler;
import gdx.lunar.protocol.packet.GamePacket;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

/**
 * Notify clients to construct a new player within their game instance
 */
public class S2CPacketCreatePlayer extends GamePacket {

    public static final int PACKET_ID = 1117;

    protected String username;
    protected int entityId;
    protected float x, y;

    public static void handle(ServerPacketHandler handler, ByteBuf buffer) {
        handler.handleCreatePlayer(new S2CPacketCreatePlayer(buffer));
    }

    /**
     * Initialize
     *
     * @param username username or {@code null}
     *                 If username is null, username string will be an empty string
     * @param entityId ID
     * @param x        starting X
     * @param y        starting Y
     */
    public S2CPacketCreatePlayer(String username, int entityId, float x, float y) {
        // empty string if no username!
        this.username = (username == null ? StringUtil.EMPTY_STRING : username);
        this.entityId = entityId;
        this.x = x;
        this.y = y;
    }

    private S2CPacketCreatePlayer(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return ID
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @return X
     */
    public float getX() {
        return x;
    }

    /**
     * @return Y
     */
    public float getY() {
        return y;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(username);
        buffer.writeInt(entityId);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    @Override
    public void decode() {
        username = readString();
        entityId = buffer.readInt();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }

}
