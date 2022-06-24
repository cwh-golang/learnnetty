package com.io.netty.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyServerHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("从客户端" + ctx.channel().remoteAddress() + " 读取到long " + msg);
        //给客户端发送一个long
        ctx.writeAndFlush(98765L);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 此处才是真正实现客户端意外关闭后，发生的异常信息
        System.out.println("exceptionCaught 被执行，且异常信息：\n" + cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive 被执行，客户端已经离开");
    }
}
