package com.project.fsnhttpproject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RestApiServer {
    // 회원 정보를 저장하기 위한 인메모리 데이터베이스
    private static final Map<String, String> database = new HashMap<>();

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 5000;

        // HTTP 서버 초기화
        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);

        // 멤버십 API 처리를 위한 핸들러 등록
        server.createContext("/membership_api/", new MembershipHandler());

        server.setExecutor(null);
        System.out.println("## REST API Server started at http://" + host + ":" + port);
        server.start();
    }

    static class MembershipHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            // URL 경로에서 회원 ID 추출 (예: .../0001)
            String memberId = path.substring(path.lastIndexOf("/") + 1);

            System.out.println("\n## [" + method + "] Request for Member ID: " + memberId);

            String response = "";
            int statusCode = 200;

            // HTTP 메서드에 따른 요청 라우팅
            if ("POST".equalsIgnoreCase(method)) {
                response = createMember(exchange, memberId);
            } else if ("GET".equalsIgnoreCase(method)) {
                response = readMember(memberId);
            } else if ("PUT".equalsIgnoreCase(method)) {
                response = updateMember(exchange, memberId);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                response = deleteMember(memberId);
            } else {
                statusCode = 405; // 허용되지 않은 메서드 처리
                response = formatJson(memberId, "Error: Method not supported");
            }

            sendResponse(exchange, statusCode, response);
        }

        // 회원 생성 (POST)
        private String createMember(HttpExchange exchange, String id) throws IOException {
            String value = getRequestBody(exchange);
            if (database.containsKey(id)) {
                return formatJson(id, "None");
            }
            database.put(id, value);
            return formatJson(id, value);
        }

        // 회원 조회 (GET)
        private String readMember(String id) {
            if (database.containsKey(id)) {
                return formatJson(id, database.get(id));
            }
            return formatJson(id, "None");
        }

        // 회원 정보 수정 (PUT)
        private String updateMember(HttpExchange exchange, String id) throws IOException {
            String value = getRequestBody(exchange);
            if (database.containsKey(id)) {
                database.put(id, value);
                return formatJson(id, value);
            }
            return formatJson(id, "None");
        }

        // 회원 삭제 (DELETE)
        private String deleteMember(String id) {
            if (database.containsKey(id)) {
                database.remove(id);
                return formatJson(id, "Removed");
            }
            return formatJson(id, "None");
        }

        // 요청 본문(Body) 읽기 헬퍼 메서드
        private String getRequestBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        // JSON 문자열 포맷팅 헬퍼 메서드
        private String formatJson(String key, String value) {
            return String.format("{\"%s\": \"%s\"}", key, value);
        }

        // 응답 전송 헬퍼 메서드
        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(statusCode, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
            System.out.println("   >> Response: " + response);
        }
    }
}