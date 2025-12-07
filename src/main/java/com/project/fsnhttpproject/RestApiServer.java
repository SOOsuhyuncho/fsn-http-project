package com.project.fsnhttpproject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// 원본 Python: lec-06-prg-07-rest-server-v3.py 대응
public class RestApiServer {
    // 회원 정보를 저장할 인메모리 데이터베이스 (파이썬의 database = {} 대응)
    private static final Map<String, String> database = new HashMap<>();

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 5000; // 파이썬 예제와 동일하게 5000번 포트 사용

        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);

        // API 경로 핸들러 등록
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

            // URL에서 member_id 추출 (예: /membership_api/0001 -> 0001)
            String memberId = path.substring(path.lastIndexOf("/") + 1);

            System.out.println("\n## [" + method + "] Request for Member ID: " + memberId);

            String response = "";
            int statusCode = 200;

            if (method.equalsIgnoreCase("POST")) {
                response = createMember(exchange, memberId);
            } else if (method.equalsIgnoreCase("GET")) {
                response = readMember(memberId);
            } else if (method.equalsIgnoreCase("PUT")) {
                response = updateMember(exchange, memberId);
            } else if (method.equalsIgnoreCase("DELETE")) {
                response = deleteMember(memberId);
            } else {
                statusCode = 405; // Method Not Allowed
                response = formatJson(memberId, "Error: Method not supported");
            }

            sendResponse(exchange, statusCode, response);
        }

        // Create (POST)
        private String createMember(HttpExchange exchange, String id) throws IOException {
            String value = getRequestBody(exchange);
            if (database.containsKey(id)) {
                return formatJson(id, "None"); // 이미 존재함
            }
            database.put(id, value);
            return formatJson(id, value);
        }

        // Read (GET)
        private String readMember(String id) {
            if (database.containsKey(id)) {
                return formatJson(id, database.get(id));
            }
            return formatJson(id, "None");
        }

        // Update (PUT)
        private String updateMember(HttpExchange exchange, String id) throws IOException {
            String value = getRequestBody(exchange);
            if (database.containsKey(id)) {
                database.put(id, value);
                return formatJson(id, value);
            }
            return formatJson(id, "None");
        }

        // Delete (DELETE)
        private String deleteMember(String id) {
            if (database.containsKey(id)) {
                database.remove(id);
                return formatJson(id, "Removed");
            }
            return formatJson(id, "None");
        }

        // Body 데이터 읽기 헬퍼 함수
        private String getRequestBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        // JSON 형식 문자열 생성 헬퍼 (라이브러리 없이 구현)
        private String formatJson(String key, String value) {
            return String.format("{\"%s\": \"%s\"}", key, value);
        }

        // 응답 전송 헬퍼
        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
            System.out.println("   >> Response: " + response);
        }
    }
}