/**
 * OKHttp 라이브러리 분석 및 코드 실습
 * 실행용 Main 클래스
 * @author 김가겸
 * @since 2025-07-09
 */
package com.kgg;
import java.io.IOException; // 네트워크 오류같은 입출력 예외를 처리하는 도구

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 데이터를 제공하는 테스트 API URL
        String SEND_URL = "https://webhook.site/ebdca94e-0938-4c0b-98ee-84ef9e1d3c35";
        String GET_URL = "https://jsonplaceholder.typicode.com/posts";

        GetRun gr = new GetRun(SEND_URL, GET_URL);
        gr.run();

        System.out.println();
        System.out.println("-".repeat(100));

        PostRun pr = new PostRun(SEND_URL);
        pr.run();

        System.exit(0);
    }
}

