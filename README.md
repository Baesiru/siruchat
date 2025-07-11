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

<img width="1563" height="734" alt="image" src="https://github.com/user-attachments/assets/1378e58d-4617-4b0e-af38-7a9c6e247b96" />
<img width="1602" height="766" alt="image" src="https://github.com/user-attachments/assets/19b15571-75f1-46c6-8b37-4942af544051" />
<img width="1577" height="738" alt="image" src="https://github.com/user-attachments/assets/6185d09c-f881-4cb4-b96d-fb67910a23fd" />

