package com.io.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 1. 搞明白入栈出站的handler调用链
 *
 * SimpleChannelInboundHandler
 * ChannelInboundHandlerAdapter
 * ChannelInboundHandler
 *
 * 2. 搞明白编码解码器和普通handler调用链信息
 *
 *
 * 入队出队的入口函数：AbstractChannelHandlerContext：read、write方法。
 *
 *
 * 解码器：Decoder类也是InboundHandler类型，在调用
 * channelRead方法中，调用了 --->
 * 	callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) out实参是在该方法中，初始化了一个空的集合数据
 * 	在该方法中，使用一个while的死循环，判定buffer中是否还有可读取项，如果有，则继续读取，而根据我们的读取规则，我们只会读取到适合我们的数据。
 * 	如果我们按照我们自定义的规则读取到了合适的数据，则加入out集合中，然后上层父类方法会判定如果out中的size>0，则进行下一个handler的处理。
 * 	直到我们的in中没有可读取的内容，则进行最后一次下一个handler的数据处理。
 * 		但是要注意，在下一个handler进行数据处理会做一个判断if (acceptInboundMessage(msg))，如果不满足数据格式，则不进行处理。
 *
 * TCP是面向连接的，面向流的，提供高可靠性服务。收发两端（客户端和服务器端）都要有一一成对的socket，
 * 因此，发送端为了将多个发给接收端的包，更有效的发给对方，使用了优化方法（Nagle算法），将多次间隔较小且数据量小的数据，合并成一个大的数据块，然后进行封包。
 * 这样做虽然提高了效率，但是接收端就难于分辨出完整的数据包了，因为面向流的通信是无消息保护边界的
 *
 * 由于TCP无消息保护边界, 需要在接收端处理消息边界问题，也就是我们所说的粘包、拆包问题, 看一张图
 */
public class MyServer {
    public static void main(String[] args) throws Exception{


        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //8个NioEventLoop
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    //加入一个netty 提供 IdleStateHandler
                    /*
                    说明
                    1. IdleStateHandler 是netty 提供的处理空闲状态的处理器
                    2. long readerIdleTime : 表示多长时间没有读, 就会发送一个心跳检测包检测是否连接
                    3. long writerIdleTime : 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接
                    4. long allIdleTime : 表示多长时间没有读写, 就会发送一个心跳检测包检测是否连接

                    5. 文档说明
                    triggers an {@link IdleStateEvent} when a {@link Channel} has not performed
 * read, write, or both operation for a while.
 *                  6. 当 IdleStateEvent 触发后 , 就会传递给管道 的下一个handler去处理
 *                  通过调用(触发)下一个handler 的 userEventTiggered , 在该方法中去处理 IdleStateEvent(读空闲，写空闲，读写空闲)
                     */
                    pipeline.addLast(new IdleStateHandler(7000,7000,10, TimeUnit.SECONDS));
                    //加入一个对空闲检测进一步处理的handler(自定义)
                    pipeline.addLast(new MyServerHandler());
                }
            });

            //启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
