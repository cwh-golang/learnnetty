package com.io.netty.source.block;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Stone
 * @date 2022/6/28
 */
public class BizHandler extends ChannelInboundHandlerAdapter {
    public BizHandler() {
        super();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("BizHandler channelRegistered 的线程是=" + Thread.currentThread().getName());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("BizHandler channelUnregistered 的线程是=" + Thread.currentThread().getName());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("BizHandler channelActive 的线程是=" + Thread.currentThread().getName());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("BizHandler channelInactive 的线程是=" + Thread.currentThread().getName());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        System.out.println("BizHandler channelRead 的线程是=" + Thread.currentThread().getName() + msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.println("BizHandler channelReadComplete 的线程是=" + Thread.currentThread().getName());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        System.out.println("BizHandler userEventTriggered 的线程是=" + Thread.currentThread().getName());
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        System.out.println("BizHandler channelWritabilityChanged 的线程是=" + Thread.currentThread().getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("BizHandler exceptionCaught 的线程是=" + Thread.currentThread().getName());
    }
}
