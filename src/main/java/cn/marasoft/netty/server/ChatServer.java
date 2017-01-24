package cn.marasoft.netty.server;

import cn.marasoft.netty.server.handler.HttpHandler;
import cn.marasoft.netty.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by JiangQi on 2017/1/23.
 */
public class ChatServer {

    //监听端口
    private int port = 8080;

    public void start() {
        //主从模式，创建主线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //创建子线程，工作线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建Netty Socket Server
            ServerBootstrap bootstrap = new ServerBootstrap();
            //默认分配1024个工作线程
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //工作流，流水线，pipeline
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            //===========   对HTTP协议的支持   ============
                            //HTTP请求解码器
                            pipeline.addLast(new HttpServerCodec());
                            //编码，输出
//                            pipeline.addLast(new StringEncoder());
                            //主要将一个HTTP请求或者响应变成一个FullHttpRequest对象
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                            //用来处理文件流
                            pipeline.addLast(new ChunkedWriteHandler());
                            //处理HTTP请求的业务逻辑
                            pipeline.addLast(new HttpHandler());

                            //===========   对WebSocket协议的支持   ============
                            //加上这个Handler已经支持WebSocket请求
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
                            //处理WebSocket逻辑的Handler
                            pipeline.addLast(new WebSocketHandler());

                        }
                    });
            //采用同步的方式监听客户端
            //NIO采用同步非阻塞
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("服务已启动，监听端口:" + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}
