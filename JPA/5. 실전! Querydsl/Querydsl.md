# 프로젝트 환경설정

## 프로젝트 생성

- 스프링 부트 스타터(https://start.spring.io/)
- Project: **Gradle - Groovy** Project
- 사용 기능: Spring Web, jpa, h2, lombok
  - SpringBootVersion: 3.x.x
  - groupId: study
  - artifactId: querydsl

## Query 설정과 검증

`build.gradle`에 주석을 참고해서 querydsl 설정 추가

```gradle
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.2.0'
	id 'io.spring.dependency-management' version '1.1.4'
}


group = 'study'
version = '0.0.1-SNAPSHOT'

java {
	sourceCompatibility = '17'
}

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}


repositories {
	mavenCentral()
}


dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'


	//test 롬복 사용
	testCompileOnly 'org.projectlombok:lombok'
	testAnnotationProcessor 'org.projectlombok:lombok'

	//Querydsl 추가
	implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
	annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
	annotationProcessor "jakarta.annotation:jakarta.annotation-api"
	annotationProcessor "jakarta.persistence:jakarta.persistence-api"
}

clean {
	delete file('src/main/generated')
}

tasks.named('test') {
	useJUnitPlatform()
}
```

### Querydsl 환경설정 검증

**검증용 엔티티 생성**

```java
package study.querydsl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Hello {

    @Id @GeneratedValue
    private Long id;
}
```

**빌드 옵션을 intelliJ로 설정한 경우**

Build Project, Rebuild, main(), 또는 테스트를 실행하면 src/main/generated 폴더에 Q객체가 생성된다.

**빌드 옵션을 Gradle로 설정한 경우**

`./gradlew clean compileJava`

**Q 타입 생성 확인**

- build -> generated -> querydsl
  - study.querydsl.entity.QHello.java 파일이 생성되어 있어야 함

> [!TIP]
> Q타입은 컴파일 시점에 자동 생성되므로 버전관리(GIT)에 포함하지 않는 것이 좋다. 앞서 설정에서 생성 위치를 gradle build폴더 아래 생성되도록 했기 때문에 이 부분도 자연스럽게 해결된다. (대부분 gradle build 폴더를 git에 포함하지 않는다.)

**테스트 케이스로 실행 검증**

```java
package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

@SpringBootTest
@Transactional
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {

		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = QHello.hello; //Querydsl Q타입 동작 확인

		Hello result = query
				.selectFrom(qHello)
				.fetchOne();

		Assertions.assertThat(result).isEqualTo(hello);

		//lombok 동작 확인 (hello.getId())
		Assertions.assertThat(result.getId()).isEqualTo(hello.getId());
	}

}
```

- Querydsl Q타입이 정상 동작하는가?
- lombok이 정상 동작 하는가?

> [!TIP]
> 스프링 부트에 아무런 설정도 하지 않으면 h2 DB를 메모리 모드로 JVM안에서 실행한다.

## 라이브러리 살펴보기

### gradle 의존ㄴ관계 보기

`./gradlew dependencies --configuration compileClasspath`

### Querydsl 라이브러리 살펴보기

- querydsl-apt: Querydsl관련 코드 생성 기능 제공
- querydsl-jpa: querydsl 라이브러리

### 스프링 부트 라이브러리 살펴보기

- spring-boot-starter-web
  - spring-boot-starter-tomcat: 톰캣(웹서버)
  - spring-webmvc: 스프링 웹 MVC
- spring-boot-starter-data-jpa
  - spring-boot-starter-aop
  - spring-boot-starter-jdbc
    - HikariCP 커넥션 풀 (부트 2.0 기본)
  - hibernate + JPA: 하이버네이트 + JPA
  - spring-data-jpa: 스프링 데이터 JPA
- spring-boot-starter(공통): 스프링 부트 + 스프링 코어 + 로깅
  - spring-boot
    - spring-core
  - spring-boot-starter-logging
    - logback, slf4j

### 테스트 라이브러리

- spring-boot-starter-test
  - junit: 테스트 프레임워크, 스프링 부트 2.2부터 junit5(`jupiter`)사용
    - 과거 버전은 `vintage`
  - mockito: 목 라이브러리
  - assertj: 테스트 코드를 좀 더 편하게 작성하게 도와주는 라이브러리
  - spring-test: 스프링 통합 테스트 지원
- 핵심 라이브러리
  - 스프링 MVC
  - JPA, 하이버네이트
  - 스프링 데이터 JPA
  - Querydsl
- 기타 라이브러리
  - H2 데이터베이스 클라이언트
  - 커넥션 풀: 부트 기본은 HikariCP
  - 로깅 SLF4J & LogBack
  - 테스트

## 스프링 부트 설정 - JPA, DB

```yml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/querydsl
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

- spring.jpa.hibernate.ddl-auto: create
  - 이 옵션은 애플리케이션 실행 시점에 테이블을 drop하고, 다시 생성한다.

> [!TIP]
> 모든 로그 출력은 가급적 로거를 통해 남겨야 한다.<br>`show_sql`: 옵션은 `System.out`에 하이버네이트 실행 SQL을 남긴다. `org.hibernate.sql`옵션은 logger를 통해 하이버네이트 실행 SQL을 남긴다.

# 예제 도메인 모델

> [!NOTE]
> 스프링 데이터 JPA와 동일한 예제 도메인 모델을 사용합니다.


## 예제 도메인 모델과 동작확인

<img src="./imgs/예제_도메인_모델.png"><br>

**Member 엔티티**

```java
package study.querydsl.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this(username, 0);
    }

    public Member(String username, int age) {
        this(username, age, null);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
```

- 롬복 설명
  - @Setter: 실무에서 가급적 Setter는 사용하지 않기
  - @NoArgsConstructor AccessLevel.PROTECTED: 기본 생성자 막고 싶은데, JPA스펙상 PROTECTED로 열어두어야 함
  - @ToString은 가급적 내부 필드만(연관관계 없는 필드만)
- `changeTeam()`으로 양방향 연관관계 한번에 처리(연관관계 편의 메소드)

**Team 엔티티**

```java
package study.querydsl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
```
- Member와 Team은 양방향 연관관계, `Member.team`이 연관관계의 주인, `Team.members`는 연관관계의 주인이 아님, 따라서 `Member.team`이 데이터베이스 외래키 값을 변경, 반대편은 읽기만 가능

**데이터 확인 테스트**

```java
package study.querydsl.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();

        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }
}
```

- 가급적 순수 JPA로 동작 확인(뒤에서 변경)
- db테이블 결과 확인
- 지연 로딩 동작 확인

# 기본 문법

## 시작 - JPQL vs Querydsl

**테스트 기본 코드**

```java
@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void before() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }
}
```

- 지금부터는 이 예제로 실행

**Querydsl vs JPQL**

```java
@Test
public void startJPQL() {
    //member1을 찾아라.
    String qlString = "select m from Member m " +
            "where m.username = :username";

    Member findMember = em.createQuery(qlString, Member.class)
            .setParameter("username", "member1")
            .getSingleResult();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}

@Test
public void startQuerydsl() {
    //member1을 찾아라.
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    QMember m = new QMember("m");

    Member findMember = queryFactory
            .select(m)
            .from(m)
            .where(m.username.eq("member1")) //파라미터 바인딩 처리
            .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```

- `EntityManager`로 `JPAQueryFactory`생성
- Querydsl은 JPQL빌더
- JPQL: 문자(실행 시점 오류), Querydsl: 코드(컴파일 시점 오류)
- JPQL: 파라미터 바인딩 직접, Querydsl: 파라미터 바인딩 자동 처리

### JPAQueryFactory를 필드로

```java
@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;
  
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        //...
    }

    @Test
    public void startQuerydsl2() {
        QMember m = new QMember("m");

        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();
        
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
}
```

- JPAQueryFactory를 필드로 제공하면 동시성 문제는 어떻게 될까? 동시성 문제는 JPAQueryFactory를 생성 할 때 제공하는 EntityManager(em)에 달려있다. 스프링 프레임워크는 여러 쓰레드에서 동시에 같은 EntityManager에 접근해도, 트랜잭션 마다 별도의 영속성 컨텍스트를 제공하기 때문에, 동시성 문제는 걱정하지 않아도 된다.

## 기본 Q-Type 활용

**Q클래스 인스턴스를 사용하는 2가지 방법**

```java
QMember qMember = new QMember("m"); //별칭 직접 지정
QMember qMember = QMember.member; //기본 인스턴스 사용
```

**기본 인스턴스를 static import와 함께 사용**

```java
import static study.querydsl.entity.QMember.*;

@Test
public void startQuerydsl3() {

    Member findMember = queryFactory
        .select(member)
        .from(member)
        .where(member.username.eq("member1"))
            .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```

다음 설정을 추가로 실행되는 JPQL을 볼 수 있다.

```properties
spring.jpa.properties.hibernate.use_sql_comments: true
```

> [!TIP]
> 같은 테이블을 조인해야 하는 경우가 아니면 기본 인스턴스를 사용하자

## 검색 조건 쿼리

**기본 검색 쿼리**

```java
@Test
public void search() {
    Member findMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1")
                    .and(member.age.eq(10)))
            .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
}
```

- 검색 조건은 `.and()`, `.or()`를 매서드 체인으로 연결할 수 있다.

> [!TIP]
> `select`, `from`을 `selectFrom`으로 합칠 수 있음

### JPQL이 제공하는 모든 검ㅁ색 조건 제공

```java
member.username.eq("member1") //username = 'member1'
member.username.ne("member1") //username = 'member1'
member.username.eq("member").not() // username != 'member1'

member.username.isNotNull() //이름이 is not null

member.age.in(10, 20) // age in (10,20)
member.age.notIn(10, 20) // age not in (10, 20)
member.age.between(10,30) // between 10, 30

member.age.goe(30) // age >= 30
member.age.gt(30) // age > 30
member.age.loe(30) // age <= 30
member.age.lt(30) // age < 30

member.username.like("member%") //like 검색
member.username.contains("member") // like '%member%' 검색
member.username.startsWith("member") //like 'member%' 검색
```

### AND 조건을 파라미터로 처리

```java
@Test
public void searchAndParam() {
    List<Member> result1 = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1"),
                    member.age.eq(10))
            .fetch();

    assertThat(result1.size()).isEqualTo(1);
}
```

- `where()`에 파라미터로 검색조건을 추가하면 `AND`조건이 추가됨
- 이 경우 `null`값은 무시 -> 메서드 추출을 활용해서 동적 쿼리를 깔끔하게 만들 수 있음 -> 뒤에서 설명

## 결과 조회

- `fetch()`: 리스트 조회, 데이터 없으면 빈 리스트 반환
- `fetchOne()`: 단 건 조회
  - 결과가 없으면: `null`
  - 결과가 둘 이상이면: `com.querydsl.core.NonUniqueResultException`
- `fetchFirst()`: `limit(1).fetchOne()`
- `fetchResults()`: 페이징 정보 포함, total count 쿼리 추가 실행
- `fetchCount()`: count 쿼리로 변경해서 count 수 조회

```java
//List
List<Member> fetch = queryFactory
        .selectFrom(member)
        .fetch();

//단 건
Member findMember1 = queryFactory
        .selectFrom(member)
        .fetchOne();

//처음 한 건 조회
Member findMember2 = queryFactory
        .selectFrom(member)
        .fetchFirst();

//페이징에서 사용
QueryResults<Member> results = queryFactory
        .selectFrom(member)
        .fetchResults();

//count 쿼리로 변경
long count = queryFactory
        .selectFrom(member)
        .fetchCount();
```

## 정렬

```java
/**
 * 회원 정렬 순서
 * 1. 회원 나이 내림차순(desc)
 * 2. 회원 이름 올림차순(asc)
 * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
 */
@Test
public void sort() throws Exception {
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));

    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.eq(100))
            .orderBy(member.age.desc(), member.username.asc().nullsLast())
            .fetch();

    for (Member m : result) {
        System.out.println("member = " + m);
    }

    Member member5 = result.get(0);
    Member member6 = result.get(1);
    Member memberNull = result.get(2);
    assertThat(member5.getUsername()).isEqualTo("member5");
    assertThat(member6.getUsername()).isEqualTo("member6");
    assertThat(memberNull.getUsername()).isNull();
}
```

- `desc()`, `asc()`: 일반 정렬
- `nullsLast()`, `nullsFirst()`: null 데이터 순서 부여

## 페이징

**조회 건수 제한**

```java
@Test
public void paging1() throws Exception {
    List<Member> result = queryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1) //0부터 시작(zero index)
            .limit(2) //최대 2건 조회
            .fetch();

    for (Member m : result) {
        System.out.println("member = " + m);
    }

    assertThat(result.size()).isEqualTo(2);
}
```

**전체 조회 수가 필요하면?**

```java
@Test
public void paging2() throws Exception {
    QueryResults<Member> queryResults = queryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1)
            .limit(2)
            .fetchResults();

    assertThat(queryResults.getTotal()).isEqualTo(4);
    assertThat(queryResults.getLimit()).isEqualTo(2);
    assertThat(queryResults.getOffset()).isEqualTo(1);
    assertThat(queryResults.getResults().size()).isEqualTo(2);
}
```

> [!CAUTION]
> count 쿼리가 실행되니 성능상 주의!

> [!TIP]
> 실무에서 페이징 쿼리를 작성할 때, 데이터를 조회하는 쿼리는 여러 테이블을 조인해야 하지만, count 쿼리는 조인이 필요 없는 경우도 있다. 그런데 이렇게 자동화된 count 쿼리는 원본 쿼리와 같이 모두 조인을 해버리기 때문에 성능이 안나올 수 있다. count쿼리에 조인이 필요없는 성능 최적화가 필요하면, count전용 쿼리를 별도로 작성해야 한다.

## 집합

### 집합 함수

```java
/**
 * JPQL
 * select
 *      COUNT(m),   //회원수
 *      SUM(m.age), //나이 합
 *      AVG(m.age), //평균 나이
 *      MAX(m.age), //최대 나이
 *      MIN(m.age)  //최소 나이
 * from Member m
 */
@Test
public void aggregation() throws Exception {
    List<Tuple> result = queryFactory
            .select(member.count(),
                    member.age.sum(),
                    member.age.avg(),
                    member.age.max(),
                    member.age.min())
            .from(member)
            .fetch();

    for (Tuple t : result) {
        System.out.println("t = " + t);
    }

    List<Member> members = queryFactory.selectFrom(member).fetch();

    for (Member m : members) {
        System.out.println("member = " + m);
    }

    Tuple tuple = result.get(0);
    assertThat(tuple.get(member.count())).isEqualTo(4);
    assertThat(tuple.get(member.age.sum())).isEqualTo(100);
    assertThat(tuple.get(member.age.avg())).isEqualTo(25);
    assertThat(tuple.get(member.age.max())).isEqualTo(40);
    assertThat(tuple.get(member.age.min())).isEqualTo(10);
}
```

- JPQL이 제공하는 모든 집합 함수를 제공한다.
- tuple은 프로젝션과 결과반환에서 설명한다.

### GroupBy 사용

```java
@Test
public void group() throws Exception {
    List<Tuple> result = queryFactory
            .select(team.name, member.age.avg())
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch();

    for (Tuple t : result) {
        System.out.println("t = " + t);
    }

    Tuple teamA = result.get(0);
    Tuple teamB = result.get(1);

    assertThat(teamA.get(team.name)).isEqualTo("teamA");
    assertThat(teamA.get(member.age.avg())).isEqualTo(15);

    assertThat(teamB.get(team.name)).isEqualTo("teamB");
    assertThat(teamB.get(member.age.avg())).isEqualTo(35);
}
```

`groupBy`, 그룹화된 결과를 제한하려먼 `having`

**groupBy(), having() 예시**

```java
    .group(item.price)
    .having(item.price.gt(1000))
```

## 조인 - 기본 조인

### 기본 조인

조인의 기본 문법은 첫 번째 파라미터에 조인 대상을 지정하고, 두 번째 파라미터에 별칭(alias)으로 사용할 Q타입을 지정하면 된다.

```java
join(조인대상, 별칭으로 사용할 Q타입)
```

**기본 조인**

```java
/**
 * 팀 A에 소속된 모든 회원
 */
@Test
public void join() throws Exception {
    QMember member = QMember.member;
    QTeam team = QTeam.team;

    List<Member> result = queryFactory
            .selectFrom(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch();

    assertThat(result)
            .extracting("username")
            .containsExactly("member1", "member2");
}
```

- `join()`, `innerJoin()`: 내부 조인(inner join)
- `leftJoin()`: left 외부 조인(left outer join)
- `rightJoin()`: right 외부 조인(right outer join)
- JPQL의 `on`과 성능 최적화를 위한 `fetch`조인 제공 -> 다음 on절에서 설명

### 세타 조인

연관관계가 없는 필드로 조인

```java
/**
 * 세타 조인(연관관계가 없는 필드로 조인)
 * 회원의 이름이 팀 이름과 같은 회원 조회
 */
@Test
public void theta_join() throws Exception {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));

    List<Member> result = queryFactory
            .select(member)
            .from(member, team)
            .where(member.username.eq(team.name))
            .fetch();

    assertThat(result)
            .extracting("username")
            .containsExactly("teamA", "teamB");
}
```

- from절에 여러 엔티티를 선택해서 세타 조인
- 외부 조인 불가능 -> 다음에 설명할 조인 on을 사용하면 외부 조인 가능

## 조인 - on절

- ON절을 활용한 조인(JPA 2.1부터 지원)
  1. 조인 대상 필터링
  2. 연관관계 없는 엔티티 외부 조인

### 1. 조인 대상 필터링

예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회

```java
/**
 * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
 * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
 * SQL : SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='teamA'
 */
@Test
public void join_on_filtering() throws Exception {
    List<Tuple> result = queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(member.team, team).on(team.name.eq("teamA"))
            .fetch();

    for (Tuple tuple : result) {
        System.out.println("tuple = " + tuple);
    }
}
```

**결과**

```java
tuple = [Member(id=1, username=member1, age=10), Team(id=1, name=teamA)]
tuple = [Member(id=2, username=member2, age=20), Team(id=1, name=teamA)]
tuple = [Member(id=3, username=member3, age=30), null]
tuple = [Member(id=4, username=member4, age=40), null]
```

> [!TIP]
> on절을 활용해 조인 대상을 필터링 할 때, 외부조인이 아니라 내부조인(inner join)을 사용하면, where절에서 필터링 하는 것과 기능이 동일하다. 따라서 on절을 활용한 조인 대상 필터링을 사용할 때, 내부조인이면 익숙한 where절로 해결하고, 정말 외부조인이 필요한 경우에만 이 기능을 사용하자.

### 2. 연관관계 없는 엔티티 외부 조인

예) 회원의 이름과 팀의 이름이 같은 대상 **외부 조인**

```java
/**
 * 2. 연관관계 없는 엔티티 외부 조인
 * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
 * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
 * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
 */
@Test
public void join_on_no_relation() throws Exception {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));

    List<Tuple> result = queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(team).on(member.username.eq(team.name))
            .fetch();

    for (Tuple tuple : result) {
        System.out.println("tuple = " + tuple);
    }
}
```

- 하이버네이트 5.1부터 `on`을 사용해서 서로 관계가 없는 필드로 외부 조인하는 기능이 추가되었다. 물론 내부 조인도 가능하다.
- 주의! 문법을 잘 봐야 한다. **leftJoin()`부분에 일반 조인과 다르게 엔티티 하나만 들어간다.
  - 일반조인: `lftJoin(member.team, team)`
  - on조인: `from(member).leftJoin(team).on(xxx)`

**결과**

```java
tuple = [Member(id=1, username=member1, age=10), null]
tuple = [Member(id=2, username=member2, age=20), null]
tuple = [Member(id=3, username=member3, age=30), null]
tuple = [Member(id=4, username=member4, age=40), null]
tuple = [Member(id=5, username=teamA, age=0), Team(id=1, name=teamA)]
tuple = [Member(id=6, username=teamB, age=0), Team(id=2, name=teamB)]
```

## 조인 - 페치 조인

페치 조인은 SQL에서 제공하는 기능은 아니다. SQL조인을 활용해서 연관된 엔티티를 SQL 한번에 조회하는 기능이다. 주로 성능 최적화에 사용하는 방법이다.

**페치 조인 미적용**

지연로딩으로 Member, Team SQL 쿼리 각각 실행

```java
@PersistenceUnit
EntityManagerFactory emf;

@Test
public void fetchJoinNo() throws Exception {
    em.flush();
    em.clear();

    Member findMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1"))
            .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

    assertThat(loaded).as("페치 조인 미젹용").isFalse();
}
```

**페치 조인 적용**

즉시로딩응로 Member, Team SQL 쿼리 조인으로 한번에 조회

```java
@Test
public void fetchJoinUse() throws Exception {
em.flush();
    em.clear();

    Member findMember = queryFactory
            .selectFrom(member)
            .join(member.team, team).fetchJoin()
            .where(member.username.eq("member1"))
            .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 적용").isTrue();
}
```

사용방법

- `join(), leftJoin()`등 조인 기능 뒤에 `fetchJoin()`이라고 추가하면 된다.

## 서브 쿼리

`com.querydsl.jpa.JPAExpressions`사용

**서브쿼리 eq 사용**

```java
/**
 * 나이가 가장 많은 회원 조회
 */
@Test
public void subQuery() throws Exception {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.eq(
                    JPAExpressions
                            .select(memberSub.age.max())
                            .from(memberSub)
            ))
            .fetch();

    assertThat(result).extracting("age")
            .containsExactly(40);
}
```

**서브 쿼리 goe 사용**

```java
/**
 * 나이가 평균 나이 이상인 회원
 */
@Test
public void subQueryGoe() throws Exception {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.goe(
                    JPAExpressions
                            .select(memberSub.age.avg())
                            .from(memberSub)
            ))
            .fetch();

    assertThat(result).extracting("age")
            .containsExactly(30, 40);
}
```

**서브쿼리 여러 건 처리 in 사용**

```java
/**
 * 서브쿼리 여러 건 처리, in 사용
 */
@Test
public void subQueryIn() throws Exception {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.in(
                    JPAExpressions
                            .select(memberSub.age)
                            .from(memberSub)
                            .where(memberSub.age.gt(10))
            ))
            .fetch();

    assertThat(result).extracting("age")
            .containsExactly(20, 30, 40);
}
```

**select절에 subquery**

```java
@Test
public void select절에_subquery() throws Exception {
    QMember memberSub = new QMember("memberSub");

    List<Tuple> fetch = queryFactory
            .select(member.username,
                    JPAExpressions
                            .select(memberSub.age.avg())
                            .from(memberSub)
            ).from(member)
            .fetch();

    for (Tuple tuple : fetch) {
        System.out.println("username = " + tuple.get(member.username));
        System.out.println("age = " + tuple.get(JPAExpressions.select(memberSub.age.avg())
                .from(memberSub)));
    }
    //username = member1
    //age = 25.0
    //username = member2
    //age = 25.0
    //username = member3
    //age = 25.0
    //username = member4
    //age = 25.0
}
```

**static import 활용**

```java
import static com.querydsl.jpa.JPAExpressions.select;

@Test
public void static_import활용() throws Exception {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.eq(
                    select(memberSub.age.max())
                            .from(memberSub)
            ))
            .fetch();
}
```

**from절의 서브쿼리 한계**

JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다. 당연히 Querydsl도 지원하지 않는다. 하이버네이트 구현체를 사용하면 select절의 서브쿼리는 지원한다. Querydsl도 하이버네이트 구현체를 사용하면 select절의 서브쿼리리를 지원한다.

**from절의 서브쿼리 해결방안**

1. 서브쿼리를 join으로 변경한다.(가능한 상황도 있고, 불가능한 상황도 있다.)
2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
3. nativceSQL을 사용한다.

## Case 문

**select, 조건절(where), order by에서 사용 가능**

**단순한 조건**

```java
List<String> result = queryFactory
        .select(member.age
                .when(10).then("열살")
                .when(20).then("스무살")
                .otherwise("기타"))
        .from(member)
        .fetch();
```

**복잡한 조건**

```java
List<String> result = queryFactory
        .select(new CaseBuilder()
                .when(member.age.between(0. 20)).then("0~20살")
                .when(member.age.between(21, 30)).then("21~30살"))
                .otherwise("기타")
        .from(member)
        .fetch();
```

**orderBy에서 Case문 함께 사용하기 예제**

예를 들어서 다음과 같은 임의의 순서로 회원ㅇ르 출력하고 싶다면?

1. 0 ~ 30살이 아닌 회원을 가장 먼저 출력
2. 0 ~ 20살 회원 출력
3. 21 ~ 30살 회원 출력

```java
NumberExpression<Integer> rankPath = new CaseBuilder()
        .when(member.age.between(0, 20)).then(2)
        .when(member.age.between(21, 30)).then(1)
        .otherwise(3);

List<Tuple> result = queryFactory
        .select(member.username, member.age, rankPath)
        .from(member)
        .orderBy(rankPath.desc());
        .fetch();

for (Tuple tuple : result) {
    String username = tuple.get(member.username);
    Integer age = tuple.get(member.age);
    Integer rank = tuple.get(rankPath);
    System.out.println("username = " + username + " age = " + age + " rank = " + rank);
}
```

Querydsl은 자바 코드로 작성하기 때문에 `rankPath`처럼 복잡한 조건을 변수로 선언해서 `select`절, `orderBy`절에서 함께 사용할 수 있다.

```
결과
username = member4 age = 40 rank = 3
username = member1 age = 10 rank = 2
username = member2 age = 20 rank = 2
username = member3 age = 30 rank = 1
```

## 상수, 문자 더하기

상수가 필요하면 `Expressions.constant(xxx)`사용

```java
Tuple result = queryFactory
        .select(member.username, Expressions.constant("A"))
        .from(member)
        .fetchFirst();

// result = [member1, A]
```

> [!TIP]
> 위와 같이 최적화가 가능하면 SQL에 constant값을 넘기지 않는다. 상수를 더하는 것 처럼 최적화가 어려우면 SQL에 constant 값을 넘긴다.

### 문자 더하기 concat

```java
@Test
public void 문자_더하기() {
    String result = queryFactory
            .select(member.username.concat("_").concat(member.age.stringValue()))
            .from(member)
            .where(member.username.eq("member1"))
            .fetchOne();

    System.out.println("result = " + result);

    //result = member1_10
}
```

> [!TIP]
> `member.age.stringValue()`부분이 중요한데, 문자가 아닌 다른 타입들은 `stringValue()`로 문자로 변환할 수 있다. 이 방법은 ENUM을 처리할 때도 자주 사용한다.

# 중급 문법

## 프로젝션과 결과 반환 - 기본

프로젝션: select 대상 지정

### 프로젝션 대상이 하나

```java
List<String> result = queryFactory
        .select(member.username)
        .from(member)
        .fetch();
```

- 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
- 프로젝션 대상이 둘 이상이면 튜플이나 DTO로 조회

### 튜플 조회

프로젝션 대상이 둘 이상일 때 사용

`com.querydsl.core.Tuple`

```java
List<Tuple> result = queryFactory
        .select(member.username, member.age)
        .from(member)
        .fetch();

for (Tuple tuple : result) {
    String username = tuple.get(member.username);
    Integer age = tuple.get(member.age);
    System.out.println("username=" + username);
    System.out.println("age=" + age);
}
```

## 프로젝션과 결과 반환 - DTO 조회

순수 JPA에서 DTO조회

**MemberDTO**

```java
package study.querydsl.dto;

import lombok.Data;

@Data
public class MemberDto {
    private String username;
    private int age;

    public MemberDto() {
    }

    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```

**순수 JPA에서 DTO조회 코드**

```java
List<MemberDto> result = em.createQuery(
    "select new study.querydsl.dto.MemberDto(m.username, m.age) " +
            "from Member m", MemberDto.class)
    .getResultList();
```

- 순수 JPA에서 DTO를 조회할 때는 new 명령어를 사용해야함
- DTO의 package이름을 다 적어줘야해서 지저분함
- 생성자 방식만 지원함

### Querydsl 빈 생성(Bean population)

결과를 DTO 반환할 때 사용<br>다음 3가지 방법 지원

- 프로퍼티 접근
- 필드 직접 접근
- 생성자 사용

**프로퍼티 접근 - Setter**

```java
List<MemberDto> result = queryFactory
        .select(Projections.bean(MemberDto.class,
                member.username,
                member.age))
        .from(member)
        .fetch();
```

**필드 직접 접근**

```java
List<MemberDto> result = queryFactory
        .select(Projections.fields(MemberDto.class,
                member.username,
                member.age))
        .from(member)
        .fetch();
```

**별칭이 다를 때**

```java
package study.querydsl.dto;

import lombok.Data;

@Data
public class UserDto {
    private String name;
    private int age;
}
```

```java
List<UserDto> fetch = queryFactory
        .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub), "age")
                )
        ).from(member)
        .fetch();
```

- 프로퍼티나, 필드 접근 생성 방식에서 이름이 다를 때 해결 방안
- `ExpressionUtils.as(source, alias)`: 필드나, 서브 쿼리에 별칭 적용
- `username.as("memberName")`: 필드에 별칭 적용

**생성자 사용**

```java
List<MemberDto> result = queryFactory
        .select(Projections.constructor((MemberDto.class),
                member.username,
                member.age))
        .from(member)
        .fetch();
```

## 프로젝션과 결과 반환 - @QueryProjection

**생성자 + @QueryProjection**

```java
package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(of = {"username", "age"})
public class MemberDto {
    private String username;
    private int age;

    public MemberDto() {
    }

    @QueryProjection
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```

`QMemberDto` 생성 확인

**@QueryProjection 활용**

```java
List<MemberDto> result = queryFactory
        .select(new QMemberDto(member.username, member.age))
        .from(member)
        .fetch();
```

이 방법은 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법이다.<br>다만 DTO에 QueryDSL어노테이션을 유지해야 하는 점과 DTO까지 Q파일을 생성해야 하는 단점이 있다.

**distinct**

```java
List<String> result = queryFactory
        .select(member.username).distinct()
        .from(member)
        .fetch();
```

> [!TIP]
> distinct는 JPQL의 distinct와 같다.

## 동적 쿼리 - BooleanBuilder 사용

**동적 쿼리를 해결하는 두가지 방식**

- BooleanBuilder
- Where 다중 파라미터 사용

```java
@Test
public void 동적쿼리_BolleanBuilder() {
    String usernameParam = "member1";
    Integer ageParam = 10;

    List<Member> result = searchMember1(usernameParam, ageParam);
    assertThat(result.size()).isEqualTo(1);
}

private List<Member> searchMember1(String usernameCond, Integer ageCond) {
    BooleanBuilder builder = new BooleanBuilder();
    if (usernameCond != null) {
        builder.and(member.username.eq(usernameCond));
    }
    if (ageCond != null) {
        builder.and(member.age.eq(ageCond));
    }

    return queryFactory
            .selectFrom(member)
            .where(builder)
            .fetch();
}
```

## 동적 쿼리 - Where 다중 파라미터 사용

```java
@Test
public void 동적쿼리_WhereParam() {
    String usernameParam = "member1";
    Integer ageParam = 10;

    List<Member> result = searchMember2(usernameParam, ageParam);
    assertThat(result.size()).isEqualTo(1);
}

private List<Member> searchMember2(String usernameCond, Integer ageCond) {
    return queryFactory
            .selectFrom(member)
            .where(usernameEq(usernameCond), ageEq(ageCond))
            .fetch();
}

private BooleanExpression usernameEq(String usernameCond) {
    return usernameCond != null ? member.username.eq(usernameCond) : null;
}

private BooleanExpression ageEq(Integer ageCond) {
    return ageCond != null ? member.age.eq(ageCond) : null;
}
```

- `where`조건에 `null`값은 무시된다.
- 메서드를 다른 쿼리에서도 재활용 할 수 있다.
- 쿼리 자체의 가독성이 높아진다.

**조합 가능**

```java
private BooleanExpression allEq(String usernameCond, Integer ageCond) {
    return usernameEq(usernameCond).and(ageEq(ageCond));
}
```

- `null`체크는 주의해서 처리해야 함

## 수정, 삭제 벌크 연산

**쿼리 한번으로 대량 데이터 수정**

```java
long count = queryFactory
        .update(member)
        .set(member.username, "비회원")
        .where(member.age.lt(28))
        .execute();
```

**기존 숫자에 1더하기**

```java
long count = queryFactory
        .update(member)
        .set(member.age, member.age.add(1))
        .execute();
```

곱하기: `multiply(x)`

*쿼리 한번으로 대량 데이터 삭제**

```java
long count = queryFactory
    .delete(member)
    .where(member.age.gt(18))
    .execute();
```

> [!TIP]
> JPQL 배치와 마찬가지로, 영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에 배치 쿼리를 실행하고 나면 영속성 컨텍스트를 초기화 하는 것이 안전하다.

## SQL function 호출

# 실무 활용 - 순수 JPA와 Querydsl

# 실무 활용 - 스프링 데이터 JPA와 Querydsl

# 스프링 데이터 JPA가 제공하는 Querydsl 기능

# 스프링 부트 3.x(2.6 이상), Querydsl 5.0 지원 방법
