package com.project.fsnhttpproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// 원본 Python 코드: lec-06-prg-02-http-web-client.py 대응
public class HttpWebClient {

    public static void main(String[] args) {
        System.out.println("## HTTP client started.");

        // 1. GET 요청: 디렉토리 조회 (단순 경로)
        sendGetRequest("http://localhost:8080/temp/");

        // 2. GET 요청: 파라미터 전달 (계산 요청: 9 x 9)
        sendGetRequest("http://localhost:8080/?var1=9&var2=9");

        // 3. POST 요청: Body 데이터 전달 (계산 요청: 9 x 9)
        sendPostRequest("http://localhost:8080", "var1=9&var2=9");

        System.out.println("## HTTP client completed.");
    }

    // GET 요청을 보내는 함수
    private static void sendGetRequest(String targetUrl) {
        System.out.println("## GET request for " + targetUrl);
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // 응답 출력
            printResponse(con, "GET");
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // POST 요청을 보내는 함수
    private static void sendPostRequest(String targetUrl, String postData) {
        System.out.println("## POST request for " + targetUrl + " with " + postData);
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true); // Body 데이터 전송을 위해 필수 설정

            // 데이터 전송 (Body 쓰기)
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 출력
            printResponse(con, "POST");
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 서버로부터 온 응답을 읽어서 출력하는 함수
    private static void printResponse(HttpURLConnection con, String method) throws IOException {
        System.out.println("## " + method + " response [start]");

        // 입력 스트림(데이터) 읽기
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
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