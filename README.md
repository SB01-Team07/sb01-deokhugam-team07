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

## 팀원별 구현 기능 상세

- 김경린
  - 대시보드
  ![대시보드](https://github.com/user-attachments/assets/bda02126-f8bc-4c7b-b55b-598a846feb1f)
  기간별 인기 도서, 인기 리뷰, 파워 유저
  - 알림
  ![알림](https://github.com/user-attachments/assets/3bb1a2a0-6412-4d49-beb3-0d63114c1d56)
  작성한 리뷰에 댓글 또는 좋아요 받을시, 리뷰 10위 진출시 알림 생성
- 박서연
- 이성근
- 이원길
- 이용구


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


