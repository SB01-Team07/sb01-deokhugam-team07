# 📚 덕후감 - 도서 이미지 OCR 및 ISBN 매칭 서비스 (7조)

[![Codecov](https://codecov.io/gh/SB01-Team07/sb01-deokhugam-team07/branch/main/graph/badge.svg)](https://codecov.io/gh/SB01-Team07/sb01-deokhugam-team07)

> 덕후감은 책 표지나 페이지 이미지를 OCR로 인식하고   
> ISBN을 매칭하여 정확한 도서 정보를 제공하는 서비스입니다.

## 😀 팀원 구성
| 김경린                     | 박서연                | 이성근                                         | 이원길                     | 이용구                     |
|:-------------------------:|:--------------------:|:---------------------------------------------:|:-------------------------:|:-------------------------:|
| <img src="https://avatars.githubusercontent.com/u/133985654?v=4" width="130"> | <img src="https://avatars.githubusercontent.com/u/90109410?v=4" width="130"> | <img src="https://avatars.githubusercontent.com/u/61682044?v=4" width="130">| <img src="https://avatars.githubusercontent.com/u/139864668?v=4" width="130"> | <img src="https://avatars.githubusercontent.com/u/86422079?v=4" width="130"> |
| [k01zero](https://github.com/k01zero) | [yxoni](https://github.com/yxoni) | [LeeSG-0114](https://github.com/LeeSG-0114) | [realitsyourman](https://github.com/realitsyourman) | [reflash407](https://github.com/reflash407) |

## ✨ 주요 기능

- 도서 이미지 OCR 인식
- ISBN 자동 매칭 및 도서 정보 조회  
  → [Naver API](https://developers.naver.com/docs/serviceapi/search/book/book.md#%EC%B1%85)를 활용해 ISBN으로 책 정보를 자동으로 불러옵니다.
- ISBN 정보는 이미지 OCR을 통해 입력할 수 있습니다.
- 리뷰 작성 및 공유
- 책 덕후들과의 소통 커뮤니티

## 🖥 팀원별 구현 기능 상세

- 김경린
  - 대시보드
    - ![대시보드](https://github.com/user-attachments/assets/b8711f01-1af2-41d9-a0d8-ab1d0268b4f2)
    - 기간별 인기 도서, 인기 리뷰, 파워 유저
  - 알림
    - ![알림](https://github.com/user-attachments/assets/3ba32942-a866-4602-8aed-c9985694c051)
    - 작성한 리뷰에 댓글 또는 좋아요 받을시, 리뷰 10위 진출시 알림 생성
- 박서연
  - 도서 등록
    - Google Coud Vision API - OCR로 이미지에서 ISBN 13자리 추출
    - Naver API를 활용해 ISBN으로 책 정보 자동으로 업로드
      - ![ocr,isbn](https://github.com/user-attachments/assets/382612e5-6dba-4f68-8ff2-99cf91e4ff9b)
  - 도서 조회
    - 검색 및 상세 조회
      - ![검색, 상세조회](https://github.com/user-attachments/assets/76cff918-92e9-4b11-8b03-857a646d47ea)
    - 도서 제목으로 정렬 및 페이지네이션
      - ![제목정렬, 페이지네이션](https://github.com/user-attachments/assets/4654df28-109d-4c1c-88c5-03bd3e7ced91)
    - 도서 정렬 (출판일, 평점, 리뷰 수)
      - ![정렬](https://github.com/user-attachments/assets/294bbf5b-6774-4da7-ad0b-0f8edcd9145e)
  - 도서 수정
    - ![수정](https://github.com/user-attachments/assets/83561b04-502b-4a5b-9f7f-0aa90b552637)
  - 도서 삭제
    - ![삭제](https://github.com/user-attachments/assets/1992eb56-0ebd-4837-b3cb-7e2d1cb54a71)

- 이성근
  - 댓글 생성, 수정, 삭제
    - ![댓글 생성수정삭제 (1)](https://github.com/user-attachments/assets/7589350e-5070-4d3b-b64f-6bbd48cece3b)
    - 댓글을 생성, 수정, 삭제 할 수 있다.
  - 댓글 페이지네이션
    - ![댓글 페이지네이션 (1)](https://github.com/user-attachments/assets/2a5bd811-55a1-4107-96ce-a8a7fa839d94)
    - 댓글 목록을 페이지 단위로 조회 할 수 있다.
- 이원길
  - 유저 회원가입
    - ![회원가입](https://github.com/user-attachments/assets/561ec82f-c4fd-444a-bb1c-61ec0a4285bb)
    - 새로운 유저를 가입할 수 있다.
  - 로그인
    - ![로그인](https://github.com/user-attachments/assets/a16404c8-eb72-4558-9e64-4ac6f0cc85ec)
    - 가입한 유저의 정보로 로그인을 할 수 있다.
  - 닉네임 변경
    - ![닉네임 수정](https://github.com/user-attachments/assets/39ad1299-f361-47d9-9488-cb669b3852c8)
    - 유저의 닉네임을 변경할 수 있다.

- 이용구
  - 리뷰 등록, 조회, 수정, 삭제 </br>
    - ![리뷰 등록 조회 수정 삭제](https://github.com/user-attachments/assets/d78b33db-c236-4afb-beea-f5a638f8343c) </br></br>
  - 리뷰 목록 조회 </br>
    - ![리뷰 목록 조회](https://github.com/user-attachments/assets/16bc8935-f79f-4eb5-a39f-c4471006e7e7) </br>
    키워드 검색, 정렬조건(작성시간, 평점) 커서기반 페이지네이션 지원



## 🛠️ 기술 스택

- **Backend:** 
  - Spring Boot
  - Spring Data JPA
  - Spring Scheduler
  - Spring Security  
- **Database:** 
  - PostgreSQL  
- **Tool:** 
  - Git & Github
  - Discord
  - Notion 
 

## 📁 파일 구조

````
src
├─main
│  ├─java
│  │  └─com
│  │      └─part3
│  │          └─team07
│  │              └─sb01deokhugamteam07
│  │                  ├─batch
│  │                  │  ├─popularbook
│  │                  │  ├─popularreview
│  │                  │  └─poweruser
│  │                  ├─client
│  │                  │  └─dto
│  │                  ├─config
│  │                  ├─controller
│  │                  ├─converter
│  │                  ├─dto
│  │                  │  ├─book
│  │                  │  │  ├─request
│  │                  │  │  └─response
│  │                  │  ├─comment
│  │                  │  │  ├─request
│  │                  │  │  └─response
│  │                  │  ├─notification
│  │                  │  │  ├─request
│  │                  │  │  └─response
│  │                  │  ├─review
│  │                  │  │  ├─request
│  │                  │  │  └─response
│  │                  │  └─user
│  │                  │      ├─request
│  │                  │      └─response
│  │                  ├─entity
│  │                  │  └─base
│  │                  ├─exception
│  │                  │  ├─book
│  │                  │  ├─comment
│  │                  │  ├─notification
│  │                  │  ├─review
│  │                  │  ├─storage
│  │                  │  └─user
│  │                  ├─logging
│  │                  ├─mapper
│  │                  ├─repository
│  │                  ├─scheduler
│  │                  ├─security
│  │                  │  └─filter
│  │                  ├─service
│  │                  ├─storage
│  │                  ├─type
│  │                  ├─util
│  │                  └─validator
│  └─resources
│      └─static
│          ├─assets
│          ├─images
│          └─storage
└─test
    ├─java
    │  └─com
    │      └─part3
    │          └─team07
    │              └─sb01deokhugamteam07
    │                  ├─batch
    │                  │  ├─popularbook
    │                  │  ├─popularreview
    │                  │  └─poweruser
    │                  ├─client
    │                  ├─controller
    │                  ├─entity
    │                  ├─integration
    │                  ├─mapper
    │                  ├─repository
    │                  ├─service
    │                  └─storage
    └─resources
        └─static
            └─storage
                └─thumbnail


