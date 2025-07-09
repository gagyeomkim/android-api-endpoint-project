/**
 * OkHttp의 GET요청을 테스트해보기 위한 클래스
 */
package com.kgg;
import okhttp3.*;   // OKHttpClient, Request, Response등 네트워킹 도구
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject; // JSON 데이터를 다루는 도구
import java.io.IOException; // 네트워크 오류같은 입출력 예외를 처리하는 도구

public class GetTest {
    String SEND_URL;
    String GET_URL;

    //GET_URL에 있는 JSON 데이터의 멤버들
    int userId;
    int postId;
    String title;
    String body;

    public GetTest(String SEND_URL, String GET_URL) {
        this.SEND_URL = SEND_URL;
        this.GET_URL = GET_URL;
    }

    /*
        1. [Intellij -> 서버 | 서버에서 GET요청을 받는 과정 확인하기]
     */
    public void sendUserInfo() {
        // 프롬프트
        System.out.println("Webhook.site로 GET 요청을 보냅니다.");
        System.out.println("요청 주소: " + SEND_URL);

        //OkHttp 객체 생성
        OkHttpClient client = new OkHttpClient();   // HTTP 요청을 보낼 수 있는 Client역할

        // GET 요청 객체 생성
        // url 주소로 GET 요청을 보내달라는 내용의 Request를 생성
        Request request = new Request.Builder()
                .url(SEND_URL)
                .addHeader("User-Agent", "Test-Client-KGG")
                .addHeader("GOAL","IFF-Security")
                .addHeader("Club","Security-FACT")
                .addHeader("Custom-Header","I-WANT-SEE-SERVER")
                .get()  // default가 .get()이지만 가독성을 위해 추가
                .build();

        //동기식 GET - `.execute()`사용
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

    /*
        2. [Intellij -> 서버 -> Intellij | GET요청을 통해 서버로부터 데이터를 받는 과정 확인하기]
     */
    public void getUserInfo() throws IOException {
        //OkHttp 객체 생성
        OkHttpClient client = new OkHttpClient();   // HTTP 요청을 보낼 수 있는 Client역할

        //(추가) Query 파라미터 추가로 필터링해보기
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_URL).newBuilder();
        urlBuilder.addQueryParameter("userId","1")
                .addQueryParameter("id","3");
        String url = urlBuilder.build().toString();
        System.out.println("생성된 최종 URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try(Response response = client.newCall(request).execute()){
            System.out.println("요청 성공 여부 : " + response.isSuccessful());
            System.out.println("HTTP 상태 코드 : " +response.code());

            if(response.isSuccessful() && response.body() != null){
                // response.body().string()은 Stream 방식이라 한번만 호출 가능하므로 변수에 저장해두고 재사용
                String responseData = response.body().string(); // dtype: string
                System.out.println("서버로부터 받은 원본 데이터: \n" + responseData);

                // 원본 데이터를 JSON으로 변형 후 원하는 정보만 추출하기
                // 서버는 기본적으로 여러 필터링 조건을 사용하면, **조건에 맞는 결과가 하나**일지라도 항상 리스트(배열) 형식으로 결과를 반환
                // 따라서 문자열을 JSONArray 로 먼저 변환한 후, 배열의 첫 번째 요소를 JSONObject 로 가져와야 함
                JSONArray jsonArray = new JSONArray(responseData);
                JSONObject jsonObject = jsonArray.getJSONObject(0);   // dtype: JSON 객체 -> Key-Value 쌍
                System.out.println("원본 데이터 -> JSON 데이터: " + jsonObject);
                
                this.userId = jsonObject.getInt("userId");
                this.postId = jsonObject.getInt("id");
                this.title = jsonObject.getString("title");
                this.body = jsonObject.getString("body");
                System.out.println(this);
            }
        }
    }

    /*
        3. [Async][Intellij -> 서버 -> Intellij | 비동기식 GET요청을 통해 서버로부터 데이터를 받는 과정 확인하기]
     */
    public void AsyncgetUserInfo() throws IOException {
        OkHttpClient client = new OkHttpClient();   // HTTP 요청을 보낼 수 있는 Client역할

        //(추가) Query 파라미터 추가로 필터링해보기
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_URL).newBuilder();
        urlBuilder.addQueryParameter("userId","1")
                .addQueryParameter("id","3");
        String url = urlBuilder.build().toString();
        System.out.println("생성된 최종 URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();  //url 주소로 GET 요청을 보내달라는 내용의 Request를 생성
        
        // 비동기식 GET -> .enqueue() 사용하기
        client.newCall(request).enqueue(new Callback() {
            //1. 요청 실패시 처리 X
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("요청 실패: " + e.getMessage());
                e.printStackTrace();
            }
            
            // 2. 요청 성공시 처리
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // response.body().string()은 Stream 방식이라 한번만 호출 가능하므로 변수에 저장해두고 재사용
                String responseData = response.body().string(); // dtype: string
                System.out.println("서버로부터 받은 원본 데이터: \n" + responseData);

                // 원본 데이터를 JSON으로 변형 후 원하는 정보만 추출하기
                // 서버는 기본적으로 여러 필터링 조건을 사용하면, **조건에 맞는 결과가 하나**일지라도 항상 리스트(배열) 형식으로 결과를 반환
                // 따라서 문자열을 JSONArray 로 먼저 변환한 후, 배열의 첫 번째 요소를 JSONObject 로 가져와야 함
                JSONArray jsonArray = new JSONArray(responseData);
                JSONObject jsonObject = jsonArray.getJSONObject(0);   // dtype: JSON 객체 -> Key-Value 쌍
                System.out.println("원본 데이터 -> JSON 데이터: " + jsonObject);

                GetTest.this.userId = jsonObject.getInt("userId");
                GetTest.this.postId = jsonObject.getInt("id");
                GetTest.this.title = jsonObject.getString("title");
                GetTest.this.body = jsonObject.getString("body");
                System.out.println(GetTest.this);
            }
        });
    }

    @Override
    public String toString() {
        return "\n**Parsing 후 개별 데이터 확인**" +
                "\n유저 ID: " + userId +
                "\n게시글 ID: " + postId +
                "\n제목: " + title +
                "\n내용: " + body;
    }
}
