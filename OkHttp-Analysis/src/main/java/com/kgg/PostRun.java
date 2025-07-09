package com.kgg;

import java.io.IOException;

public class PostRun {
    String SEND_URL;

    public PostRun(String SEND_URL) {
        this.SEND_URL = SEND_URL;
    }

    public void run() throws IOException, InterruptedException {
        PostTest post = new PostTest(this.SEND_URL);

        System.out.println("\n1. [Intellij -> 서버 | 서버에서 POST요청을 받는 과정 확인하기]");
        post.sendUserInfo();

        System.out.println("\n2. [Async][Intellij -> 서버 | 서버에서 비동기식 POST요청을 받는 과정 확인하기]");
        post.AsyncsendUserInfo();
        System.out.println("**For AsyncgetUserInfo... 백그라운드 작업 완료 대기 중**");
        Thread.sleep(3000); // 3초 대기
    }
}
