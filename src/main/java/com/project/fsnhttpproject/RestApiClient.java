package com.project.fsnhttpproject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// 원본 Python: lec-06-prg-08-rest-client-v3.py 대응
public class RestApiClient {

    public static void main(String[] args) {
        System.out.println("## REST API Client Started...");

        String baseUrl = "http://127.0.0.1:5000/membership_api/";

        // 1. 없는 멤버 조회 (에러 케이스)
        sendRequest(baseUrl + "0001", "GET", null, "#1 Read non-registered");

        // 2. 신규 멤버 생성 (정상 케이스) -> body: "apple"
        sendRequest(baseUrl + "0001", "POST", "apple", "#2 Create new member");

        // 3. 생성된 멤버 조회 (정상 케이스)
        sendRequest(baseUrl + "0001", "GET", null, "#3 Read registered member");

        // 4. 이미 있는 멤버 생성 시도 (에러 케이스) -> body: "xpple"
        sendRequest(baseUrl + "0001", "POST", "xpple", "#4 Create duplicate member");

        // 5. 없는 멤버 수정 시도 (에러 케이스) -> body: "xrange"
        sendRequest(baseUrl + "0002", "PUT", "xrange", "#5 Update non-registered");

        // 6. 멤버 추가 후 수정 (정상 케이스)
        sendRequest(baseUrl + "0002", "POST", "xrange", "#6-1 Create another member");
        sendRequest(baseUrl + "0002", "PUT", "orange", "#6-2 Update member");

        // 7. 멤버 삭제 (정상 케이스)
        sendRequest(baseUrl + "0001", "DELETE", null, "#7 Delete member");

        // 8. 없는 멤버 삭제 (에러 케이스)
        sendRequest(baseUrl + "0001", "DELETE", null, "#8 Delete non-registered");
    }

    // HTTP 요청 전송 통합 함수
    private static void sendRequest(String targetUrl, String method, String bodyData, String testTitle) {
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);

            // POST나 PUT일 경우 Body 데이터 전송
            if (bodyData != null) {
                con.setDoOutput(true);
                try (OutputStream os = con.getOutputStream()) {
                    os.write(bodyData.getBytes(StandardCharsets.UTF_8));
                }
            }

            int responseCode = con.getResponseCode();

            // 응답 읽기
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