package com.kgg;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class PostTest {
    String SEND_URL;

    public PostTest(String SEND_URL) {
        this.SEND_URL = SEND_URL;
    }

    public void sendUserInfo() {
        System.out.println("Webhook.site로 POST 요청을 보냅니다.");
        System.out.println("요청 주소: " + SEND_URL);

        OkHttpClient client = new OkHttpClient();

        // POST요청으로 전송할 JSON 데이터 생성
        String data = "{\"name\":\"SecurityFACT\", \"job\":\"developer\"}";
        
        // RequestBody 생성
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), // POST 요청시 본문에 담기는 데이터의 형식을 알려줌
                data    // 전송할 JSON 데이터
        );

        Request request = new Request.Builder()
                .url(SEND_URL)
                .addHeader("User-Agent", "Test-Client-KGG")
                .addHeader("Custom-Header","I-WANT-SEE-SERVER")
                .post(body) // GET() 요청때와의 차이점
                .build();


        //동기식 POST - `.execute()`사용 - GET요청과 동일하게 구성 가능
        //해당 요청서(request)를 client에게 전달하여 서버에 요청을 보내고 응답이 올때까지 기다림
        // try 블록이 끝나면 response 객체의 자원을 자동 정리하게 구성
        try(Response response = client.newCall(request).execute()){
            if(response.isSuccessful()) {
                System.out.println("요청 성공! Webbhook.site 페이지를 확인하세요");
                System.out.println("응답 코드 : " + response.code());
                System.out.println("응답 내용 : " + response.body().string());
            } else{
                System.err.println("요청 실패 : " + response.code());
            }
        } catch (IOException e) {
            System.out.println("네트워크 오류 발생");
            throw new RuntimeException(e);
        }
    }

    public void AsyncsendUserInfo() {
        System.out.println("Webhook.site로 POST 요청을 보냅니다.");
        System.out.println("요청 주소: " + SEND_URL);

        OkHttpClient client = new OkHttpClient();
        // 전송할 JSON 데이터 생성
        String data = "{\"name\":\"SecurityFACT\", \"job\":\"developer\"}";

        // RequestBody 생성
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                data
        );

        Request request = new Request.Builder()
                .url(SEND_URL)
                .addHeader("User-Agent", "Test-Client-KGG")
                .addHeader("Custom-Header","I-WANT-SEE-SERVER")
                .post(body)
                .build();

        
        // 비동기식 POST 요청 -> .enqueue() 사용
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    System.out.println("요청 성공 여부: " + response.isSuccessful());
                    System.out.println("응답 코드: " + response.code());

                    if (body != null) { // response가 있다면
                        // .string() 메서드를 사용해야 실제 응답 내용을 문자열로 가져올 수 있음
                        String responseData = body.string();
                        System.out.println("응답 내용: " + responseData);
                    }
                }
            }
        });
    }
}
