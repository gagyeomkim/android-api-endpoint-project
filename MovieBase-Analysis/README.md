## APK 분석: MovieBase
- **분석 목표:** **Moviebase: TV & Movie Tracker 5.3.21 apk**의 API 엔드포인트를 추출하고, 라이브러리 사용 방식과 엔드포인트 생성 구조를 분석하여 자동화 방안을 모색.
- **분석 대상:** [**Moviebase: TV & Movie Tracker 5.3.21**](https://www.apkmirror.com/apk/chris-krueger/moviebase-manage-movies-tv-shows/moviebase-tv-movie-tracker-5-3-21-release/)
- **분석 환경 및 도구:** JEB, apktool

## Moviebase: TV & Movie Tracker 5.3.21 apk API Endpoint 분석 결과
![자동화구현영상](https://github.com/user-attachments/assets/ad486de6-a742-422b-a858-a391d435ea86)

- 해당 자동화 코드를 통해 얻을 수 있는 효과는 다음과 같다.
    - 아래와 같이 apk 내 존재하는 endpoint “path”의 후보 검색
    - 해당 path들이 어떤 HTTP-Method로 사용되는지 알 수 있음
    - 어떤 파일에서 해당 endpoint path들을 발견할 수 있는지 알 수 있음
    - 즉, “현재까지 알고있는” `METHOD-PATH-DIRECTORY` 의 관계를 바로 확인가능
    - 추가로, 내가 모르는 Annotation들과, 앞으로 분석해야할 path들에 대해서도 알 수 있음
- 한계
    - BASE URL은 아직 구하지 못하여, 정적분석이 다시 필요함 -> 즉, endpoint가 바로 나오진 않음.

## 참고 사항
위의 API Endpoint 분석 결과 영상은 Trakt TV의 Android APK를 정적 분석하여 얻은 API 엔드포인트 정보를 정리한 것입니다.
- 포함된 정보: HTTP 메소드, path, 파일명
- 포함하지 않은 정보: 인증 헤더, 실제 API 응답, 토큰, 사용자 데이터 등
- 본 자료는 보안 및 리버싱 학습 목적의 참고용이며, Trakt 측과는 무관합니다.
문제 소지가 있을 경우 연락 주시면 즉시 조치하겠습니다.

This document summarizes the API endpoint information obtained through static analysis of the Trakt TV Android APK.
- Included information: HTTP methods, paths, filenames
- Excluded information: Authentication headers, actual API responses, tokens, user data, etc.
- This material is intended solely for educational purposes related to security and reverse engineering and is not affiliated with Trakt in any way.
If there are any concerns or issues, please contact us and we will take immediate action.
