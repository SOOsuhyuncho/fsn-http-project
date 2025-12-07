package com.project.fsnhttpproject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpWebServer {

    public static void main(String[] args) throws IOException {
        // 서버 설정 정보
        String host = "localhost";
        int port = 8080;

        // HTTP 서버 인스턴스 생성 및 포트 바인딩
        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);

        // 루트 경로("/")에 대한 요청 핸들러 등록
        server.createContext("/", new MyHttpHandler());

        // 기본 실행자(Executor) 설정 및 서버 시작
        server.setExecutor(null);
        System.out.println("## HTTP server started at http://" + host + ":" + port + ".");
        server.start();
    }

    /**
     * HTTP 요청을 처리하는 핵심 핸들러 클래스
     * GET 및 POST 메서드에 따라 요청을 분기 처리함
     */
    static class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();

            // 요청 메서드 유형에 따른 로직 분기
            if (requestMethod.equalsIgnoreCase("GET")) {
                handleGetRequest(exchange);
            } else if (requestMethod.equalsIgnoreCase("POST")) {
                handlePostRequest(exchange);
            } else {
                // 지원하지 않는 메서드에 대한 예외 처리 (선택 사항)
                exchange.sendResponseHeaders(405, -1);
            }
        }

        // GET 요청 처리: 쿼리 파라미터 파싱 및 연산 수행
        private void handleGetRequest(HttpExchange exchange) throws IOException {
            System.out.println("## do_GET() activated.");

            String requestURI = exchange.getRequestURI().toString();
            String response = "";

            // 쿼리 스트링 존재 여부 확인
            if (requestURI.contains("?")) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQuery(query);

                // 연산에 필요한 파라미터 유효성 검증
                if (params.containsKey("var1") && params.containsKey("var2")) {
                    try {
                        int var1 = Integer.parseInt(params.get("var1"));
                        int var2 = Integer.parseInt(params.get("var2"));
                        int result = var1 * var2;

                        response = "<html>GET request for calculation => " + var1 + " x " + var2 + " = " + result + "</html>";
                        System.out.println("## GET request for calculation => " + var1 + " x " + var2 + " = " + result + ".");
                    } catch (NumberFormatException e) {
                        response = "Invalid Parameter Format";
                    }
                }
            } else {
                // 파라미터가 없는 경우 기본 경로 정보 반환
                response = "<html><p>HTTP Request GET for Path: " + exchange.getRequestURI().getPath() + "</p></html>";
                System.out.println("## GET request for directory => " + exchange.getRequestURI().getPath() + ".");
            }
            sendResponse(exchange, response);
        }

        // POST 요청 처리: 요청 본문(Body) 데이터 파싱 및 연산 수행
        private void handlePostRequest(HttpExchange exchange) throws IOException {
            System.out.println("## do_POST() activated.");

            // 요청 본문 데이터 읽기
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // 본문 데이터를 맵 형태로 파싱
            Map<String, String> params = parseQuery(body);

            if (params.containsKey("var1") && params.containsKey("var2")) {
                int var1 = Integer.parseInt(params.get("var1"));
                int var2 = Integer.parseInt(params.get("var2"));
                int result = var1 * var2;

                String response = "POST request for calculation => " + var1 + " x " + var2 + " = " + result;

                System.out.println("## POST request data => " + body + ".");
                System.out.println("## POST request for calculation => " + var1 + " x " + var2 + " = " + result + ".");

                sendResponse(exchange, response);
            }
        }

        // 응답 전송을 위한 헬퍼 메서드
        private void sendResponse(HttpExchange exchange, String responseBody) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);

            // HTTP 상태 코드 200(OK) 및 응답 길이 설정
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }

        // 쿼리 스트링 파싱 유틸리티 메서드 (Key-Value 변환)
        private Map<String, String> parseQuery(String query) {
            Map<String, String> result = new HashMap<>();
            if (query == null || query.isEmpty()) return result;

            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] entry = pair.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                }
            }
            return result;
        }
    }
}