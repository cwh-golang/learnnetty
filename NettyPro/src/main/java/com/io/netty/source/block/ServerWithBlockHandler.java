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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Echoes back any received data from a client.
 */
public final class ServerWithBlockHandler {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

    //创建业务线程池
    //这里我们就创建2个子线程
//    static final EventExecutorGroup group = new UnorderedThreadPoolEventExecutor(2, new DefaultThreadFactory("自定义线程池"));
    static final EventExecutorGroup group = new DefaultEventExecutorGroup(2, new DefaultThreadFactory("自定义线程池"));

    public static void main(String[] args) throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(8, new DefaultThreadFactory("work"));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    // boss 线程调用
                    // 1、记录设备连接数；
                    // 2、触发阈值、直接拒绝链接；
//                    .handler(new ConnectMonitorHandler(LogLevel.INFO))
//             .handler(new ConnectMonitorHandler())
                    .handler(new BossIntrospectHandler())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // worker group 线程执行的
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
//                            p.addLast(new WorkerIntrospectHandler());
                            p.addLast(group, new BizHandler());
//                            p.addLast(group, new BlockHandler());
                            //说明: 如果我们在addLast 添加handler ，前面有指定
                            //EventExecutorGroup, 那么该handler 会优先加入到该线程池中，也就是说只有EchoServerHandler会加入该线程池中；
                            //但其他handler依旧是在 WordGroup线程池中执行的
//                     p.addLast(group, new EchoServerHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(PORT).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
