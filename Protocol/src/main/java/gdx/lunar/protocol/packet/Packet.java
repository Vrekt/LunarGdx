package gdx.lunar.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.StandardCharsets;

/**
 * Represents a base packet.
 */
public abstract class Packet {

    /**
     * The buffer content of this packet.
     */
    protected ByteBuf buffer;

    public Packet(ByteBufAllocator allocator) {
        this.buffer = allocator.ioBuffer();
    }

    protected Packet(ByteBuf buffer) {
        this.buffer = buffer;
        decode();
    }

    protected Packet() {

    }

    /**
     * @return the packet ID.
     */
    public abstract int getId();

    /**
     * Encode this packet
     */
    public void encode() {

    }

    /**
     * Decode this packet.
     */
    public void decode() {

    }

    /**
     * Encode this packet now
     *
     * @return the byte buf contents
     */
    public ByteBuf encodeNow() {
        this.encode();
        return buffer;
    }

    /**
     * @return contents of this packet.
     */
    public ByteBuf getBuffer() {
        return buffer;
    }

    public void release() {
        buffer.release();
    }

    /**
     * Write PID
     */
    protected void writeId() {
        buffer.writeByte(getId());
    }

    /**
     * Read bytes
     *
     * @param length the length
     * @return the bytes
     */
    protected byte[] readBytes(int length) {
        final byte[] contents = new byte[length];
        buffer.readBytes(contents, 0, length);
        return contents;
    }

    /**
     * Read a string
     *
     * @return the string
     */
    protected String readString() {
        if (buffer.isReadable(4)) {
            final int length = buffer.readInt();
            if (buffer.isReadable(length)) {
                final byte[] contents = readBytes(length);
                return new String(contents, StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    /**
     * Write a string.
     *
     * @param value the value
     */
    protected void writeString(String value) {
        if (value == null) return;

        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

}
