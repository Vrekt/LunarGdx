package gdx.lunar.protocol;

import io.netty.buffer.ByteBuf;

public interface PacketFactory<T> {

    T create(ByteBuf in);

}
