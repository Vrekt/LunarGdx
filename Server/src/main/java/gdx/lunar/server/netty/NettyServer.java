package gdx.lunar.server.netty;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.protocol.channel.ServerChannels;
import gdx.lunar.protocol.codec.ProtocolPacketEncoder;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.netty.codec.ClientProtocolPacketDecoder;
import gdx.lunar.server.network.connection.ServerAbstractConnection;
import gdx.lunar.server.network.connection.ServerPlayerConnection;
import gdx.lunar.server.network.connection.provider.ConnectionProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * The netty server.
 */
public class NettyServer {

    private final ServerBootstrap bootstrap;
    private final EventLoopGroup parent, child;
    private ProtocolPacketEncoder encoder;

    private final String ip;
    private final int port;

    private ConnectionProvider connectionProvider;
    private Supplier<LengthFieldBasedFrameDecoder> decoderSupplier;
    private final SslContext sslContext;

    private final LunarProtocol protocol;
    private LunarServer server;

    private final LinkedList<ByteToMessageDecoder> decoders = new LinkedList<>();

    /**
     * Initialize the bootstrap and server.
     *
     * @param ip        the server IP address
     * @param port      the server port
     * @param protocol  protocol
     * @param bootstrap bootstrap
     * @param context   ssl
     */
    public NettyServer(String ip, int port, LunarProtocol protocol, ServerBootstrap bootstrap, SslContext context, LunarServer server) {
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
        this.bootstrap = bootstrap;
        this.sslContext = context;
        this.server = server;

        this.parent = bootstrap.config().group();
        this.child = bootstrap.config().childGroup();
        this.encoder = new ProtocolPacketEncoder();
    }

    /**
     * Initialize the bootstrap and server.
     *
     * @param ip   the server IP address
     * @param port the server port
     */
    public NettyServer(String ip, int port, LunarProtocol protocol, LunarServer server) {
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
        this.server = server;

        try {
            final SelfSignedCertificate ssc = new SelfSignedCertificate();
            this.sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .build();
        } catch (Exception any) {
            throw new RuntimeException(any);
        }

        final ServerChannels channels = ServerChannels.get();
        bootstrap = new ServerBootstrap();

        parent = channels.group();
        child = channels.newGroup();

        bootstrap.group(parent, child)
                .channel(channels.channel())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        handleSocketConnection(channel);
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        encoder = new ProtocolPacketEncoder();
    }

    public void setDecoderSupplier(Supplier<LengthFieldBasedFrameDecoder> decoderSupplier) {
        this.decoderSupplier = decoderSupplier;
    }

    public void setConnectionProvider(ConnectionProvider provider) {
        this.connectionProvider = provider;
    }

    public void setEncoder(ProtocolPacketEncoder encoder) {
        this.encoder = encoder;
    }

    public void addDecoder(ByteToMessageDecoder decoder) {
        this.decoders.add(decoder);
    }

    /**
     * Handle a new socket channel
     *
     * @param channel the channel
     */
    private void handleSocketConnection(SocketChannel channel) {
        final ServerAbstractConnection connection = connectionProvider == null ? new ServerPlayerConnection(channel, server) : connectionProvider.createConnection(channel);
        final LengthFieldBasedFrameDecoder decoder = this.decoderSupplier == null ? new ClientProtocolPacketDecoder(connection, protocol)
                : decoderSupplier.get();

        channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));
        channel.pipeline().addLast(decoder);
        for (ByteToMessageDecoder byteToMessageDecoder : this.decoders) {
            channel.pipeline().addLast(byteToMessageDecoder);
        }
        channel.pipeline().addLast(encoder);
        channel.pipeline().addLast(connection);
    }

    /**
     * Bind
     *
     * @return the result.
     */
    public CompletableFuture<ChannelFuture> bind() {
        final CompletableFuture<ChannelFuture> result = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                final ChannelFuture future = bootstrap.bind(ip, port).sync();
                result.complete(future);
            } catch (Exception any) {
                result.completeExceptionally(any);
            }
        });

        return result;
    }

    /**
     * Shutdown the server
     */
    public void shutdown() {
        child.shutdownGracefully();
        parent.shutdownGracefully();
    }

}
