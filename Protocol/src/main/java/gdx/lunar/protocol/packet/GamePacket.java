package gdx.lunar.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.StandardCharsets;

/**
 * Default implementation of the {@link Packet} outline
 * Includes convince functions for writing and reading
 */
public abstract class GamePacket implements Packet {

    protected ByteBuf buffer;

    public GamePacket(ByteBuf buffer) {
        this.buffer = buffer;
        this.decode();
    }

    public GamePacket() {
    }

    /**
     * Write the ID of this packet to the buffer
     */
    protected void writeId() {
        buffer.writeInt(getId());
    }

    @Override
    public void alloc(ByteBufAllocator allocator) {
        this.buffer = allocator.ioBuffer();
    }

    @Override
    public ByteBuf getBuffer() {
        return buffer;
    }

    @Override
    public void decode() {

    }

    @Override
    public void release() {
        buffer.release();
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
