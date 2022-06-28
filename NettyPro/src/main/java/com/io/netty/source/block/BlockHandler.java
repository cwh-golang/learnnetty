/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.io.netty.source.block;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class BlockHandler extends SimpleChannelInboundHandler<Object> {

    private static AtomicLong count= new AtomicLong(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("EchoServer Handler channelActive 的线程是=" + Thread.currentThread().getName());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("EchoServer Handler channelInactive 的线程是=" + Thread.currentThread().getName());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("EchoServer Handler 的线程是=" + Thread.currentThread().getName() + msg.toString());
//        ByteBuf in = (ByteBuf) msg;
//        //将消息记录到控制台
//        System.out.println(
//                "channelRead: " + in.toString(CharsetUtil.UTF_8));
        System.out.println(count.getAndIncrement());
        try {
//            Thread.sleep(1000 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(ctx);
        ByteBuf in = (ByteBuf) msg;
        //将消息记录到控制台
        System.out.println(
                " channelRead0 : " + in.toString(CharsetUtil.UTF_8));
        try {
//            Thread.sleep(1000 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        ctx.close();
    }
}
