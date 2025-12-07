package com.project.fsnhttpproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonPractice {

    public static void main(String[] args) {
        System.out.println("## JSON 데이터 처리 실습 시작...");

        // 1. JSON 데이터 생성
        // 외부 라이브러리(Gson, Jackson 등) 의존성을 제거하기 위해
        // 문자열 포맷팅을 사용하여 JSON 구조를 직접 생성함
        String jsonString = "{\n" +
                "  \"squadName\": \"Super hero squad\",\n" +
                "  \"homeTown\": \"Metro City\",\n" +
                "  \"formed\": 2016,\n" +
                "  \"active\": true\n" +
                "}";

        // 2. 파일 저장 (I/O)
        // FileWriter를 사용하여 생성된 JSON 문자열을 파일로 기록
        String fileName = "super_hero.json";
        saveJsonToFile(fileName, jsonString);

        // 3. 파일 읽기 (I/O)
        // BufferedReader를 사용하여 라인 단위로 파일을 읽고 문자열로 재조립
        // [수정 완료] 반환된 문자열을 변수에 저장 (기존 void 에러 해결)
        String readData = readJsonFromFile(fileName);
        System.out.println("## 파일에서 읽은 데이터:\n" + readData);

        // 4. 데이터 파싱 (Parsing)
        // 문자열 검색(indexOf) 및 추출(substring) 로직을 사용하여 특정 키 값("homeTown") 획득
        String homeTown = parseValue(readData, "homeTown");
        System.out.println("## 파싱된 'homeTown': " + homeTown);

        System.out.println("## JSON 데이터 처리 실습 완료.");
    }

    // 파일 저장 유틸리티 메서드
    private static void saveJsonToFile(String fileName, String content) {
        // try-with-resources 구문을 사용하여 자원 해제(close) 안정성 확보
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(content);
            System.out.println("## 성공적으로 저장됨: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일 읽기 유틸리티 메서드
    // [수정됨] void -> String 반환으로 변경하여 호출 측에서 데이터를 받을 수 있도록 함
    private static String readJsonFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ""; // 예외 발생 시 빈 문자열 반환
        }
        return content.toString(); // 읽은 파일 내용 반환
    }

    // 경량 JSON 파싱 메서드 (문자열 조작 방식)
    private static String parseValue(String json, String key) {
        if (json == null || key == null) return "Error";

        // 키 패턴 검색 (예: "key":)
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);

        // 키가 존재하지 않을 경우 처리
        if (startIndex == -1) return "Not Found";

        // 값의 시작 인덱스 계산
        startIndex += searchKey.length();

        // 값의 종료 인덱스 계산 (콤마 또는 줄바꿈 기준)
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = json.indexOf("\n", startIndex);

        if (endIndex == -1) return "Not Found";

        // 불필요한 공백 및 따옴표 제거 후 순수 데이터 반환
        String value = json.substring(startIndex, endIndex).trim();
        return value.replace("\"", "");
    }
}