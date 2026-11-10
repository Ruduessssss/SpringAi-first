package com.lycai.mock;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class MockModelServer {

    public static void main(String[] args) throws IOException {
        AtomicInteger requestCount = new AtomicInteger();

        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 18089), 0
        );

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();

            if (!"POST".equals(exchange.getRequestMethod())
                    || !path.endsWith("/chat/completions")) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            // 消费请求体，但不打印问题、密钥或请求头
            try (var input = exchange.getRequestBody()) {
                input.transferTo(java.io.OutputStream.nullOutputStream());
            }

            int count = requestCount.incrementAndGet();
            int status;
            String response;

            if (false) {
                status = 503;
                response = """
                        {
                          "error": {
                            "message": "模拟临时故障",
                            "type": "server_error",
                            "code": "service_unavailable"
                          }
                        }
                        """;
            } else {
                status = 200;
                response = """
                        {
                          "id": "chatcmpl-local",
                          "object": "chat.completion",
                          "created": 0,
                          "model": "mock-model",
                          "choices": [
                            {
                              "index": 0,
                              "message": {
                                "role": "assistant",
                                "content": " 123 "
                              },
                              "finish_reason": "stop"
                            }
                          ],
                          "usage": {
                            "prompt_tokens": 1,
                            "completion_tokens": 1,
                            "total_tokens": 2
                          }
                        }
                        """;
            }

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type", "application/json; charset=utf-8"
            );

            System.out.printf(
                    "模拟服务收到第 %d 次请求，路径=%s，返回=%d%n",
                    count, path, status
            );
//            try {
//                Thread.sleep(3_000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                exchange.close();
//                return;
//            }
            exchange.sendResponseHeaders(status, bytes.length);
            try (var output = exchange.getResponseBody()) {
                output.write(bytes);
            } finally {
                exchange.close();
            }
        });

        server.start();
        System.out.println("模拟模型服务已启动，端口：18089");
    }
}