
## 주요 기능

### 1. 인증 API

<details>
<summary>회원가입 [POST, /api/auth/signup]</summary>

- URL: /api/auth/signup
- Method: POST
- 요청데이터
    ```json
    {
      "username": "user1",
      "password": "password123",
      "email": "user1@example.com",
      "nickname": "사용자1"
    }
    ```
- 응답
  - 성공: 200 OK
- 오류
  - 409 CONFLICT: 이미 사용 중인 사용자명 또는 이메일
  - 400 BAD_REQUEST: 유효하지 않은 입력 값
</details>

<details>
<summary>로그인 [POST, /api/auth/login]</summary>

- URL: /api/auth/login
- Method: POST
- 요청데이터
    ```json
    {
      "username": "user1",
      "password": "password123"
    }
    ```
- 응답
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "type": "Bearer"
    }
    ```

- 오류
  - 401 UNAUTHORIZED: 잘못된 사용자명 또는 비밀번호
  - 403 FORBIDDEN: 비활성화된 계정
</details>


### 2. 사용자 API
<details>
<summary>사용자 목록 조회 (관리자 전용) [GET, /api/users]</summary>

- URL: /api/users
- Method: GET
- 인증: 필수 (ADMIN 권한)
- 파라미터
  - page: 페이지 번호 (기본값: 0)
  - size: 페이지 크기 (기본값: 20)
  - sort: 정렬 방식 (기본값: createdAt,desc)
- 응답: 사용자 목록 (페이징)
</details>

<details>
<summary>특정 사용자 정보 조회 [GET, /api/users/{userId}]</summary>

- URL: /api/users/{userId}
- Method: GET
- 인증: 필수 (본인 또는 ADMIN 권한)
- 응답: 사용자 정보
</details>

<details>
<summary>현재 사용자 정보 조회 [GET, /api/users/me]</summary>

- URL: /api/users/me
- Method: GET
- 인증: 필수
- 응답: 현재 로그인한 사용자 정보
</details>

<details>
<summary>사용자 정보 수정 [PUT, /api/users/{userId}]</summary>

- URL: /api/users/{userId}
- Method: PUT
- 인증: 필수 (본인 또는 ADMIN 권한)
- 요청데이터
    ```json
    {
        "email": "newemail@example.com",
        "password": "newpassword",
        "nickname": "새닉네임"
    }
    ```
- 응답: 수정된 사용자 정보
</details>

<details>
<summary>포인트 충전 [POST, /api/users/{userId}/points]</summary>

- URL: /api/users/{userId}/points
- Method: POST
- 인증: 필수 (본인 또는 ADMIN 권한)
- 요청데이터
    ```json
    {
        "amount": 10000
    }
    ```
- 응답: 충전 후 사용자 정보
</details>

<details>
<summary>사용자 권한 추가 (관리자 전용) [POST, /api/users/{userId}/roles/{role}]</summary>

- URL: /api/users/{userId}/roles/{role}
- Method: POST
- 인증: 필수 (ADMIN 권한)
- 응답: 권한 추가 후 사용자 정보
</details>

<details>
<summary>사용자 권한 삭제 (관리자 전용) [DELETE, /api/users/{userId}/roles/{role}]</summary>

- URL: /api/users/{userId}/roles/{role}
- Method: DELETE
- 인증: 필수 (ADMIN 권한)
- 응답: 권한 삭제 후 사용자 정보
</details>

<details>
<summary>사용자 활성화/비활성화 (관리자 전용) [PATCH, /api/users/{userId}/status]</summary>

- URL: /api/users/{userId}/status
- Method: PATCH
- 파라미터:
  - enable: 활성화 여부 (true/false)
- 응답: 상태 변경 후 사용자 정보
</details>

<details>
<summary>사용자 검색 (관리자 전용) [GET, /api/users/search]</summary>

- URL: /api/users/search
- Method: GET
- 인증: 필수 (ADMIN 권한)
- 파라미터:
  - keyword: 검색 키워드
  - page: 페이지 번호 (기본값: 0)
  - size: 페이지 크기 (기본값: 20)
- 응답: 검색 결과 사용자 목록 (페이징)
</details>


<details>
<summary>특정 권한 사용자 목록 조회 (관리자 전용) [GET, /api/users/roles/{role}]</summary>

- URL: /api/users/roles/{role}
- Method: GET
- 인증: 필수 (ADMIN 권한)
- 파라미터:
  - page: 페이지 번호 (기본값: 0)
  - size: 페이지 크기 (기본값:20)
- 응답: 권한별 사용자 목록 (페이징)
</details>


### 3. 작품 API

<details>
<summary>작품 목록 조회 [GET, /api/works]</summary>

- URL: /api/works
- Method: GET
- 인증: 옵션
- 파라미터:
  - page: 페이지 번호 (기본값: 0)
  - size: 페이지 크기 (기본값: 20)
  - sort: 정렬 방식 (기본값: createdAt,desc)
- 응답: 작품 목록 (페이징)
</details>

<details>
<summary>특정 작품 조회 [GET, /api/works/{workId}]</summary>

- URL: /api/works/{workId}
- Method: GET
- 인증: 옵션 (인증 시 조회 이력 저장)
- 응답: 작품 상세 정보
</details>

<details>
<summary>인기 작품 목록 조회 [GET, /api/works/popular]</summary>

- URL: /api/works/popular
- Method: GET
- 인증: 옵션
- 응답: 인기 작품 목록 (조회수 기준)
</details>

<details>
<summary>인기 구매 작품 목록 조회 [GET, /api/works/popular-purchases]</summary>

- URL: /api/works/popular-purchases
- Method: GET
- 인증: 옵션
- 응답: 인기 구매 작품 목록 (구매수 기준)
</details>

<details>
<summary>작품 등록 [POST, /api/works]</summary>

- URL: /api/works
- Method: POST
- 인증: 필수 (ADMIN 또는 CREATOR 권한)
- 요청데이터
    ```json
    {
        "title": "작품제목",
        "author": "작가명",
        "description": "작품 설명",
        "price": 0,
        "type": "WEBTOON",
        "thumbnailUrl": "https://example.com/thumbnail.jpg"
    }
    ```
- 응답: 등록된 작품 정보
</details>

<details>
<summary>작품 수정 [PUT, /api/works/{workId}]</summary>

- URL: /api/works/{workId}
- Method: PUT
- 인증: 필수 (ADMIN 또는 CREATOR 권한)
- 요청데이터
    ```json
    {
        "title": "수정된 제목",
        "author": "작가명",
        "description": "수정된 설명",
        "price": 1000,
        "type": "WEBTOON",
        "thumbnailUrl": "https://example.com/new-thumbnail.jpg"
    }
    ```
- 응답: 수정된 작품 정보
</details>

<details>
<summary>작품 삭제 [DELETE, /api/works/{workId}]</summary>

- URL: /api/works/{workId}
- Method: DELETE
- 인증: 필수 (ADMIN 권한)
- 응답: 204 No Content
</details>

### 4. 에피소드 API

<details>
<summary>작품별 에피소드 목록 조회 [GET, /api/works/{workId}/episodes]</summary>

- URL: /api/works/{workId}/episodes
- Method: GET
- 인증: 옵션
- 파라미터
  - page: 페이지 번호 (기본값: 0)
  - size: 페이지 크기 (기본값: 20)
  - sort: 정렬 방식 (기본값: episodeNumber,asc)
- 응답: 에피소드 목록 (페이징)
</details>

<details>
<summary>특정 에피소드 조회 [GET, /api/works/{workId}/episodes/{episodeId}]</summary>

- URL: /api/works/{workId}/episodes/{episodeId}
- Method: GET
- 인증: 옵션
- 응답: 에피소드 상세 정보 (조회 시 조회수 증가)
</details>

<details>
<summary>에피소드 번호로 조회 [GET, /api/works/{workId}/episodes/number/{episodeNumber}]</summary>

- URL: /api/works/{workId}/episodes/number/{episodeNumber}
- Method: GET
- 인증: 옵션
- 응답: 에피소드 상세 정보 (조회 시 조회수 증가)
</details>

<details>
<summary>무료 에피소드 목록 조회 [GET, /api/works/{workId}/episodes/free]</summary>

- URL: /api/works/{workId}/episodes/free
- Method: GET
- 인증: 옵션
- 응답: 무료 에피소드 목록
</details>

<details>
<summary>인기 에피소드 목록 조회 [GET, /api/works/{workId}/episodes/popular]</summary>

- URL: /api/works/{workId}/episodes/popular
- Method: GET
- 인증: 옵션
- 파라미터
  - limit: 조회 개수 (기본값: 5)
- 응답: 인기 에피소드 목록 (조회수 기준)
</details>

<details>
<summary>에피소드 등록 [POST, /api/works/{workId}/episodes]</summary>

- URL: /api/works/{workId}/episodes
- Method: POST
- 인증: 필수 (ADMIN 또는 CREATOR 권한)
- 요청 데이터
    ```json
    {
        "workId": 1,
        "title": "에피소드 제목",
        "episodeNumber": 1,
        "content": "에피소드 내용",
        "price": 100,
        "isFree": false
    }
    ```
- 응답: 등록된 에피소드 정보
</details>

<details>
<summary>에피소드 수정 [PUT, /api/works/{workId}/episodes/{episodeId}]</summary>

- URL: /api/works/{workId}/episodes/{episodeId}
- Method: PUT
- 인증: 필수 (ADMIN 또는 CREATOR 권한)
- 요청 데이터
    ```json
    {
        "title": "수정된 제목",
        "content": "수정된 내용",
        "price": 200,
        "isFree": true
    }
    ```
- 응답: 수정된 에피소드 정보
</details>

<details>
<summary>에피소드 삭제 [DELETE, /api/works/{workId}/episodes/{episodeId}]</summary>

- URL: /api/works/{workId}/episodes/{episodeId}
- Method: DELETE
- 인증: 필수 (ADMIN 또는 CREATOR 권한)
- 응답: 204 No Content
</details>

### 5. 조회이력 API

<details>
<summary>사용자 조회 이력 목록 조회 [GET, /api/users/{userId}/view-history]</summary>

- URL: /api/users/{userId}/view-history
- Method: GET
- 인증: 필수 (본인 또는 ADMIN 권한)
- 파라미터
  - page: 페이지 번호 (기본값: 0)
  - size: 페이지 크기 (기본값: 20)
  - sort: 정렬 방식 (기본값: viewedAt,desc)
- 응답: 조회 이력 목록 (페이징)
</details>

<details>
<summary>특정 조회 이력 삭제 [DELETE, /api/users/{userId}/view-history/{historyId}]</summary>

- URL: /api/users/{userId}/view-history/{historyId}
- Method: DELETE
- 인증: 필수 (본인 또는 ADMIN 권한)
- 응답: 204 No Content
</details>

<details>
<summary>모든 조회 이력 삭제 [DELETE, /api/users/{userId}/view-history]</summary>

- URL: /api/users/{userId}/view-history
- Method: DELETE
- 인증: 필수 (본인 또는 ADMIN 권한)
- 응답: 204 No Content
</details>

### 5. 구매 API

<details>
<summary>사용자 구매 내역 목록 조회 [GET, /api/users/{userId}/purchases]</summary>

- URL: /api/users/{userId}/purchases
- Method: GET
- 인증: 필수 (본인 또는 ADMIN 권한)
- 파라미터
  - page: 페이지 번호 (기본값: 0)
  - size: 페이지 크기 (기본값: 20)
  - sort: 정렬 방식 (기본값: purchasedAt,desc)
- 응답: 구매 내역 목록 (페이징)
</details>

<details>
<summary>특정 구매 내역 조회 [GET, /api/users/{userId}/purchases/{purchaseId}]</summary>

- URL: /api/users/{userId}/purchases/{purchaseId}
- Method: GET
- 인증: 필수 (본인 또는 ADMIN 권한)
- 응답: 구매 내역 상세 정보
</details>

<details>
<summary>작품 구매 [POST, /api/users/{userId}/purchases]</summary>

- URL: /api/users/{userId}/purchases
- Method: POST
- 인증: 필수 (본인 또는 ADMIN 권한)
- 요청 데이터
    ```json
    {
        "workId": 1,
        "type": "POINT" // 구매 유형: FREE, POINT, CASH
    }
    ```
- 응답: 구매 내역 정보
- 오류
  - 409 CONFLICT: 이미 구매한 작품
  - 400 BAD_REQUEST: 포인트 부족 또는 무료 작품이 아닌데 FREE 타입으로 구매 시도
</details>

### 상태 코드
<details>
<summary>API는 다음과 같은 HTTP 상태 코드를 사용합니다.</summary>
    

| 상태 코드 | 설명                                                                 |
|-----------|----------------------------------------------------------------------|
| 200 OK    | 요청이 성공적으로 처리됨                                             |
| 201 Created | 리소스가 성공적으로 생성됨                                          |
| 204 No Content | 요청이 성공했으나 반환할 콘텐츠가 없음                          |
| 400 Bad Request | 잘못된 요청 (요청 형식 오류, 유효성 검사 실패 등)             |
| 401 Unauthorized | 인증 필요 (인증 정보 없음 또는 유효하지 않은 토큰)           |
| 403 Forbidden | 접근 권한 없음                                                   |
| 404 Not Found | 요청한 리소스를 찾을 수 없음                                     |
| 409 Conflict | 리소스 충돌 (중복된 사용자명, 이미 구매한 작품 등)               |
| 422 Unprocessable Entity | 요청 형식은 올바르지만 의미적으로 처리할 수 없음     |
| 429 Too Many Requests | 요청 제한 초과                                           |
| 500 Internal Server Error | 서버 내부 오류                                       |
</details>

### 오류 응답

- 모든 API는 오류 발생 시 아래 양식으로 응답합니다.
    ```json
    {
      "timestamp": "2025-03-23T14:30:45",
      "status": 400,
      "error": "BAD_REQUEST",
      "code": "C001",
      "message": "잘못된 입력값입니다",
      "path": "/api/works/invalid",
      "fieldErrors": [
        {
          "field": "title",
          "message": "제목은 필수입니다"
        }
      ]
    }
    ```

<details>
<summary>주요 에러 코드</summary>

  | 코드 | 설명                     |
  |------|--------------------------|
  | C001 | 잘못된 입력값            |
  | C003 | 요청한 리소스를 찾을 수 없음 |
  | C006 | 접근 권한 없음           |
  | U001 | 존재하지 않는 사용자     |
  | U002 | 이미 사용 중인 사용자명  |
  | U003 | 이미 사용 중인 이메일    |
  | U004 | 유효하지 않은 비밀번호   |
  | A001 | 인증 필요                |
  | A002 | 유효하지 않은 토큰       |
  | A003 | 만료된 토큰              |
  | W001 | 존재하지 않는 작품       |
  | W002 | 존재하지 않는 에피소드   |
  | P001 | 이미 구매한 작품         |
  | P002 | 포인트 부족              |
</details>

* * *

## 기술 스택

- Java 21
- Spring Boot 3.4.3
- Spring Data JPA
- Spring Security
- JWT 인증
- H2 Database (개발용)
- Redis (캐싱)
- Docker & Docker Compose

* * *

## DB 스키마
<details>
<summary>사용자 테이블 (users)</summary>

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(50),
    point INT NOT NULL DEFAULT 0,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL
);
```
</details>

<details>
<summary>사용자 권한 테이블 (user_roles)</summary>

```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```
</details>


<details>
<summary>작품 테이블 (works)</summary>

```sql
CREATE TABLE works (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   title VARCHAR(100) NOT NULL,
   author VARCHAR(50) NOT NULL,
   description TEXT,
   price DECIMAL(10, 2) NOT NULL,
   type VARCHAR(20) NOT NULL,
   thumbnail_url VARCHAR(255),
   view_count INT NOT NULL DEFAULT 0,
   purchase_count INT NOT NULL DEFAULT 0,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   updated_by VARCHAR(50) NOT NULL
);
```
</details>


<details>
<summary>에피소드 테이블 (episodes)</summary>

```sql
CREATE TABLE episodes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  episode_number INT NOT NULL,
  content TEXT,
  price DECIMAL(10, 2) NOT NULL,
  is_free BOOLEAN NOT NULL DEFAULT FALSE,
  view_count INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  created_by VARCHAR(50) NOT NULL,
  updated_by VARCHAR(50) NOT NULL,
  FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE,
  UNIQUE KEY (work_id, episode_number)
);
```
</details>


<details>
<summary>조회 이력 테이블 (histories)</summary>

```sql
CREATE TABLE histories (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   user_id BIGINT NOT NULL,
   work_id BIGINT NOT NULL,
   viewed_at TIMESTAMP NOT NULL,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   updated_by VARCHAR(50) NOT NULL,
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
   FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE
);
```
</details>


<details>
<summary>구매 내역 테이블 (purchases)</summary>

```sql
CREATE TABLE purchases (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   user_id BIGINT NOT NULL,
   work_id BIGINT NOT NULL,
   price DECIMAL(10, 2) NOT NULL,
   type VARCHAR(20) NOT NULL,
   purchased_at TIMESTAMP NOT NULL,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NOT NULL,
   created_by VARCHAR(50) NOT NULL,
   updated_by VARCHAR(50) NOT NULL,
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
   FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE
);
```
</details>

* * *

## 설치 및 실행 방법

### 전제 조건

- Docker 및 Docker Compose 설치
- JDK 21 설치 (로컬 빌드 시 필요)

### 실행 방법

#### Linux/macOS

```bash
./run.sh
```

#### Windows

```
run.bat
```

### 접속 정보

- **API 서버**: http://localhost:8080
- **H2 콘솔**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:lezhindb`
  - 사용자명: `sa`
  - 비밀번호: 없음

## 종료 방법

```bash
docker-compose down
```