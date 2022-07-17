# open-banking
오픈뱅킹 API 를 활용하여, A->이용기관->B 흐름의 타투타를 구현.

## SPEC
- JDK: 1.8
- Framework: Spring Boot
- Language: Kotlin
- DB: Postgres ( Embedded )
- Docker

## 실행방법
- 

# 소스구조
```
src                       ---- 소스 Root
 └ kotlin                      └ Kotlin Source Root
 | └ com.exam                  |  └ Exam Main Package
 |   └ ap                      |     └- 오픈 뱅킹 API Mock
 |   └ bank                    |     └- 뱅킹 영역
 |   |  └ controller           |     |   └--- HTTP 요청 진입점
 |   |  └ dto                  |     |   └--- Data Transfer Object
 |   |  └ entity               |     |   └--- 테이블과 매칭되는 엔티티 영역
 |   |  └ repo                 |     |   └--- JPA or MyBatis Repository
 |   |  └ service              |     |   └--- 비즈니스 로직
 |   |  └ task                 |     |   └--- 주기적인 작업
 |   └ fwk                     |     └- 프레임워크 영역
 |      └ core                 |     |   └--- 프레임웤 기본
 |      |  └ base              |     |   |    └--- controller, service 등에서 상속받아야 하는 상위 클래스 모임
 |      |  └ component         |     |   |    └--- 컴포넌트 for framework
 |      |  └ error             |     |   |    └--- 에러 클래스 집합
 |      |  └ mybatis           |     |   |    └--- mybatis 위한 컨버트 클래스들
 |      └ custom               |     |   └--- 프로젝트별 특성 프레임웤 영역
 |         └ config            |     |   └--- 스프링 부트 설정 및 DB 설정
 |         └ dto               |     |   |
 |         └ filter            |     |   └--- AOP 영역
 |         └ service           |     |   |
 |         └ util              |     |   └--- 유틸리티
 └ resources                   └  리소스 ( 스프링 부트 설정 + Web + MyBatis Query )
  └ db                           └  flyway 쿼리 영역
  └ mybatis                      └  mybatis 쿼리 및 설정
test                      ---- 테스트 Root
   └ http                      └ http 파일로 작성된 테스트
   └ kotlin                    └ 코틀린으로 작성된 테스트 소스
     └ com.exam.bank                └ Exam Main Package
       └ base                          └ 모든 테스트 소스에서 상속 받아야 하는 클래스
       └ controller                    └ 콘트롤러 테스트
```
