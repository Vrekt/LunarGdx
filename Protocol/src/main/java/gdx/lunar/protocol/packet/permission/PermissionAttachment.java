package gdx.lunar.protocol.packet.permission;

import gdx.lunar.protocol.handler.ServerPacketHandler;
import gdx.lunar.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Represents a permission response to a {@link Permissible}
 */
public class PermissionAttachment extends Packet {

    // packet ID who initially requested permission
    private int packetIdFrom;

    // other related data
    private int entityId;

    private boolean hasPermission;
    private String permissionReason;

    public static void handle(ServerPacketHandler handler, ByteBuf buf) {
        handler.handlePermissionAttachment(new PermissionAttachment(buf));
    }

    public PermissionAttachment(int packetIdFrom, boolean hasPermission, String permissionReason) {
        this.packetIdFrom = packetIdFrom;
        this.hasPermission = hasPermission;
        this.permissionReason = permissionReason;
    }

    public PermissionAttachment(ByteBuf buffer) {
        super(buffer);
    }

    public int getPacketIdFrom() {
        return packetIdFrom;
    }

    public int getEntityId() {
        return entityId;
    }

    public boolean hasPermission() {
        return hasPermission;
    }

    public String getPermissionReason() {
        return permissionReason;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(packetIdFrom);
        buffer.writeBoolean(hasPermission);
        if (!hasPermission) writeString(permissionReason);

        // extra fields
        buffer.writeInt(entityId);
    }

    @Override
    public void decode() {
        packetIdFrom = buffer.readInt();
        hasPermission = buffer.readBoolean();
        if (!hasPermission) permissionReason = readString();
    }

    @Override
    public int getId() {
        return 0;
    }
}
