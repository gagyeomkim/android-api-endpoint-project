package com.kgg;
import java.io.IOException; // 네트워크 오류같은 입출력 예외를 처리하는 도구

public class GetRun {
    String SEND_URL;
    String GET_URL;

    public GetRun(String SEND_URL, String GET_URL) {
        this.SEND_URL = SEND_URL;
        this.GET_URL = GET_URL;
    }

    public void run() throws IOException, InterruptedException {
        GetTest get = new GetTest(this.SEND_URL, this.GET_URL);
        System.out.println("1. [Intellij -> 서버 | 서버에서 GET요청을 받는 과정 확인하기]");
        get.sendUserInfo(); // 동기식

        System.out.println("\n2. [Intellij -> 서버 -> Intellij | GET요청을 통해 서버로부터 데이터를 받는 과정 확인하기]");
        get.getUserInfo();  // 동기식

        System.out.println("\n3. [Async][Intellij -> 서버 -> Intellij | 비동기식 GET요청을 통해 서버로부터 데이터를 받는 과정 확인하기]");
        get.AsyncgetUserInfo(); // 비동기식
        System.out.println("**For AsyncgetUserInfo... 백그라운드 작업 완료 대기 중**");
        Thread.sleep(3000); // 3초 대기
    }
}
