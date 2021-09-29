package gdx.lunar.protocol.channel;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Holds specific channel data.
 */
public enum ClientChannels {

    /**
     * NIO
     */
    NIO(NioSocketChannel.class, (KQueue.isAvailable() || Epoll.isAvailable()) ? null : new NioEventLoopGroup()),
    /**
     * K-QUEUE
     */
    KQUEUE(KQueueSocketChannel.class, KQueue.isAvailable() ? new KQueueEventLoopGroup() : null),
    /**
     * E-POLL
     */
    EPOLL(EpollSocketChannel.class, Epoll.isAvailable() ? new EpollEventLoopGroup() : null);

    /**
     * The channel
     */
    private final Class<? extends SocketChannel> channel;
    /**
     * The group
     */
    private final EventLoopGroup group;

    ClientChannels(Class<? extends SocketChannel> channelClass, EventLoopGroup group) {
        this.channel = channelClass;
        this.group = group;
    }

    /**
     * @return channel type
     */
    public Class<? extends SocketChannel> channel() {
        return channel;
    }

    /**
     * @return group
     */
    public EventLoopGroup group() {
        return group;
    }

    /**
     * Creates a new group
     *
     * @return the group
     */
    public EventLoopGroup newGroup() {
        return Epoll.isAvailable() ? new EpollEventLoopGroup() :
                KQueue.isAvailable() ? new KQueueEventLoopGroup() : new NioEventLoopGroup();
    }

    /**
     * Get the default channels
     *
     * @return the channels
     */
    public static ClientChannels get() {
        return Epoll.isAvailable() ? ClientChannels.EPOLL :
                KQueue.isAvailable() ? ClientChannels.KQUEUE : ClientChannels.NIO;
    }

}