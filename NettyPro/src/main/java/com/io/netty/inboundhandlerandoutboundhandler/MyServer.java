package com.io.netty.inboundhandlerandoutboundhandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/*
 * inbound流程：
 * 1.SimpleChannelInboundHandler、ChannelInboundHandlerAdapter、ChannelInboundHandler，关系是继承关系。
 * SimpleChannelInboundHandler中有唯一一个read0的方法，是在ChannelInboundHandler执行read的时候会调用该方法
 *
 *
 *
 * @Author 86187
 * @Param
 * @return
 **/
public class MyServer {
    public static void main(String[] args) throws Exception{

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new MyServerInitializer()); //自定义一个初始化类

            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
//            System.out.println("客户端强制关闭 " + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
