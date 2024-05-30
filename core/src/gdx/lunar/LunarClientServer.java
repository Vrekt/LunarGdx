package gdx.lunar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.network.AbstractConnectionHandler;
import gdx.lunar.network.adapter.FirstInboundConnectionHandler;
import gdx.lunar.network.codec.ServerProtocolPacketDecoder;
import gdx.lunar.network.provider.ConnectionProvider;
import gdx.lunar.network.PlayerConnectionHandler;
import gdx.lunar.protocol.GdxProtocol;
import gdx.lunar.protocol.channel.ClientChannels;
import gdx.lunar.protocol.codec.ProtocolPacketEncoder;
import gdx.lunar.protocol.packet.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.util.concurrent.CompletableFuture;

/**
 * LunarProtocolSettings accessor for game clients.
 */
public final class LunarClientServer implements Disposable {

    /**
     * The netty bootstrap
     */
    private final Bootstrap bootstrap;
    private final EventLoopGroup group;

    private final String ip;
    private final int port;

    private final SslContext ssl;

    private final GdxProtocol protocol;

    private ChannelInboundHandlerAdapter adapter;
    private LengthFieldBasedFrameDecoder decoder;
    private MessageToByteEncoder<Packet> encoder;
    private ConnectionProvider provider;

    private AbstractConnectionHandler connection;

    /**
     * Initialize a new instance with a pre-built bootstrap.
     *
     * @param protocol  the protocol to use
     * @param bootstrap boostrap.
     * @param ip        the server IP address
     * @param port      the server port
     */
    public LunarClientServer(GdxProtocol protocol, Bootstrap bootstrap, String ip, int port) {
        this.protocol = protocol;
        this.bootstrap = bootstrap;
        this.group = bootstrap.config().group();
        this.ip = ip;
        this.port = port;

        try {
            ssl = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Initialize the bootstrap
     *
     * @param protocol the protocol to use
     * @param ip       the server IP address
     * @param port     the server port
     */
    public LunarClientServer(GdxProtocol protocol, String ip, int port) {
        this.protocol = protocol;
        this.ip = ip;
        this.port = port;

        final ClientChannels channelConfig = ClientChannels.get();
        group = channelConfig.group();
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(channelConfig.channel())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        handleSocketConnection(channel);
                    }
                });

        try {
            ssl = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Set the main handler netty will use for all game traffic.
     * Not required.
     *
     * @param adapter the adapter
     */
    public void setInboundNetworkHandler(ChannelInboundHandlerAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Set the default protocol decoder to use within the network.
     * Not required.
     *
     * @param decoder the decoder
     */
    public void setProtocolDecoder(LengthFieldBasedFrameDecoder decoder) {
        this.decoder = decoder;
    }

    /**
     * Set the connection provider.
     * This provides incoming multiplayer connections a way to retrieve custom implementations of {@link AbstractConnectionHandler}
     *
     * @param provider the provider
     */
    public void setConnectionProvider(ConnectionProvider provider) {
        this.provider = provider;
    }

    /**
     * Use a custom protocol encoder.
     * 10-12-2021: Custom encoders
     *
     * @param encoder encoder
     */
    public void setProtocolEncoder(MessageToByteEncoder<Packet> encoder) {
        this.encoder = encoder;
    }

    /**
     * Handle a new socket connection
     *
     * @param channel channel
     */
    private void handleSocketConnection(SocketChannel channel) {
        connection = this.provider == null
                ? new PlayerConnectionHandler(channel, protocol)
                : this.provider.createConnection(channel);
        if (this.adapter == null) adapter = new FirstInboundConnectionHandler(connection);
        if (this.decoder == null) decoder = new ServerProtocolPacketDecoder(connection, protocol);
        if (this.encoder == null) encoder = new ProtocolPacketEncoder();

        channel.pipeline().addLast(ssl.newHandler(channel.alloc(), ip, port));
        channel.pipeline().addLast(decoder);
        channel.pipeline().addLast(encoder);
        channel.pipeline().addLast(adapter);
    }

    public AbstractConnectionHandler getConnection() {
        return connection;
    }

    public GdxProtocol getProtocol() {
        return protocol;
    }

    /**
     * Connect to the previous provided IP and PORT.
     *
     * @return the result.
     */
    public CompletableFuture<Void> connectAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                bootstrap.connect(ip, port).sync();
            } catch (Exception any) {
                Gdx.app.log("LunarClientServer", "Could not connect async", any);
            }
        });
    }

    /**
     * Connect.
     *
     * @throws Exception any
     */
    public void connect() throws Exception {
        bootstrap.connect(ip, port).sync();
    }

    /**
     * Connect, but silence the exception.
     */
    public boolean connectNoExceptions() {
        try {
            bootstrap.connect(ip, port).sync();
            return true;
        } catch (Exception any) {
            Gdx.app.log("LunarClientServer", "Could not connect", any);
        }
        return false;
    }

    @Override
    public void dispose() {
        group.shutdownGracefully();
        connection.dispose();
        protocol.dispose();
    }

}
