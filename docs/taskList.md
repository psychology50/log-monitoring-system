# 실시간 로그 모니터링 시스템 - 점진적 학습 TaskList

## 🎓 학습 중심 접근법

**핵심 철학**: 복잡도를 점진적으로 증가시키며 각 기술을 확실히 학습  
**목표 기술**: Kafka → OpenSearch → Vue.js → 분산 시스템

---

## 🚀 Phase 1: Kafka 기본 마스터 (1주)
> **목표**: Kafka Producer/Consumer 패턴 완전히 이해하기

### 1.1 최소한의 Kafka 환경 (2시간)
**인프라**: Kafka 단일 브로커만!

```yaml
# docker-compose.simple.yml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

- [ ] **1.1.1** 단일 Kafka 브로커 실행 확인
- [ ] **1.1.2** 토픽 생성 (`app-logs` 하나만)
- [ ] **1.1.3** CLI로 메시지 송수신 테스트

### 1.2 첫 번째 Spring Boot Producer (4시간)
**애플리케이션**: 가장 간단한 Web App

```java
// 정말 간단한 Controller
@RestController
public class SimpleController {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @GetMapping("/test")
    public String test() {
        kafkaTemplate.send("app-logs", "Hello Kafka!");
        return "Message sent!";
    }
}
```

- [ ] **1.2.1** Spring Boot + Kafka 기본 설정
- [ ] **1.2.2** 간단한 메시지 전송 구현
- [ ] **1.2.3** 전송 성공/실패 확인

### 1.3 첫 번째 Consumer (4시간)
**목표**: Kafka 메시지를 받아서 콘솔에 출력

```java
@Component
public class SimpleLogConsumer {
    
    @KafkaListener(topics = "app-logs")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
```

- [ ] **1.3.1** 별도 Spring Boot Consumer 애플리케이션 생성
- [ ] **1.3.2** 메시지 수신 확인
- [ ] **1.3.3** Consumer Group 개념 실습

### 1.4 구조화된 로그 (4시간)
**목표**: JSON 형태의 구조화된 로그 전송

```java
public class LogEntry {
    private String timestamp;
    private String level;
    private String service;
    private String message;
    // getters/setters
}
```

- [ ] **1.4.1** LogEntry DTO 생성
- [ ] **1.4.2** JSON 직렬화/역직렬화 설정
- [ ] **1.4.3** 구조화된 로그 전송/수신 확인

**🎯 Phase 1 완료 기준**: Web App → Kafka → Consumer로 JSON 로그가 정상적으로 흐름

---

## 🔍 Phase 2: OpenSearch 연동 (1주)
> **목표**: Kafka에서 받은 로그를 OpenSearch에 저장하고 검색하기

### 2.1 OpenSearch 기본 환경 (2시간)
**추가 인프라**: OpenSearch 단일 노드만 추가

```yaml
# docker-compose.simple.yml에 추가
  opensearch:
    image: opensearchproject/opensearch:2.8.0
    ports:
      - "9200:9200"
    environment:
      - discovery.type=single-node
      - "DISABLE_SECURITY_PLUGIN=true"
```

- [ ] **2.1.1** OpenSearch 실행 및 접속 확인
- [ ] **2.1.2** 기본 인덱스 생성 및 문서 삽입 실습
- [ ] **2.1.3** 간단한 검색 쿼리 실습

### 2.2 Consumer → OpenSearch 연동 (6시간)
**목표**: Kafka 메시지를 OpenSearch에 저장

```java
@Component
public class LogProcessor {
    
    @Autowired
    private OpenSearchClient client;
    
    @KafkaListener(topics = "app-logs")
    public void processLog(String logJson) {
        // JSON 파싱
        LogEntry log = parseLog(logJson);
        
        // OpenSearch에 저장
        indexLog(log);
    }
}
```

- [ ] **2.2.1** OpenSearch Java 클라이언트 설정
- [ ] **2.2.2** 로그 인덱싱 로직 구현
- [ ] **2.2.3** 인덱스 템플릿 설정
- [ ] **2.2.4** 데이터 저장 확인

### 2.3 기본 검색 API (6시간)
**목표**: 저장된 로그를 검색할 수 있는 REST API

```java
@RestController
public class LogSearchController {
    
    @GetMapping("/api/logs/search")
    public List<LogEntry> searchLogs(@RequestParam String query) {
        // OpenSearch 검색 쿼리 실행
        return searchResults;
    }
}
```

- [ ] **2.3.1** 간단한 검색 API 구현
- [ ] **2.3.2** 키워드 검색 기능
- [ ] **2.3.3** 날짜 범위 검색 기능
- [ ] **2.3.4** Postman으로 API 테스트

**🎯 Phase 2 완료 기준**: Web App → Kafka → OpenSearch → REST API 검색 가능

---

## 🖥️ Phase 3: Vue.js 대시보드 (1-2주)
> **목표**: 실시간 로그 모니터링 대시보드 구축

### 3.1 Vue.js 기본 설정 (4시간)
**목표**: Vue.js 프로젝트 생성 및 API 연동

```bash
npm create vue@latest log-dashboard
cd log-dashboard
npm install
```

- [ ] **3.1.1** Vue.js 3 프로젝트 생성
- [ ] **3.1.2** Axios를 이용한 API 호출 설정
- [ ] **3.1.3** 기본 라우팅 설정

### 3.2 로그 검색 화면 (8시간)
**목표**: 검색 폼과 결과 테이블

```vue
<template>
  <div>
    <SearchForm @search="handleSearch" />
    <LogTable :logs="searchResults" />
  </div>
</template>
```

- [ ] **3.2.1** 검색 폼 컴포넌트
- [ ] **3.2.2** 검색 결과 테이블 컴포넌트
- [ ] **3.2.3** 검색 상태 관리 (Pinia)
- [ ] **3.2.4** 페이지네이션 구현

### 3.3 실시간 로그 스트림 (8시간)
**목표**: WebSocket으로 실시간 로그 표시

```java
// Spring Boot WebSocket 설정
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new LogStreamHandler(), "/logs/stream");
    }
}
```

- [ ] **3.3.1** Spring Boot WebSocket 서버 구현
- [ ] **3.3.2** Kafka Consumer에서 WebSocket으로 브로드캐스트
- [ ] **3.3.3** Vue.js WebSocket 클라이언트
- [ ] **3.3.4** 실시간 로그 스트림 화면

### 3.4 기본 대시보드 (6시간)
**목표**: 차트와 통계 표시

- [ ] **3.4.1** Chart.js 설치 및 설정
- [ ] **3.4.2** 로그 레벨별 파이 차트
- [ ] **3.4.3** 시간대별 로그 수 차트
- [ ] **3.4.4** 서비스별 통계

**🎯 Phase 3 완료 기준**: 실시간 로그 스트림 + 검색 + 기본 차트가 모두 동작

---

## ⚡ Phase 4: 성능 및 확장성 (1-2주)
> **목표**: 실제 부하에서의 병목 지점 발견 및 해결

### 4.1 부하 테스트 (4시간)
**목표**: 현재 시스템의 한계 파악

- [ ] **4.1.1** 간단한 부하 생성 스크립트 작성
```java
// 로그 대량 생성기
@Component
public class LogLoadGenerator {
    public void generateLogs(int logsPerSecond) {
        // 설정한 TPS로 로그 생성
    }
}
```
- [ ] **4.1.2** 처리량 측정 (메시지/초)
- [ ] **4.1.3** 병목 지점 파악 (CPU, 메모리, 디스크)

### 4.2 첫 번째 최적화 (6시간)
**목표**: 발견된 병목 해결

- [ ] **4.2.1** Kafka Producer 배치 설정 최적화
- [ ] **4.2.2** OpenSearch 벌크 인덱싱 최적화
- [ ] **4.2.3** Consumer 병렬 처리 설정

### 4.3 클러스터링이 필요한 시점 판단 (2시간)
**질문들**:
- 단일 브로커로 목표 TPS를 달성할 수 있나?
- 장애 발생 시 복구 시간이 허용 범위인가?
- 데이터 손실 위험은 없나?

---

## 🌐 Phase 5: 클러스터링 (필요시)
> **목표**: 실제 분산 시스템 구축 및 운영

### 5.1 Kafka 클러스터 구축 시점
**언제 필요한가?**
- 단일 브로커가 10,000+ msg/sec을 처리하지 못할 때
- 장애 시 서비스 중단이 허용되지 않을 때
- 데이터 손실을 절대 허용할 수 없을 때

### 5.2 클러스터링 구현
```yaml
# 이때 비로소 3-broker 클러스터 구성
kafka1:
  # replication-factor: 3
kafka2:
kafka3:
```

- [ ] **5.2.1** Kafka 3-broker 클러스터 구성
- [ ] **5.2.2** 토픽 replication factor 설정
- [ ] **5.2.3** 파티셔닝 전략 구현
- [ ] **5.2.4** 장애 시나리오 테스트

### 5.3 OpenSearch 클러스터 (필요시)
- [ ] **5.3.1** OpenSearch 3-node 클러스터
- [ ] **5.3.2** 샤딩 및 복제 설정

---

## 🎯 학습 순서별 핵심 포인트

### 📚 **Phase 1 학습 목표**
- **Kafka 기본 개념**: Producer, Consumer, Topic, Partition
- **Spring Kafka**: KafkaTemplate, @KafkaListener
- **직렬화**: JSON 메시지 처리

### 🔍 **Phase 2 학습 목표**  
- **OpenSearch 기본**: 인덱스, 도큐먼트, 검색 쿼리
- **Java 클라이언트**: 인덱싱, 검색 API
- **데이터 모델링**: 로그 데이터 구조 설계

### 🖥️ **Phase 3 학습 목표**
- **Vue.js 기본**: Composition API, 컴포넌트, 상태관리
- **실시간 통신**: WebSocket 클라이언트/서버
- **데이터 시각화**: Chart.js, 반응형 차트

### ⚡ **Phase 4+ 학습 목표**
- **성능 튜닝**: 병목 지점 파악 및 최적화
- **분산 시스템**: 클러스터링, 파티셔닝, 복제
- **운영**: 모니터링, 장애 대응

---

## 💡 각 Phase별 소요 시간

| Phase | 기간 | 핵심 목표 |
|-------|------|----------|
| Phase 1 | 1주 | Kafka 기본 마스터 |
| Phase 2 | 1주 | OpenSearch 연동 |
| Phase 3 | 1-2주 | Vue.js 대시보드 |
| Phase 4 | 1-2주 | 성능 최적화 |
| Phase 5 | 필요시 | 클러스터링 |

**총 소요 시간**: 4-7주 (클러스터링 포함 시)