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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
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

### 실습 - 회원 저장

- 회원 등록
- 회원 수정
- 회원 삭제
- 회원 단 건 조회

```java
package jpabasic.ex1hellojpa;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        //엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager(); //엔티티 매니저 생성

        EntityTransaction tx = em.getTransaction(); //트랜잭션 기능 획득

        try {
            tx.begin(); //트랜잭션 시작
            logic(em); //비즈니스 로직
            tx.commit(); //트랜잭션 커밋
        } catch (Exception e) {
            //e.printStackTrace();
            tx.rollback(); //트랜잭션 롤백
        } finally {
            em.close(); //엔티티 매니저 종료
        }

        emf.close(); //엔티티 매니저 팩토리 종료
    }

    public static void logic(EntityManager em) {
        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("unknown");
        member.setAge(2);

        //등록
        em.persist(member);

        //수정
        member.setAge(20);

        //한 건 조회
        Member findMember = em.find(Member.class, id);
        System.out.println("findMember = " + findMember.getUsername() + ", age=" + findMember.getAge());

        //목록 조회
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        System.out.println("members.size = " + members.size());
    }
}
```

### 주의 사항

- **엔티티 매니저 팩토리**는 하나만 생성해서 애플리케이션 전체에서 공유
- **엔티티 매니저**는 쓰레드간에 공유x(사용하고 버려야 한다.)
- **JPA의 모든 데이터 변경은 트랜잭션 안에서 실행**

### JPQL

조회시 단순한 조회(`em.find(...)`)가 아닌, 조건이 추가되어야 하는 등의 경우에 **JPQL**이라는 것을 사용해야 한다.

```java
//목록 조회
List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
for (Member m : members) {
    System.out.println("member.name = " + m.getUsername());
}
```

- JPQ는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공한다.(객체 지향 SQL)
  - 테이블을 대상으로 쿼리 하는 것이 아닌 엔티티 객체를 대상으로 쿼리
  - JPQL은 SQL을 추상화했기 때문에 특정 데이터베이스 SQL에 의존하지 않는 장점이 있다.
- **JPQl은 엔티티 객체**를 대상으로 쿼리
- **SQL은 데이터베이스 테이블**을 대상으로 쿼리
- JPQL은 테이블이 아닌 **객체를 대상으로 검색하는 객체 지향 쿼리**
- JQL을 한마디로 정의하면 객체 지향 SQL이다.


