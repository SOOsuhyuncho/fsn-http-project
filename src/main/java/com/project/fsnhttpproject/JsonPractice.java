package com.project.fsnhttpproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// 원본 Python: lec-06-prg-03 ~ 06 (JSON 읽기/쓰기/변환 통합 예제)
public class JsonPractice {

    public static void main(String[] args) {
        System.out.println("## JSON Practice Started...");

        // 1. JSON 데이터 준비 (Super Hero 예제 데이터)
        String jsonString = "{\n" +
                "  \"squadName\": \"Super hero squad\",\n" +
                "  \"homeTown\": \"Metro City\",\n" +
                "  \"formed\": 2016,\n" +
                "  \"active\": true\n" +
                "}";

        // 2. 파일로 저장하기 (lec-06-prg-04 대응)
        String fileName = "super_hero.json";
        saveJsonToFile(fileName, jsonString);

        // 3. 파일 읽어오기 (lec-06-prg-03 대응)
        // [수정된 부분] 이제 이 함수가 정상적으로 String을 반환합니다.
        String readData = readJsonFromFile(fileName);
        System.out.println("## Read from file:\n" + readData);

        // 4. 데이터 파싱 맛보기 (lec-06-prg-06 대응 - 문자열 처리)
        String homeTown = parseValue(readData, "homeTown");
        System.out.println("## Parsed 'homeTown': " + homeTown);

        System.out.println("## JSON Practice Completed.");
    }

    // 파일 저장 함수
    private static void saveJsonToFile(String fileName, String content) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(content);
            System.out.println("## Successfully saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // [수정된 함수] void -> String으로 변경
    private static String readJsonFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ""; // 에러 발생 시 빈 문자열 반환
        }
        return content.toString(); // 파일 내용 반환
    }

    // 간단한 문자열 파싱 함수
    private static String parseValue(String json, String key) {
        if (json == null || key == null) return "Error";

        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "Not Found";

        startIndex += searchKey.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = json.indexOf("\n", startIndex);

        if (endIndex == -1) return "Not Found"; // 파싱 실패 시 방어 코드

        String value = json.substring(startIndex, endIndex).trim();
        return value.replace("\"", "");
    }
}