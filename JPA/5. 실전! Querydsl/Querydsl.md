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

# 기본 문법

# 중급 문법

# 실무 활용 - 순수 JPA와 Querydsl

# 실무 활용 - 스프링 데이터 JPA와 Querydsl

# 스프링 데이터 JPA가 제공하는 Querydsl 기능

# 스프링 부트 3.x(2.6 이상), Querydsl 5.0 지원 방법
