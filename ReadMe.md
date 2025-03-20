
## 주요 기능

### 1. 사용자 인증

- **회원가입**: `POST` `/api/auth/signup`
- **로그인**: `POST` `/api/auth/login`

### 2. 작품 관리

- **작품 목록 조회**: `GET` `/api/works`
- **작품 상세 조회**: `GET` `/api/works/{workId}`
- **인기 작품 조회**: `GET` `/api/works/popular`
- **인기 구매 작품 조회**: `GET` `/api/works/popular-purchases`
- **작품 등록**: `POST` `/api/works`
- **작품 수정**: `PUT` `/api/works/{workId}`
- **작품 삭제**: `DELETE` `/api/works/{workId}`

### 3. 조회 이력 관리

- **사용자 조회 이력 목록**: `GET` `/api/users/{userId}/view-history`
- **특정 조회 이력 삭제**: `DELETE` `/api/users/{userId}/view-history/{historyId}`
- **모든 조회 이력 삭제**: `DELETE` `/api/users/{userId}/view-history`

### 4. 구매 관리

- **사용자 구매 목록 조회**: `GET` `/api/users/{userId}/purchases`
- **작품 구매**: `POST` `/api/users/{userId}/purchases`
- **특정 구매 내역 조회**: `GET` `/api/users/{userId}/purchases/{purchaseId}`

## 기술 스택

- Java 21
- Spring Boot 3.4.3
- Spring Data JPA
- Spring Security
- JWT 인증
- H2 Database (개발용)
- Redis (캐싱)
- Docker & Docker Compose

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
  - 비밀번호: (비워두기)

## API 인증

API는 JWT 토큰 기반 인증을 사용합니다.

### 인증 흐름

1. **회원가입**:
   ```
   POST` `/api/auth/signup
   {
     "username": "user1",
     "password": "password123",
     "email": "user1@example.com",
     "nickname": "사용자1"
   }
   ```

2. **로그인 및 토큰 발급**:
   ```
   POST` `/api/auth/login
   {
     "username": "user1",
     "password": "password123"
   }
   ```

   응답:
   ```
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "type": "Bearer"
   }
   ```

3. **토큰 사용**: 모든 API 요청의 Authorization 헤더에 `Bearer {token}` 형식으로 토큰 포함

## 데이터 모델

### 주요 엔티티

- **User**: 사용자 정보
- **Work**: 만화 작품 정보
- **Episode**: 작품의 각 에피소드
- **History**: 작품 조회 이력
- **Purchase**: 작품 구매 내역
- **Review**: 작품 리뷰
- **Creator**: 작가 정보

## 예시 요청

### 인기 작품 조회

```bash
curl -X GET` `http://localhost:8080/api/works/popular
```

### 작품 구매

```bash
curl -X POST` `http://localhost:8080/api/users/1/purchases \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "workId": 1,
    "type": "POINT"
  }'
```

## 종료 방법

```bash
docker-compose down
```