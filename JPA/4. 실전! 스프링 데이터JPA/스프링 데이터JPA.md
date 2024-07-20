# 프로젝트 환경 설정

## 프로젝트 생성

- Project: **Gradle-Groovy** project
- 사용 기능: web, jpa, h2, lombok
  - SpringBootVersion: 3.x.x
  - groupId: study
  - artifactId: data-jpa

## 라이브러리 살펴보기

`./gradlew dependencies --configuration compileClasspath`

**스프링 부트 라이브러리 살펴보기**

- spring-boot-starter-web
  - spring-boot-starter-tomcat: 톰캣 (웹서버)
  - spring-webmvc: 스프링 웹 MVC
- spring-boot-starter-data-jpa
  - spring-boot-starter-aop
  - spring-boot-starter-jdbc
    - HikariCP 커넥션 풀
  - hibernate + JPA: 하이버네이트 + JPA
  - spring-data-jpa: 스프링 데이터 JPA
- spring-boot-starter(공통): 스프링 부트 + 스프링 코어 + 로깅
  - spring-boot
    - spring-core
  - spring-boot-starter-logging
    - logback, slf4j


## H2 데이터베이스 설치

개발이나 테스트 용도로 가볍고 편리한 DB, 웹 화면 제공

- 데이터베이스 파일 생성 방법
  - `jdbc:h2:~/datajpa` (최소 한번)
  - `~/datajpa.mv.db`파일 생성 확인
  - 이후 부터는 `jdbc:h2:tcp://localhost/~/datajpa`이렇게 접속

https://www.h2database.com

## 스프링 데이터 JPA와 DB 설정, 동작확인

`application.yml`

```yml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/datajpa
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
#        show_sql: true
        format_sql: true

logging:
  level:
    org.hibernate.sql: debug
#    org.hibernate.type: trace
```

> [!TIP]
> 모든 로그 출력은 가급적 로거를 통해 남겨야 한다.<br>`show_sql`: 옵션은 `System.out`에 하이버네이트 실행 SQL을 남긴다. `org.hibernate.sql`옵션은 logger를 통해 하이버네이트 실행 sQL을 남긴다.

### 실제 동작하는지 확인하기

회원 엔티티

```java
@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;
    ...
}
```

회원 JPA 리포지토리

```java
@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
```

JPA 기반 테스트

```java
@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        Assertions.assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());

        Assertions.assertThat(findMember).isEqualTo(savedMember);
    }
}
```

스프링 데이터 JPA 리포지토리

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
}
```

스프링 데이터 JPA 기반 테스트

```java
@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}
```

- Entity, Repository 동작 확인
- jar 빌드해서 동작 확인

> [!NOTE]
> 스프링 부트를 통해 복잡한 설정이 다 자동화 되었다. `persistence.xml`도 없고 `LocalContainerEntityManagerFactoryBean`도 없다. 스프링 부트를 통한 추가 설정을 스프링 부트 메뉴얼을 참고하자.

### 쿼리 파라미터 로그 남기기

- 로그를 다음을 추가하기 `org.hibernate.type`: SQL 실행 파라미터를 로그로 남긴다.
- 외부 라이브러리 사용
  - https://github.com/gavlyukovskiy/spring-boot-data-source-decorator
  
스프링 부트를 사용하려면 이 라이브러리만 추가하면 된다.

`implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0'`

> [!TIP]
> 쿼리 파라미터를 로그로 남기는 외부 라이브러리는 시스템 자원을 사용하므로, 개발 단계에서는 편하게 사용해도 된다. 하지만 운영시스템에 젹용하려면 꼭 성능테스트를 하고 사용하는 것이 좋다.

# 예제 도메인 모델

# 공통 인터페이스 기능

# 쿼리 메소드 기능

# 확장 기능

# 스프링 데이터 JPA 분석

# 나머지 기능들