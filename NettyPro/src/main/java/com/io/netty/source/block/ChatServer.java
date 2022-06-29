package com.io.netty.source.block;

/**
 * @author Stone
 * @date 2022/6/29
 */


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;


public class ChatServer {
    public static void main(String[] args) {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(8, new DefaultThreadFactory("work"));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // worker group 线程执行的
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
//                            p.addLast(new WorkerIntrospectHandler());
                            p.addLast(new BizHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(9876).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}