package com.project.fsnhttpproject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RestApiClient {

    public static void main(String[] args) {
        System.out.println("## REST API Client Started...");

        String baseUrl = "http://127.0.0.1:5000/membership_api/";

        // 테스트 시나리오 실행

        // 1. 존재하지 않는 회원 조회
        sendRequest(baseUrl + "0001", "GET", null, "#1 Read non-registered");

        // 2. 신규 회원 생성
        sendRequest(baseUrl + "0001", "POST", "apple", "#2 Create new member");

        // 3. 등록된 회원 조회
        sendRequest(baseUrl + "0001", "GET", null, "#3 Read registered member");

        // 4. 중복 회원 생성 시도
        sendRequest(baseUrl + "0001", "POST", "xpple", "#4 Create duplicate member");

        // 5. 존재하지 않는 회원 수정 시도
        sendRequest(baseUrl + "0002", "PUT", "xrange", "#5 Update non-registered");

        // 6. 회원 생성 및 수정
        sendRequest(baseUrl + "0002", "POST", "xrange", "#6-1 Create another member");
        sendRequest(baseUrl + "0002", "PUT", "orange", "#6-2 Update member");

        // 7. 회원 삭제
        sendRequest(baseUrl + "0001", "DELETE", null, "#7 Delete member");

        // 8. 존재하지 않는 회원 삭제 시도
        sendRequest(baseUrl + "0001", "DELETE", null, "#8 Delete non-registered");
    }

    // HTTP 요청 전송을 위한 일반화된 메서드
    private static void sendRequest(String targetUrl, String method, String bodyData, String testTitle) {
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);

            // POST 또는 PUT 요청 시 데이터 전송 설정
            if (bodyData != null) {
                con.setDoOutput(true);
                try (OutputStream os = con.getOutputStream()) {
                    os.write(bodyData.getBytes(StandardCharsets.UTF_8));
                }
            }

            int responseCode = con.getResponseCode();

            // 응답 데이터 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            System.out.println(testTitle + " >> Code: " + responseCode + ", JSON: " + response.toString());

        } catch (Exception e) {
            System.out.println(testTitle + " >> Error: " + e.getMessage());
        }
    }
}