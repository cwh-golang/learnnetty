package com.io.netty.source.block;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Stone
 * @date 2022/6/28
 */
public class WorkerIntrospectHandler extends ChannelInboundHandlerAdapter {
    private final AttributeKey<byte[]> srcdataAttrKey = AttributeKey.valueOf("srcdata");
    private static AtomicLong count = new AtomicLong(0);


    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        if (channelHandlerContext.channel().hasAttr(srcdataAttrKey)) {
            System.out.println("WorkerIntrospectHandler channelActive 的线程是=" + Thread.currentThread().getName());
        } else {
            super.channelActive(channelHandlerContext);
        }
//        channelHandlerContext.close();
//        super.channelActive(channelHandlerContext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().hasAttr(srcdataAttrKey)) {
            System.out.println("WorkerIntrospectHandler channelInactive 的线程是=" + Thread.currentThread().getName());
        } else {
            super.channelInactive(ctx);
        }
//        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        super.channelUnregistered(ctx);
        if (ctx.channel().hasAttr(srcdataAttrKey)) {
            System.out.println("WorkerIntrospectHandler channelUnregistered 的线程是=" + Thread.currentThread().getName());
        } else {
            super.channelUnregistered(ctx);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        super.channelRegistered(ctx);
        System.out.println("WorkerIntrospectHandler channelRegistered 的线程是=" + Thread.currentThread().getName());
//        ctx.close();
        long andIncrement = count.getAndIncrement();
        if (andIncrement % 2 == 0) {
            System.out.println("andIncrement = " + andIncrement);
            ctx.channel().attr(srcdataAttrKey);
            ctx.close();
        } else {
            super.channelRegistered(ctx);
        }

    }
}
