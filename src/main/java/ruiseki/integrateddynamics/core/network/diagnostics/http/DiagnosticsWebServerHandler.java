package ruiseki.integrateddynamics.core.network.diagnostics.http;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.core.network.diagnostics.NetworkDataClient;

/**
 * A handler for HTTP requests.
 *
 * @author rubensworks
 */
public class DiagnosticsWebServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object message) throws Exception {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;

            if (HttpHeaders.is100ContinueExpected((HttpMessage) request)) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE);
                context.write(response);
            }

            HttpResponseStatus responseStatus;
            String responseString;
            String contentType;

            switch (request.getUri()) {
                case "/":
                    responseStatus = HttpResponseStatus.OK;
                    InputStream is = IntegratedDynamics.class
                        .getResourceAsStream("/data/" + Reference.MOD_ID + "/web/diagnostics.html");
                    responseString = is != null ? IOUtils.toString(is, StandardCharsets.UTF_8) : "File Not Found";
                    contentType = "text/html; charset=UTF-8";
                    break;

                case "/data.json":
                    responseStatus = HttpResponseStatus.OK;
                    responseString = NetworkDataClient.getAsJsonString();
                    contentType = "application/json; charset=UTF-8";
                    break;

                case "/highlightEnable":
                    if (request.getMethod()
                        .equals(HttpMethod.POST)) {
                        JsonObject json = parseJsonBody(message);
                        if (json != null) {
                            NetworkDataClient.highlightEnable(json);
                        }
                    }
                    responseStatus = HttpResponseStatus.OK;
                    responseString = "Ok";
                    contentType = "text/plain; charset=UTF-8";
                    break;

                case "/highlightDisable":
                    if (request.getMethod()
                        .equals(HttpMethod.POST)) {
                        JsonObject json = parseJsonBody(message);
                        if (json != null) {
                            NetworkDataClient.highlightDisable(json);
                        }
                    }
                    responseStatus = HttpResponseStatus.OK;
                    responseString = "Ok";
                    contentType = "text/plain; charset=UTF-8";
                    break;

                case "/teleport":
                    if (request.getMethod()
                        .equals(HttpMethod.POST)) {
                        JsonObject json = parseJsonBody(message);
                        if (json != null) {
                            NetworkDataClient.teleport(json);
                        }
                    }
                    responseStatus = HttpResponseStatus.OK;
                    responseString = "Ok";
                    contentType = "text/plain; charset=UTF-8";
                    break;

                default:
                    responseStatus = HttpResponseStatus.NOT_FOUND;
                    responseString = "Not found";
                    contentType = "text/plain; charset=UTF-8";
                    break;
            }

            if (!writeResponse(request, context, responseString, contentType, responseStatus)) {
                // If keep-alive is off, close the connection once the content is fully written.
                context.writeAndFlush(Unpooled.EMPTY_BUFFER)
                    .addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    /**
     * Trích xuất và parse JsonObject từ Netty ByteBuf message.
     */
    private JsonObject parseJsonBody(Object message) {
        try {
            if (message instanceof FullHttpRequest) {
                FullHttpRequest fullReq = (FullHttpRequest) message;
                String body = fullReq.content()
                    .toString(CharsetUtil.UTF_8);
                return JSON_PARSER.parse(body)
                    .getAsJsonObject();
            } else if (message instanceof HttpContent) {
                HttpContent httpContent = (HttpContent) message;
                String body = httpContent.content()
                    .toString(CharsetUtil.UTF_8);
                return JSON_PARSER.parse(body)
                    .getAsJsonObject();
            }
        } catch (Exception e) {
            IntegratedDynamics.clog("Failed to parse JSON body from HTTP request: " + e.getMessage());
        }
        return null;
    }

    private boolean writeResponse(HttpRequest request, ChannelHandlerContext context, String responseString,
        String contentType, HttpResponseStatus responseStatus) {
        // Cast explicit to HttpMessage for Netty 4.0 compatibility
        boolean keepAlive = HttpHeaders.isKeepAlive((HttpMessage) request);

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            responseStatus,
            Unpooled.copiedBuffer(responseString, CharsetUtil.UTF_8));

        response.headers()
            .set(HttpHeaders.Names.CONTENT_TYPE, contentType);

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers()
                .set(
                    HttpHeaders.Names.CONTENT_LENGTH,
                    response.content()
                        .readableBytes());
            // Add keep alive header
            response.headers()
                .set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Write the response.
        context.write(response);

        return keepAlive;
    }
}
