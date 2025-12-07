package com.project.fsnhttpproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpWebClient {

    public static void main(String[] args) {
        System.out.println("## HTTP client started.");

        // 1. GET 요청 테스트: 단순 경로 조회
        sendGetRequest("http://localhost:8080/temp/");

        // 2. GET 요청 테스트: 쿼리 파라미터 전달
        sendGetRequest("http://localhost:8080/?var1=9&var2=9");

        // 3. POST 요청 테스트: 요청 본문(Body) 데이터 전달
        sendPostRequest("http://localhost:8080", "var1=9&var2=9");

        System.out.println("## HTTP client completed.");
    }

    // GET 요청 전송 메서드
    private static void sendGetRequest(String targetUrl) {
        System.out.println("## GET request for " + targetUrl);
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // 응답 처리
            printResponse(con, "GET");
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // POST 요청 전송 메서드
    private static void sendPostRequest(String targetUrl, String postData) {
        System.out.println("## POST request for " + targetUrl + " with " + postData);
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            // 데이터 출력 스트림 활성화 (POST Body 전송용)
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 처리
            printResponse(con, "POST");
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 서버 응답 데이터 읽기 및 출력 메서드
    private static void printResponse(HttpURLConnection con, String method) throws IOException {
        System.out.println("## " + method + " response [start]");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;

            // [수정 완료] 변수명 오타 수정 (line -> responseLine)
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println("Error reading response: " + e.getMessage());
        }

        System.out.println("## " + method + " response [end]");
        System.out.println();
    }
}