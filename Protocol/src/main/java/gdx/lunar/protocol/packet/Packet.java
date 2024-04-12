package gdx.lunar.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Represents a basic packet outline
 */
public interface Packet {

    /**
     * @return the packet ID of this packet
     */
    int getId();

    /**
     * Allocate a buffer to this packet
     * Usually done before writing this packet to a channel.
     *
     * @param allocator the allocator to use
     */
    void alloc(ByteBufAllocator allocator);

    /**
     * Encode the contents of this packet
     */
    void encode();

    /**
     * Decode the contents of this packet
     */
    void decode();

    /**
     * @return the buffer of this packet
     */
    ByteBuf getBuffer();

    /**
     * Release (discard) this packet after contents have been read.
     */
    void release();

}
