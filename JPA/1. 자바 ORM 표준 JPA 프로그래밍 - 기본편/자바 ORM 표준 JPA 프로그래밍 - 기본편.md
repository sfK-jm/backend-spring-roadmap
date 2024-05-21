# JPA 시작

## 프로젝트 생성

앞으로 H2데이터베이스와 메이븐을 사용해서 개발 할 것이다.


### 라이브러리 추가 (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>your-project</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- Hibernate -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>5.6.14.Final</version>
        </dependency>

        <!-- H2 -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.1.214</version>
        </dependency>
    </dependencies>
</project>
```

### JPA설정하기 - (persistence.xml)

`/META-INF/persistence.xml`

데이터 베이스 설정

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">

    <persistence-unit name="jpabook">
        <properties>
            <!-- H2 데이터베이스 설정 -->
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver" />
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test" />
            <property name="javax.persistence.jdbc.user" value="sa" />
            <property name="javax.persistence.jdbc.password" value="" />
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect" />

            <!-- 옵션 -->
            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />
            <property name="hibernate.use_sql_comments" value="true" />
            <property name="hibernate.id.new_generator_mappings" value="true" />
            <property name="hibernate.hbm2ddl.auto" value="create" />
        </properties>
    </persistence-unit>
</persistence>
```

### 데이터베이스 방언

- JPA는 특정 데이터베이스에 종속 x
- 각각의 데이터베이스가 제공하는 SQL 문법과 함수는 조금씩 다름
  - 가변 문자: MySQL은 VARCHAR, Oracle은 VARCHHAR@
  - 문자열을 자르는 함수: SQL표준은 SUBSTRING(), Oracle은 SUBSTR()
  - 페이징: MySQL은 LIMIT, Oracle은 ROWNUM

> 방언: SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능

<img src="./imgs/jpa시작/데이버베이스_방언.png"><br>

- **hibernate.dialect**속성에 지정
  - H2: org.hibernate.dialect.H2Dialect
  - Oracle 10g: org.hibernate.dialect.Oracle10gDialect
  - MySQL: org.hibernate.dialect.MYSQL5InnoDBDialect
- 하이버네이트는 40가지 이상의 데이터베이스 방언 지원

## 애플리케이션 개발

### JPA 구동 방식

<img src="./imgs/jpa시작/JPA구동-방식.png"><br>

### Member 클래스 생성

```java
package jpabasic.ex1hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME")
    private String username;

    private Integer age;

    // getter, setter
}
```

```sql
CREATE TABLE MEMBER (
                        ID VARCHAR(255) NOT NULL, --아이디(기본 키)
                        NAME VARCHAR(255),        --이름
                        AGE INTEGER NOT NULL,     --나이
                        PRIMARY KEY (ID)
)
```

- **@Entity**: JPA가 관리할 객체
- **@Id**: 데이터베이스 PK와 매핑

### 회원 저장

- 회원 등록
- 회원 수정
- 회원 삭제
- 회원 단 건 조회

```java

public class JpaMain {

    public static void main(String[] args) {

        //엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager(); //엔티티 매니저 생성

        EntityTransaction tx = em.getTransaction(); //트랜잭션 기능 획득
        tx.begin();

        Member member = new Member();
        member.setId(1L);
        member.setName("TestA");

        //등록
        em.persist(member);

        tx.commit();

        em.close();
        emf.close();
    }
}
```

- **엔티티 매니저 팩토리**는 하나만 생성해서 애플리케이션 전체에서 공유
- **엔티티 매니저**는 쓰레드간에 공유x(사용하고 버려야 한다.)
- **JPA의 모든 데이터 변경은 트랜잭션 안에서 실행**(`tx.begin() - tx.commit()`)

### 회원 단건 조회

```java
public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.name = " + findMember.getName());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        
        emf.close();
    }
}
```

트랜잭션에서 문제가 발생했을 경우 트랜잭션을 ROLLBACK해야 한다. 또한 문제 여부와 상관없이 `EntityManager`를 닫아서 DB Connection을 종료해야 한다.

### 회원 삭제

```java
public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member findMember = em.find(Member.class, 1L);
			em.remove(findMember);
            
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        
        emf.close();
    }
}
```

### 회원 수정

```java
public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member findMember = em.find(Member.class, 1L);
			findMember.setName("HelloJPA");
            
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        
        emf.close();
    }
}
```

자바 객체를 수정하듯이 DB를 수정할 수 있다. JPA를 통하여 객체를 가져올 경우 JPA에서 객체를 관리하여 트랜잭션 시점에 객체의 변경 여부를 감지하여 객체가 변경되었을 경우 UPDATE 쿼리를 생성한다.

### 회원 다수 조회

```java
public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
			List<Member> result = em.createQuery("select m from Member as m", Member.class).getResultList();
            
			for (Member member : result) {
                System.out.println("member.name = " + member.getName());
            }
            
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        
        emf.close();
    }
}
```

JPQL을 사용해서 다수의 객체를 조회하고 있다.

### JPQL

가장 단순한 조회 방법은 EntityManager.find(), 객체 그래프 탐색 방법은 a.getB().getC()이다.<br>하지만 만약에, 나이가 18살 이상인 회원을 모두 검색하고 싶으면 JPQL가 필요하다.

- JPA를 사용하면 엔티티 객체를 중심으로 개발
  - 문제는 검색 쿼리
  - JPA가 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
  - 그러나 모든 DB데이터를 객체로 변환해서 검색하는 것은 불가능
  - 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요하다.
- JPQL
  - JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
  - SQL문법과 유사 SELECT, FROM, WHERE 등등 지원
  - JPQL은 엔티티 객체를 대상으로 쿼리
  - SQL은 데이터베이스 테이블을 대상으로 쿼리
  - 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
  - SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않음
  - JPQL을 한마디로 정의하면 객체 지향 SQL

# 영속성 관리

## 영속성 컨텍스트

JPA에서 가장 중요한 2가지

- 객체외 관계형 데이터베이스 매핑하기 (정적인 영역)
- **영속성 컨텍스트** (동적인 영역)

### 엔티티 매니저 팩토리와 엔티티 매니저

<img src="./imgs/영속성_관리/엔티티_매니저_팩토리와_엔티티_매니저.png"><br>

웹 애플리케이션에서 요청이 들어온 경우 엔티티 매니저 팩토리는 고객의 요청이 들어올 때마다 엔티티 매니저를 생성하고 엔티티 매니저는 DB 커넥션 풀을 사용하여 DB에 접근한다.


### 영속성 컨텍스트

- JPA를 이해하는데 가장 중요한 용어
- 엔티티를 영구 저장하는 환경
- `EntityManager.persist(entity)`: 엔티티를 영속성 컨텍스트에 저장한다는 의미

영속성 컨텍스트는 논리적인 개념으로 **눈에 보이지 않음**.<br>엔티티 매니저를 통해서 영속성 컨텍스트에 접근

### 엔티티의 생명 주기

 - **비영속**(new/transient)
   - 영속성 컨텍스트와 전혀 관계가 없는 **새로운**싱테
 - **영속**(managed)
   - 영속성 컨텍스트에 **관리**되는 상태
 - **준영속**(detached)
   - 영속성 컨텍스트에 저장되었다가 **분리**된 상태
 - **삭제**(removed)
   - **삭제**된 상태

<img src="./imgs/영속성_관리/엔티티의_생명주기.png"><br>

### 비영속

영속성 컨텍스트와 전혀 관계가 없는 새로운 상태

<img src="./imgs/영속성_관리/비영속.png"><br>

### 영속

영속성 컨텍스트에 관리되는 상태

<img src="./imgs/영속성_관리/영속.png"><br>

### 준영속, 삭제

- 준영속: 영속성 컨텍스트에 저장되었다가 분리된 상태
- 삭제: 삭제된 상태

<img src="./imgs/영속성_관리/준영속,삭제.png"><br>

### 영속성 컨텍스트의 이점

- 1차 캐시
- 동일성(identity)보장
- 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
- 변경 감지(Dirty Checking)
- 지연 로딩(Lazy Loading)

#### 1차 캐시

영속성 컨텍스트는 내부에 1차 캐시라는 것을 들고있다. (Map 형태)<br>1차 캐시 내부에는(엔티티 객체의 PK정보, 엔티티 객체, 최초 1차 캐시에 들오온 시점의 엔티티 정보)등이 있다.

<img src="./imgs/영속성_관리/1차캐시-1.png"><br>

```java
//엔티티를 생성한 상태 (비영속)
Member member = new Member();
member.setId("member1");
member.setName("회원1");

//엔티티를 영속
em.persist(member);
```

<img src="./imgs/영속성_관리/1차캐시-2.png"><br>

```java
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");

//1차 캐시에 저장됨
em.persist(member);

//1차 캐시에서 조회
Member findMember = em.find(Member.class, "member1");
```

JPA에서 조회를 할 경우 바로 DB에 접근하지 않고 영속성 컨텍스트 내의 1차 캐시에 접근한다.

<img src="./imgs/영속성_관리/1차캐시-3.png"><br>

```java
Member findMember2 = em.find(Member.class, "member2");
```

그러나 엔티티 매니저는 트랜잭션 단위로 존재하므로 어플리케이션 전체에서 공유하는 캐시(2차 캐시)와 달리 성능의 이점은 크지 않다.

#### 영속 엔티티의 동일성 보장

```java
Member a = em.find(Member.class, "member1");
member b = em.find(Member.class, "member1");
System.out.println(a == b); //동일성 비교 true
```

1차 캐시로 반복 가능한 읽기(REPEATEABLE READ)등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공

#### 트랜잭션을 지원하는 쓰기 지연

```java
EntityManager em = emf.createEntityManager();
EntityTranscation transaction = em.getTransaction();
//엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다.
transaction.begin(); //[트랜잭션] 시작

em.persist(memberA);
em.persist(memberB);
//여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.

//커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.
transaction.commit(); //[트랜잭션] 커밋
```

<img src="./imgs/영속성_관리/쓰기_지연1.png"><br>

<img src="./imgs/영속성_관리/쓰기_지연2.png"><br>

#### 변경 감지(Dirty Checking)

```java
EntityManager dm = dmf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
transaction.begin(); //[트랜잭션] 시작

//영속 엔티티 조회
Member memberA = em.find(Member.class, "memberA");

//영속 엔티티 데이터 수정
memberA.setUsername("hi");
memberA.setAge(10);

//em.update(member) 이런 코드가 있어야 하지 않을까?

transaction.commit(); //[트랜잭션] 커밋
```

<img src="./imgs/영속성_관리/변경감지.png"><br>

### 플러시

영속성 컨텍스트의 변경내용을 데이터베이스에 반영

#### 플러시 발생

- 변경 감지
- 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
- 쓰기 지연 SQL저장소의 쿼리를 데이터 베이스에 전송(등록, 수정, 삭제 쿼리)

#### 영속성 컨텍스트를 플러시하는 방법

- `em.flush()` - 직접 호출, 일반적인 경우에는 사용하지 않으나 테스트할 때 사용할 수 있음
- 트랜잭션 커밋 - 플리시 자동 호출
- JPQL 쿼리 실행 - 플리시 자동 호출

#### JPQl 쿼리 실행시 플러시가 자동으로 호출하는 이유

```java
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);

//중간에 JPQL 실행
query = em.createQuery("select m from  Member m". Member.class);
List<Member> members = query.getResultList();
```

위의 코드를 보면 JPQl실행시 플러시가 호출되지 않으면 persist로 영속성 컨텍스트에 반영된 내용은 가져올 수 없다.

### 플러시 모드 옵션

```java
em.setFlushMode(FlushModeType.COMMIT)
```

- FlushModeType.AUTO: 커밋이나 쿼리를 실행할 때 플리서(기본값, 변경할 일은 거의 없다.)
- FlushModeType.COMMIT: 커밋할 때만 플러시

### 정리

**플러시**

- 영속성 컨텍스트를 비유지 않음
- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화
- 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화하면 됨

### 준영속 상태

- 영속 -> 준영속
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached)
- 영속성 컨텍스트가 제공하는 기능을 사용 못함

#### 준영속 상태로 만드는 방법

- `em.detach(entity)`: 특정 엔티티만 준영속 상태로 전환
- `em.clear()`: 영속성 컨텍스트를 완전히 초기화
- `em.close()`: 영속성 컨텍스트를 종료