package com.project.fsnhttpproject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// 원본 Python 코드: lec-06-prg-01-http-web-server.py
public class HttpWebServer {

    public static void main(String[] args) throws IOException {
        // 서버 설정
        String host = "localhost";
        int port = 8080;

        // 1. 서버 생성 (최대 대기열은 0으로 설정하여 기본값 사용)
        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);

        // 2. 요청을 처리할 핸들러 등록 (루트 경로 "/")
        server.createContext("/", new MyHttpHandler());

        // 3. 실행자 설정 (null로 설정하면 기본 실행자 사용)
        server.setExecutor(null);

        System.out.println("## HTTP server started at http://" + host + ":" + port + ".");

        // 서버 시작
        server.start();
    }

    // 실제 요청을 처리하는 핸들러 클래스
    static class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();

            // Python의 do_GET, do_POST 분기 처리
            if (requestMethod.equalsIgnoreCase("GET")) {
                handleGetRequest(exchange);
            } else if (requestMethod.equalsIgnoreCase("POST")) {
                handlePostRequest(exchange);
            }
        }

        // GET 요청 처리 함수
        private void handleGetRequest(HttpExchange exchange) throws IOException {
            System.out.println("## do_GET() activated.");

            // 요청받은 URL 확인
            String requestURI = exchange.getRequestURI().toString();
            String response = "";

            // 파라미터가 있는 경우 (예: ?var1=9&var2=9)
            if (requestURI.contains("?")) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQuery(query);

                // var1과 var2가 모두 있는지 확인
                if (params.containsKey("var1") && params.containsKey("var2")) {
                    int var1 = Integer.parseInt(params.get("var1"));
                    int var2 = Integer.parseInt(params.get("var2"));

                    // 곱셈 계산 (simple_calc 역할)
                    int result = var1 * var2;

                    // 응답 HTML 생성
                    response = "<html>GET request for calculation => " + var1 + " x " + var2 + " = " + result + "</html>";
                    System.out.println("## GET request for calculation => " + var1 + " x " + var2 + " = " + result + ".");
                }
            } else {
                // 파라미터가 없는 경우 (디렉토리 조회 등)
                response = "<html><p>HTTP Request GET for Path: " + exchange.getRequestURI().getPath() + "</p></html>";
                System.out.println("## GET request for directory => " + exchange.getRequestURI().getPath() + ".");
            }

            // 클라이언트에게 응답 전송
            sendResponse(exchange, response);
        }

        // POST 요청 처리 함수
        private void handlePostRequest(HttpExchange exchange) throws IOException {
            System.out.println("## do_POST() activated.");

            // Body 데이터 읽기
            InputStream is = exchange.getRequestBody();
            // Java 9 이상에서는 readAllBytes() 사용 가능. 그 이하는 별도 로직 필요하지만 M2 환경이시니 괜찮습니다.
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Body 내용을 파싱 (Query String 형태라고 가정)
            Map<String, String> params = parseQuery(body);

            if (params.containsKey("var1") && params.containsKey("var2")) {
                int var1 = Integer.parseInt(params.get("var1"));
                int var2 = Integer.parseInt(params.get("var2"));

                // 곱셈 계산
                int result = var1 * var2;

                // 응답 문자열 생성
                String response = "POST request for calculation => " + var1 + " x " + var2 + " = " + result;

                System.out.println("## POST request data => " + body + ".");
                System.out.println("## POST request for calculation => " + var1 + " x " + var2 + " = " + result + ".");

                sendResponse(exchange, response);
            }
        }

        // 응답 전송을 위한 헬퍼 함수
        private void sendResponse(HttpExchange exchange, String responseBody) throws IOException {
            // 헤더 설정 (200 OK)
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            byte[] rawResponse = responseBody.getBytes(StandardCharsets.UTF_8);

            // 응답 길이와 상태코드 전송
            exchange.sendResponseHeaders(200, rawResponse.length);

            // 내용(Body) 쓰기
            OutputStream os = exchange.getResponseBody();
            os.write(rawResponse);
            os.close();
        }

        // 쿼리 스트링(key=value&key=value)을 맵으로 변환하는 함수
        private Map<String, String> parseQuery(String query) {
            Map<String, String> result = new HashMap<>();
            if (query == null) return result;

            // & 기준으로 나누기
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                // = 기준으로 나누기
                String[] entry = pair.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                }
            }
            return result;
        }
    }
}