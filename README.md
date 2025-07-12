# 💬 SIRU Chat

**SIRU Chat**은 1:1 및 그룹 채팅을 지원하는 실시간 채팅 서비스입니다. STOMP 기반의 WebSocket을 활용하며, 채팅 기능은 RabbitMQ와 MongoDB를 활용하였습니다.

---

## 🔧 주요 기능

- **1:1 채팅**
  - 상대방 닉네임 기반 자동 채팅방 이름
  - 퇴장 시 `active = false`, 재입장 시 `deactivatedAt` 이후 메시지만 조회
  - 상대방 메시지 수신 시 자동 재참여 처리

- **그룹 채팅**
  - 명시적 채팅방 이름으로 생성
  - 자유롭게 입장 및 퇴장 가능

- **메시지 처리**
  - STOMP를 통한 실시간 송수신
  - RabbitMQ로 메시지 브로드캐스팅
  - MongoDB에 메시지 저장 및 조회
  - Redis를 통한 채팅방 ID 생성 관리

- **채팅방 목록**
  - 현재 참여 중인 채팅방만 표시
  - 각 채팅방의 가장 최근 메시지 포함 (참여 시간 이후 기준)

---

## 📌 기술 스택

- Spring Boot
- WebSocket (STOMP)
- RabbitMQ
- MongoDB (채팅 정보)
- Redis
- MySQL (회원 정보)

---

## 🖼️ 구현 사항(스크린샷)
### 메인화면

<img width="1306" height="950" alt="image" src="https://github.com/user-attachments/assets/c7a81bcb-28e1-45df-9ba7-1880e1b359d6" />

### 그룹채팅 및 사용자 찾기

<img width="1141" height="586" alt="image" src="https://github.com/user-attachments/assets/8dea154d-1ae9-4f03-a673-5fdd46ab3212" />

<img width="1141" height="567" alt="image" src="https://github.com/user-attachments/assets/a6e9b5d8-97eb-437b-ab18-bf317bcbe9a2" />

### 그룹 채팅방 만들기

<img width="1262" height="886" alt="image" src="https://github.com/user-attachments/assets/1f85b4f5-a453-41f2-8809-33c74b232b23" />

### 그룹 채팅방

<img width="1560" height="1242" alt="image" src="https://github.com/user-attachments/assets/47dbbd67-a623-49dd-819a-8ebc02f6a62b" />

### 추가된 그룹 채팅방과 참여자 리스트

<img width="1178" height="752" alt="image" src="https://github.com/user-attachments/assets/bb077a28-e4d2-4fbc-ae4f-45fedc49f755" />

<img width="1932" height="1031" alt="image" src="https://github.com/user-attachments/assets/39ff6015-11d0-4c27-adc5-5edec009f7d7" />

### 입장 이후 메시지만 로딩

<img width="1624" height="1309" alt="image" src="https://github.com/user-attachments/assets/a3dd3226-12e6-4d98-8c37-003726ab5f8a" />

### 1:1 채팅방

<img width="1647" height="1328" alt="image" src="https://github.com/user-attachments/assets/b87fdc05-c3b5-4356-82ae-009656b2c772" />

### SSE를 이용한 알림 및 채팅방 내역의 최근 메시지 업데이트

<img width="2149" height="1287" alt="image" src="https://github.com/user-attachments/assets/d06bfb70-04c6-4a75-acd8-8bc90bddf98a" />
