package gdx.lunar.protocol.channel;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Holds data for server channels
 */
public enum ServerChannels {

    /**
     * NIO
     */
    NIO(NioServerSocketChannel.class, (KQueue.isAvailable() || Epoll.isAvailable()) ? null : new NioEventLoopGroup(4)),
    /**
     * K-QUEUE
     */
    KQUEUE(KQueueServerSocketChannel.class, KQueue.isAvailable() ? new KQueueEventLoopGroup(4) : null),
    /**
     * E-POLL
     */
    EPOLL(EpollServerSocketChannel.class, Epoll.isAvailable() ? new EpollEventLoopGroup(4) : null);

    /**
     * The channel
     */
    private final Class<? extends ServerChannel> channel;
    /**
     * The group
     */
    private final EventLoopGroup group;

    ServerChannels(Class<? extends ServerChannel> channelClass, EventLoopGroup group) {
        this.channel = channelClass;
        this.group = group;
    }

    /**
     * @return channel type
     */
    public Class<? extends ServerChannel> channel() {
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
    public static ServerChannels get() {
        return Epoll.isAvailable() ? ServerChannels.EPOLL :
                KQueue.isAvailable() ? ServerChannels.KQUEUE : ServerChannels.NIO;
    }

}
