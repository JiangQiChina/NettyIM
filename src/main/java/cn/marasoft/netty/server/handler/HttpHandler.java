package cn.marasoft.netty.server.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by JiangQi on 2017/1/23.
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    //class路径
    private URL basePath = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();
    //静态文件的存放目录
    private final String webroot = "webroot";

    private File getResource(String uri) throws Exception {
        String path = basePath.toURI() + webroot + "/" + uri;
        path = !path.contains("file:") ? path : path.substring(5);
        System.out.println(path);
        path.replace("//", "/");
        return new File(path);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        //一个URI对应一个资源，只解析静态资源
//        String uri = fullHttpRequest.getUri();  //fullHttpRequest.uri();
        String uri = fullHttpRequest.uri();

        System.out.println("获取URL:" + uri);
        String resource = uri.equals("/") ? "chat.html" : uri;
        RandomAccessFile file;
        try {
            file = new RandomAccessFile(getResource(resource), "r");
        } catch (Exception e) {
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
            return;
        }

//        HttpResponse response = new DefaultHttpResponse(fullHttpRequest.getProtocolVersion(), HttpResponseStatus.OK);
        HttpResponse response = new DefaultHttpResponse(fullHttpRequest.protocolVersion(), HttpResponseStatus.OK);
        String contextType = "text/html;";
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contextType + "charset=utf-8");
        boolean keepAlive = HttpHeaders.isKeepAlive(fullHttpRequest);
        if(keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        channelHandlerContext.write(response);

        channelHandlerContext.writeAndFlush(new ChunkedNioFile(file.getChannel()));

        ChannelFuture future = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        //如果不是长链接，然后文件也全部输出完毕，就关闭链接
        if(!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

        channelHandlerContext.close();
    }
}
