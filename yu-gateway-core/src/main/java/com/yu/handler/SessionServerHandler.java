package com.yu.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yu.BaseHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yu
 * @description 会话服务处理器
 * @date 2024-01-24
 */
public class SessionServerHandler extends BaseHandler<FullHttpRequest> {

	private final Logger logger = LoggerFactory.getLogger(SessionServerHandler.class);

	/**
	 * 用于处理会话
	 *
	 * @param ctx     通道处理上下文，提供了与处理器和通道交互的方法
	 * @param channel 表示与客户端的连接通道
	 * @param request 表示一个完整的HTTP请求
	 */
	@Override
	protected void session(ChannelHandlerContext ctx, Channel channel, FullHttpRequest request) {
		logger.info("网关接收请求 uri：{} method：{}", request.uri(), request.method());

		//表示一个完整的HTTP响应,返回信息处理
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		//返回信息控制，就是返回一条信息
		response.content().writeBytes(JSON.toJSONBytes("访问路径被网关管理了 URI：" + request.uri(), SerializerFeature.PrettyFormat));

		HttpHeaders headers = response.headers();
		//设置响应头部的Content-Type字段，表示返回的内容类型为JSON，并且使用UTF-8字符集
		headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + "; charset=UTF-8");
		//设置响应头部的Content-Length字段，表示响应体的长度，使用response.content().readableBytes()获取实际长度
		headers.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		//设置响应头部的Connection字段，表示使用持久连接，使得连接在请求和响应之间保持打开状态，以便可以复用
		headers.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		//配置跨域访问
		headers.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		headers.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
		headers.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");
		headers.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

		//将构建好的响应写入通道，并通过writeAndFlush方法刷新通道，确保数据即时发送到客户端
		//这标志着HTTP响应已经被完整地构建并发送给了客户端
		channel.writeAndFlush(response);
	}
}
