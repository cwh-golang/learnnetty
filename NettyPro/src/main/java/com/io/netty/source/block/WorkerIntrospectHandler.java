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
        if (isLimited()) {
            System.out.println("andIncrement = " + count.get());
            ctx.channel().attr(srcdataAttrKey);
            ctx.close();
        } else {
            super.channelRegistered(ctx);
        }
    }

    //todo 压测机器复现机器僵死

    private boolean isLimited(){
        //可以根据设备的在线数量，进行动态阈值调整
        //在机器刚加入到elb里面去，这时，由于外部的流量巨大，这时应该限制连接数量为一个较小的值，比如 1000，这样能有效的保护
        //后端业务处理的线程和后端模块
        //随着连接的建立，
        //主要的目的上为了让设备连接的数量平滑增长，不会导致后端有洪峰，因为在建立连接的时候，业务处理相对比较复杂
        if (count.getAndIncrement()>Integer.MAX_VALUE){
            return true;
        }
        return false;
    }
}
