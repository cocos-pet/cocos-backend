# 반려인의 간절함이 모이는 공간, 코코스
![Image](https://github.com/user-attachments/assets/4139a727-8172-4f00-b637-7273970ccc84)
🔗 [https://cocos-pet.kr](https://cocos-pet.kr) — **운영 중**
> 코코스는 반려인을 위한 커뮤니티 기반 동물병원 리뷰 플랫폼입니다.
아픈 반려동물의 증상 고민을 나누고, 실제 병원 후기 기반으로 믿을 수 있는 병원을 함께 찾는 공간입니다.

---

## 🐶 Main Feature
1. 반려동물 증상 고민과 병원 후기를 나눌 수 있는 **커뮤니티**
2. 증상/질병 중심 **병원 리뷰**
3. 커뮤니티와 병원 리뷰 연결로 탐색 플로우 단순화

---
## 🐱 Authors


|            | 김의진 [@sansan20535](https://github.com/sansan20535)                                                                                                                                                                                                                                                                                                                                                                           | 서연진 [@seoyeonjin](https://github.com/seoyeonjin)                                                                                                                                                                                                                                                                                                                                                                        |
|:-----------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|            | <img src="https://github.com/user-attachments/assets/676a25b6-beb2-49db-8a3a-2a1892003133" width="300"/>                                                                                                                                                                                                                                                                                                                     | <img src="https://github.com/user-attachments/assets/a4f57999-a7a1-4f20-8ec8-4b57dc8c6306" width="300"/>                                                                                                                                                                                                                                                                                                                |
| **담당 API** | **회원 관련:** 로그인/회원가입 API, 로그아웃 API, 토큰 재발급 API  <br><br> **병원 관련:** 병원 상세 조회 API, 병원 리뷰 요약 조회 API, 병원 이용 목적 조회 API, 병원 리뷰 삭제 API, 병원 리뷰 요약 옵션 리스트 조회 API, 병원 리뷰 작성 API, 후기 작성 약관 동의 API, 지역 조회 API  <br><br> **반려동물 관련:** 동물 종류 조회 API, 품종 조회 API, 신체 부위 조회 API, 질병 조회 API, 증상 조회 API  <br><br> **커뮤니티 관련:** 인기글 조회 API, 게시글 리스트 조회 API, 게시글 상세 조회 API, 게시글 작성 API, 게시글 삭제 API, 게시글 카테고리 조회 API, 게시글 공감 추가 API, 게시글 공감 삭제 API | **회원 관련:** 사용자 조회 API, 회원정보 수정 API, 회원 탈퇴 API, 사용자 위치 조회 API, 사용자 게시글 리스트 조회 API, 사용자 댓글 조회 API, 검색어 저장 API, 닉네임 중복 여부 확인 API  <br><br> **병원 관련:** 병원 리스트 조회 API, 병원 최근 검색어 조회 API, 병원 리뷰 리스트 조회 API, 사용자 작성 리뷰 조회 API, 즐겨찾는 병원 조회 API, 즐겨찾는 병원 수정 API, 병원 검색어 저장 API  <br><br> **반려동물 관련:** 반려 동물 생성 API, 반려 동물 수정 API, 반려 동물 정보 조회 API  <br><br> **커뮤니티 관련:** 댓글 조회 API, 댓글 작성 API, 댓글 삭제 API, 대댓글 작성 API, 대댓글 삭제 API |
| **주요 작업**  | <ul><li>카카오 로그인 인증 구현</li><li>HTTPS 설정 및 CORS 설정</li><li>S3 Presigned Url 로직 구현</li><li>에러 추적을 위한 Sentry 설정</li></ul>                                                                                                                                                                                                                                                                                                        | <ul><li>블루그린 배포 자동화 스크립트 작성</li><li>Cursor 기반 페이지네이션 적용</li><li>API 공통 응답 형식 및 Global Exception 세팅</li></ul>                                                                                                                                                                                                                                                                                                            |

---
## 🐭 Architecture
- GitHub Actions를 활용한 CI/CD 파이프라인이 구성되어 있어, `main`, `dev` 브랜치 머지 시 자동으로 Docker 이미지 빌드 및 서버 반영이 이루어집니다. 
- 무중단 배포를 위해 블루그린 배포 전략을 적용하여, 새 버전 배포 시에도 사용자에게 영향 없이 서비스가 전환됩니다.
<br><br>
![Image](https://github.com/user-attachments/assets/2509893e-6e3a-40df-9e35-74e39efcbe98)

---
## 🐹 ERD
- 데이터 모델링 시 정규화를 통해 반복 필드를 제거하고, N:M 관계는 조인 테이블을 통해 명확히 분리하였습니다. 
- 총 34개 테이블로 구성되어 있으며, 병원 후기, 사용자 정보, 반려동물, 커뮤니티 게시글 및 댓글 간의 관계를 안정적으로 처리할 수 있습니다.
 <br><br>
![Image](https://github.com/user-attachments/assets/633af5f4-8e2c-45a6-94ea-40d58a970799)

---
## 🐰 Git convention
[COCOS Git Convention](https://oceanic-pixie-c2c.notion.site/Git-Convention-1a4c12bc853380d1b95acaa7aafa16b2?source=copy_link)

---
## 🦊 Code convention
[COCOS Code Convention](https://oceanic-pixie-c2c.notion.site/Code-Convention-1a4c12bc85338050b460fd14fab6931c?source=copy_link)

---
## 🐻 Stack

| Stack                     | Description |
|---------------------------|-------------|
| Java 21                   | Language    |
| Spring Boot 3.4.1, Gradle | Framework   |
| Spring Data JPA           | ORM         |
| MySQL                     | DB          |
| JWT                       | Auth        |
| AWS EC2 / RDS / Nginx     | Infra       |
| GitHub Actions            | CI/CD       |

---

## 🐼 Folder Structure
```shell
📦 src
 ┣ 📂 api              # 컨트롤러, 요청/응답 DTO, 서비스 모듈
 ┣ 📂 db               # 엔티티, 리포지토리, JPA 관련
 ┣ 📂 common           # 예외, 공통 응답
 ┣ 📂 config           # CORS, 시큐리티, Swagger 설정 등
 ┣ 📂 auth             # JWT, 필터, 인증 관련
 ┣ 📂 enums            # Enum 및 메시지 정의
 ┣ 📂 util             # 유틸 및 컨버터
 ┣ 📂 validation       # 커스텀 Validator
 ┣ 📂 external         # 외부 API(S3, 소셜 로그인) 클라이언트
 ┗ 📄 CocosApplication.java
```
