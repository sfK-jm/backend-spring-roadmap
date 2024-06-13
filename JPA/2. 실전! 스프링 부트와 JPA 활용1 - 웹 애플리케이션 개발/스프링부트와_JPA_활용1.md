# 프로젝트 환경설정

## 프로젝트 생성

- [스프링 부트 스타터](https://start.spring.io)
- Project - **Gradle - Groovy** project
- Dependencies: web, thymeleaf, jpa, h2, lombok, validation
  - groupId: jpabook
  - artifactid: jpashop

## 라이브러리 살펴보기

- spring-boot-starter-web
  - spring-boot-starter-tomcat: 톰캣(웹서버)
  - spring-webmvc: 스프링 웹 MVC
- spring-boot-starter-thymeleaf: 타임리프 템플릿 엔진(View)
- spring-boot-starter-data-jpa
  - spring-boot-starter-aop
  - spring-boot-starter-jdbc
    - HikariCP 커넥션 풀 (부트 2.0기본)
  - hibernate + JPA: 하이버네이트 + JPA
  - spring-data-jpa: 스프링 데이터 JPA
- spring-boot-starter(공통): 스프링 부트 + 스프링 코어 + 로깅
  - spring-boot
    - spring-core
  - spring-boot-starter-logging
    - logback, slf4;

**테스트 라이브러리**

- spring-boot-starter-test
  - junit: 테스트 프레임워크
  - mockito: 목 라이브러리
  - assertj: 테스트 코드를 좀 더 편리하게 작성하게 도와주는 라이브러리
  - spring-test: 스프링 통합 테스트 지원
<br>

- 핵심 라이브러리
  - 스프링 MVC
  - 스프링 ORM
  - JPA, 하이버네이트
  - 스프링 데이터 JPA
- 기타 라이브러리
  - H2데이터베이스 클라이언트
  - 커넥션 풀: 부트 기본은 HikariCP
  - WEB(thymeleaf)
  - 로깅 SLF4J&LogBack
  - 테스트

> [!NOTE]
> 스프링 데이터 JPA는 스프링과 JPA를 이해하고 사용해야 하는 응용 기술이다.

## View 환경 설정

**thymeleaf 템플릿 엔진**

- thymeleaf 공식 사이트: https://www.thymeleaf.org/
- 스프링 공식 튜토리얼: https://spring.io/guides/gs/serving-web-content
- 스프링부트 메뉴얼: https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-template-engines

<br>

- 스프링 부트 thymeleaf viewName aovld
  - `resources:templates/`+(ViewName)+`.html`

**jpabook.jpashop.HelloController**

```java
@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!");
        return "hello";
    }
}
```

**thymeleaf 템플릿엔진 동작 확인(hello.html)**

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Hello</title>
</head>
<body>
<p th:text="'안녕하세요. ' + ${data}" >안녕하세요</p>

</body>
</html>
```

위치: `resources/templates/hello.html`


- index.html 하나 만들기
  - `static/index.html`

```html
<!DOCTYPE html>
<html lang="en", xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Hello</title>
</head>
<body>
Hello
<a href="/hello">hello</a>
</body>
</html>
```

> [!TIP]
> `spring-boot-devtools`라이브러리를 추가하면, `html`파일을 컴파일만 해주면 서버 재시작 없이 View파일 변경이 가능하다<br>인텔리제이 컴파일 방법: 메뉴 build -> Recompile

## H2 데이터베이스 설치

개발이나 테스트 용도로 가볍고 편리한 DB, 웹 화면 제공

- 데이터베이스 파일 생성 방법
  - `jdbc:h2:~/jpashop`(최소 한번)
  - `~/jpashop.mv.db`파일 생성 확인
  - 이후 부터는 `jdbc:h2:tcp://localhost/~/jpashop`이렇게 접속

> [!WARNING]
> H2데이터베이스의 MVCC옵션은 H2 1.4.198버전부터 제거되었스빈다. 1.4.200버전에서는 MVCC옵션을 사용하면 오류가 발생합니다.

## JPA와 DB설정, 동작확인

`main/resources/application.yml`

```yml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/jpashop
    username: sa
    password:
    driver-class-name: org.h2.Driver


  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        show_sql: true
        format_sql: true

logging.level:
  org.hibernate.sql: debug
```

- `spring.jpa.hibernate.ddl-auto: create`
  - 이 옵션은 애플리케이션 실행 시점에 테이블을 drop하고, 다시 생성한다.

> [!TIP]
> 모든 로그 추력은 가급적 로거를 통해 남겨야 한다.<br>`show_sql`옵션은 `System.out`에 하이버네이트 실행 SQL을 남긴다.<br>`org.hibernate.SQL`옵션은 logger를 통해 하이버네이트 실행 SQL을 남긴다.

> [!WARNING]
> `application.yml`같은 `yml`파일은 띄어쓰기(스페이스)2칸으로 계층을 만듭니다. 따라서 띄어쓰기 2칸을 필수로 적어주어야 합니다.<br>예를 들어서 아래의 `datasource`는 `spring:`하위에 있고 앞에 띄어 쓰기 2칸이 있으므로 `spring.datasource`가 됩니다.

### 실제 동작하는지 확인하기

**회원 엔티티**<br>

```java
@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
}
```

**회원 리포지토리**<br>

```java
@Repository
public class MemberRepository {

    @PersistenceContext
    EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
```

**테스트**<br>

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;


    @Test
    @Transactional
    @Rollback(value = false)
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        member.setUsername("memberA");
        Long savedId = memberRepository.save(member);

        //when
        Member findMember = memberRepository.find(savedId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }
}
```

# 도메인 분석 설계

## 요구사항 분석

## 도메인 모델과 테이블 설계

## 엔티티 설계시 주의점

# 애플리케이션 구현 준비

## 구현 요구사항

## 애플리케이션 아키텍처

# 회원 도메인 개발

## 회원 리포지토리 개발

## 회원 서비스 개발

## 회원 기능 테스트

# 상품 도메인 개발

## 상품 엔티티 개발(비즈니스 로직 추가)

## 상품 리포지토리 개발

## 상품 서비스 개발

# 주문 도메인 개발

## 주문, 주문상품 엔티티 개발

## 주문 리포지토리 개발

## 주문 서비스 개발

## 주문 기능 테스트

## 주문 검색 기능 개발

# 웹 계층 개발

## 홈 화면과 레이아웃

## 회원등록

## 회원 목록 조회

## 상품 등록

## 상품 목록

## 상품 수정

## 변경 감지와 병합(merge)

## 상품 주문

## 주문 목록 검색, 취소