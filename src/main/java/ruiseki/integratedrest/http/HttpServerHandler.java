package ruiseki.integratedrest.http;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import ruiseki.integratedrest.Uris;
import ruiseki.integratedrest.api.http.request.IRequestHandler;
import ruiseki.integratedrest.http.request.RequestHandlers;

/**
 * A handler for HTTP requests.
 *
 * @author rubensworks
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object message) throws Exception {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;

            // Netty 4.0.x check for 100 Continue
            if (HttpHeaders.is100ContinueExpected(request)) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE);
                context.write(response);
            }

            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("@context", Uris.IT + "context.jsonld");

            String[] path = request.getUri()
                .substring(1)
                .split("/"); // Note: Use request.getUri() in Netty 4.0 or request.uri() in Netty 4.1+
            List<String[]> paths = Lists.newArrayList();
            while (path.length > 1) {
                paths.add(path);
                String prefix = path[0];
                path = ArrayUtils.subarray(path, 1, path.length);
                path[0] = prefix + '/' + path[0];
            }
            paths.add(path);
            paths = Lists.reverse(paths); // Longest match first

            IRequestHandler requestHandler = null;
            for (String[] pathArray : paths) {
                requestHandler = RequestHandlers.REGISTRY.getHandler(pathArray[0]);
                if (requestHandler != null) {
                    path = pathArray;
                    break;
                }
            }

            HttpResponseStatus responseStatus;
            if (requestHandler == null) {
                responseStatus = HttpResponseStatus.NOT_FOUND;
            } else {
                responseStatus = requestHandler
                    .handle(ArrayUtils.subarray(path, 1, path.length), request, responseObject);
            }

            if (responseStatus == HttpResponseStatus.NOT_FOUND) {
                responseObject.addProperty("error", "Resource was not found.");
            }

            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
            String responseString = gson.toJson(responseObject) + "\n";
            if (!writeResponse(request, context, responseString, responseStatus)) {
                // If keep-alive is off, close the connection once the content is fully written.
                context.writeAndFlush(Unpooled.EMPTY_BUFFER)
                    .addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private boolean writeResponse(HttpRequest request, ChannelHandlerContext context, String responseString,
        HttpResponseStatus responseStatus) {
        // Netty 4.0.x check for keep-alive
        boolean keepAlive = HttpHeaders.isKeepAlive(request);

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            responseStatus,
            Unpooled.copiedBuffer(responseString, CharsetUtil.UTF_8));

        response.headers()
            .set(HttpHeaders.Names.CONTENT_TYPE, "application/ld+json");
        response.headers()
            .set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers()
                .set(
                    HttpHeaders.Names.CONTENT_LENGTH,
                    response.content()
                        .readableBytes());
            response.headers()
                .set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Write the response.
        context.write(response);

        return keepAlive;
    }
}
