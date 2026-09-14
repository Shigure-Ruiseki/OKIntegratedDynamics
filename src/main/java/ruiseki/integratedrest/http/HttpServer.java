package ruiseki.integratedrest.http;

import org.apache.logging.log4j.Level;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ruiseki.integratedrest.GeneralConfig;
import ruiseki.integratedrest.IntegratedRest;

/**
 * An HTTP server that holds a single channel.
 *
 * @author rubensworks
 */
public class HttpServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void initialize() {
        IntegratedRest.clog(Level.INFO, "Starting Integrated REST server...");
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new HttpServerInitializer());

        try {
            this.channel = b.bind(GeneralConfig.apiPort)
                .sync()
                .channel();
            IntegratedRest
                .clog(Level.INFO, "Started Integrated REST server on http://localhost:" + GeneralConfig.apiPort + "/");
        } catch (Exception e) {
            IntegratedRest.clog(
                Level.ERROR,
                "Failed to start Integrated REST server on port " + GeneralConfig.apiPort + ": " + e.getMessage());
            deinitialize();
        }
    }

    public void deinitialize() {
        IntegratedRest.clog(Level.INFO, "Stopping Integrated REST server...");
        if (this.bossGroup != null && this.workerGroup != null) {
            try {
                this.bossGroup.shutdownGracefully()
                    .sync();
                this.workerGroup.shutdownGracefully()
                    .sync();
            } catch (InterruptedException e) {
                IntegratedRest.clog(Level.WARN, "Interrupted while shutting down HTTP server: " + e.getMessage());
            }
        }

        IntegratedRest.clog(Level.INFO, "Stopped Integrated REST server");
    }

}
