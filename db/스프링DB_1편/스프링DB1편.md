# JDBC 이해
## 프로젝트 생성

프로젝트 선택
- Project: Gradle - Groovy Project
- Language: Java
- Spring Boot: 2.7.16 
- Project Metadata
  - Group: hello
  - Artifact: jdbc
  - Name: jdbc
  - Package name: hello.jdbc
  - Packaging: Jar
  - Java: 11
- Dependencies
  - JDBC API
  - H2 Database
  - Lombok


실행시 콘솔에 `Started JdbcApplication` 로그가 보이면 성공이다.

## H2 데이터베이스 설정

H2 데이터베이스는 개발이나 테스트 용도로 사용하기 좋고 가볍고 편리한 DB이다. 그리고 SQL을 실행할 수 있는 웹 화면을 제공한다.

H2 실행<br>
/h2/bin/h2.sh 실행

### 테이블 생성하기

테이블 관리를 위해 프로젝트 루트에 `sql/schema.sql` 파일을 생성하자.

```sql
drop table memeber if exists cascade;
create table member
(
    member_id varchar(10),
    money     integer not null default 0,
    primary key (member_id)
);

insert into member(member_id, money) values ('hi1', 10000);
insert into member(member_id, money) values ('hi2', 20000);
```

방금 작성한 SQL을 H2 데이터베이스 웹 콘솔에 실행해서 `member`테이블을 생성한다. (이후 select 쿼리를 실행해서 저장한 데이터가 잘 나오는지 결과를 확인하자.)

## JDBC 이해

이제 본격적으로 JDBC에 대해서 알아보자

### JDBC 등장 이유

애플리케이션을 개발할 때 중요한 데이터는 데이터베이스에 보관한다. 보통은 아래와 같은 구조로 되어있다.

**클라이언트, 애플리케이션 서버, DB**<br>
<img src="./imgs/JDBC이해/클라이언트-애플리케이션_서버-DB.png"><br>

클라이언트가 애플리케이션 서버를 통해 데이터를 저장하거나 조회하면, 애플리케이션 서버는 다음 과정을 통해서 데이터베이스를 사용한다.

**애플리케이션 서버와 DB - 일반적인 사용법**<br>

<img src="./imgs/JDBC이해/애플리케이션_서버와_DB-일반적인_사용법.png"><br>

1. 커넥션 연결: 주로 TCP/IP를 사용해서 커넥션을 연결한다.
2. SQL 전달: 애플리케이션 서버는 DB가 이해할 수 있는 SQL을 연결된 커넥션을 통해 DB에 전달한다.
3. 결과 응답: DB는 전달된 SQL을 수행하고 그 결과를 응답한다. 애플리케이션 서버는 응답 결과를 활용한다.

여기서 잠시 과거로 돌아가보자. 과거에는 아래와 같은 문제가 있었다.

**애플리케이션 서버와 DB - DB 변경**<br>

<img src="./imgs/JDBC이해/애플리케이션_서버와_DB-DB_변경.png"><br>

- 문제는 각각의 데이터베이스마다 1) 커넥션을 연결하는 방법, 2) SQL을 전달하는 방법, 그리고 3) 결과를 응답받는 방법이 모두 다르다는 점이다. (참고로 관계형 데이터베이스는 수십개가 있다.)
- 여기에는 2가지 큰 문제가 있다.
  - 데이터베이스를 다른 종류의 데이터베이스로 변경하면 애플리케이션 서버에 개발된 데이터베이스 사용 코드도 함께 변경해야 한다.
  - 개발자가 각각의 데이터베이스마다 커넥션 연결, SQL 전달, 그리고 그 결과를 응답받는 방법을 새로 학습해야 한다.

이런 문제를 해결하기 위해 JDBC라는 자바 표준이 등장하였다.

### JDBC 표준 인터페이스

JDBC(Java Database Connectivity)는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API이다.<br>
JDBC는 데이터베이스에서 자료를 쿼리하거나 업데이트하는 방법을 제공한다.

(참고) JDBC 표준 인터페이스<br>

<img src="./imgs/JDBC이해/JDBC_표준_인터페이스.png"><br>

- 대표적으로 다음 3가지 기능을 표준 인터페이스로 정의해서 제공한다.
  - `java.sql.Connection` - 연결
  - `java.sql.Statement` - SQL을 담은 내용
  - `java.sql.ResultSet` - SQL 요청 응답

자바는 이렇게 표준 인터페이스를 정의해두었다. 이제부터 개발자는 이 표준 인터페이스만 사용해서 개발하면 된다.

그런데 인터페이스만 있다고해서 기능이 동작하지 않는다. 이 JDBC 인터페이스를 각각의 DB 벤더(회사)에서 자신들의 DB에 맞도록 구현해서 라이브러리로 제공하는데, 이것을 JDBC 드라이버라 한다. (예를 들어서 MySQL DB에 접근할 수 있는 것은 MySQL JDBC 드라이버라 하고, Oracle DB에 접근할 수 있는 것은 Oracle JDBC 드라이버라 한다.)

MySQL 드라이버 사용<br>

<img src="./imgs/JDBC이해/MySQL_드라이버_사용.png"><br>

Oracle 드라이버 사용 <br>

<img src="./imgs/JDBC이해/Oracle_드라이버_사용.png"><br>

#### 정리
- JDBC의 등장으로 다음 2가지 문제가 해결되었다.
  - 데이터베이스를 다른 종류의 데이터베이스로 변경하면 애플리케이션 서버의 데이터베이스 사용코드도 함께 변경해야하는 문제
    - 애플리케이션 로직은 이제 JDBC 표준 인터페이스에만 의존한다. 따라서 데이터베이스를 다른 종류의 데이터베이스로 변경하고 싶으면 JDBC 구현 라이브러리만 변경하면 된다. 따라서 다른 종류의 데이터베이스로 변경해도 애플리케이션 서버의 사용코드를 그대로 유지할 수 있다.
  - 개발자가 각각의 데이터베이스마다 커넥션 연결, SQL 전달, 그리고 그 결과를 응답받는 방법을 새로 학습해야 하는 문제
    - 개발자는 JDBC 표준 인터페이스 사용법만 학습하면 된다. 한번 배워두면 수십개의 데이터베이스에 모두 동일하게 적용할 수 있다.

> [!TIP]
> 표준화의 한계
>
> - JDBC의 등장으로 많은 것이 편리해졌지만, 각각의 데이터베이스마다 SQL, 데이터타입 등의 일부 사용법이 다르다, ANSI SQL이라는 표준이 있기는 하지만 일반적인 부분만 공통화했기 때문에 한계가 있다. 대표적으로 실무에서 기본으로 사용하는 페이징 SQL은 각각의 데이터베이스마다 사용법이 다르다. 결국 데이터베이스를 변경하면 JDBC 코드는 변경하지 않아도 되지만 SQL은 해당 데이터베이스에 맞도록 변경해야 한다.
>   - 참고로 JPA를 사용하면 이렇게 각각의 데이터베이스마다 다른 SQL을 정의해야 하는 문제도 많은 부분 해결할 수 있다.

## JDBC와 최신 데이터 접근 기술

JDBC는 1997년에 출시될 정도로 오래된 기술이고, 사용하는 방법도 복잡하다. 그래서 최근에는 JDBC를 직접 사용하기 보다는 JDBC를 직접 사용하기 보다는 JDBC를 편리하게 사용하는 다양한 기술이 존재한다. 대표적으로 SQL Mapper와 ORM 기술로 나눌 수 있다.

**JDBC 직접 사용**<br>
<img src="./imgs/JDBC이해/JDBC_직접_사용.png"><br>

**SQL Mapper**<br>
<img src="./imgs/JDBC이해/SQL_Mapper.png"><br>

- 장전: JDBC를 편리하게 사용하도록 도와준다.
  - SQL 응답 결과를 객체로 편리하게 변환해준다.
  - JDBC의 반복 코드를 제거해준다.
- 단점: 개발자가 SQL을 직접 작성해야 한다.
- 대표 기술: 스프링 JdbcTemplate, MyBatis

**ORM 기술**<br>

<img src="./imgs/JDBC이해/ORM_기술.png"><br>

- ORM은 객체를 관계형 데이터베이스 테이블과 매핑해주는 기술이다. 이 기술 덕분에 개발자는 반복적인 SQL을 직접 작성하지 않고, ORM 기술이 개발자 대신에 SQL을 동적으로 만들어 실행해준다. 추가로 각각의 데이터베이스마다 다른 SQL을 사용하는 문제도 중간에 해결해준다.
- 대표기술: JPA, 하이버네이트, 이클립스링크
- JPA는 자바 진영의 ORM 표준 인터페이스이고, 이것을 구현한 것으로 하이버네이트와 이클립스링크 등의 구현 기술이 있다.

**SQL Mapper vs ORM 기술**<br>

- SQL Mapper와 ORM 기술 둘 다 각각 장단점이 있다
- 쉽게 설명하자면 SQL Mapper는 SQL만 직접 작성하면 나머지 번거로운 일은 SQL Mapper가 대신 해결해준다. SQL Mapper는 SQL만 작성할 줄 알면 금방 배워서 사용할 수 있다.
- ORM기술은 SQL 자체를 작성하지 않아도 되어서 개발 생산성이 매우 높아진다. 편리한 반면에 쉬운 기술은 아니므로 실무에서 사용하려면 깊이있게 학습해야 한다.
- 나중에 뒷부분에서 다양한 데이터 접근 기술을 설명하는데, 그때 SQL Mapper인 JdbcTemplate과 MyBatis를 학습하고 코드로 활용해본다. 그리고 ORM의 대표 기술인 JPA도 학습하고 코드로 활용해본다. 이 과정을 통해서 각각의 기술들의 장단점을 파악하고, 이떤 기술을 언제 사용해야 하는지 자연스럽게 이해하게 될 것이다.

> [!IMPORTANT]
> 이런 기술들도 내부에서는 모두 JDBC를 사용한다. 따라서 JDBC를 직접 사용하지는 않더라도, JDBC가 어떻게 동작하는지 기본 원리를 알아두어야 한다. 그래야 해당 기술들을 더 깊이있게 이해할 수 있고, 무엇보다 문제가 발생했을 때 근본적인 문제를 찾아서 해결할 수 있다. **JDBC는 자바 개발자라면 꼭 알아두어야 하는 필수 기본 기술**이다.

## 데이터베이스 연결

애플리케이션과 데이터베이스를 연결해두자.<br>
(주의) H2 데이터베이스 서버를 먼저 실행해두자.

코드로 바로 적용해보자

**ConnectionConst생성**<br>
`src/main/java/hello/jdbc/connection/ConnectionConst.java`<br>
```java
package hello.jdbc.connection;

public class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";

}
```

- 데이터 베이스에 접속하는데 필요한 기본 정보를 편리하게 사용할 수 있도록 상수로 만들었다.
- 이제 JDBC를 사용해서 실제 데이터베이스에 연결하는 코드를 작성해보자.

**DBConnectionUtil생성**<br>
`src/main/java/hello/jdbc/connection/DBConnectionUtil.java`<br>
```java
package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    ConnectionConst.URL,
                    ConnectionConst.USERNAME,
                    ConnectionConst.PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());

            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
```
- 데이터베이스에 연결하려면 JDBC가 제공하는 `DriverManager.getConnection(...)`를 사용하면 된다. 이렇게 하면 라이브러리에 있는 데이터베이스 드라이버를 찾아서 해당 드라이버가 제공하는 커넥션을 반환해준다. 여기서는 H2 데이터베이스 드라이버가 작동해서 실제 데이터베이스와 커넥션을 맺고 그 결과를 반환해준다.
- (참고) External Libraries > h2database 라이브러리 > Driver 클래스 참고 (org.h2.Driver)

간단한 학습용 데스트 코드를 만들어서 실행해보자.

**DBConnectionUtilTest 생성**: test/java/hello/jdbc 패키지 내부에 connetcion 패키지를 생성하고, 그 내부에 DBConnectionUtilTest클래스를 생성하자

```java
package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

@Slf4j
class DBConnectionUtilTest {

    @Test
    void connection() {
        Connection connection = DBConnectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();
    }

}
```

실행 결과를 보면 `class=class org.h2.jdbc.JdbcConnection` 부분을 확인할 수 있다. <br>이것이 바로 H2데이터베이스 드라이버가 제공하는 H2 전용 커넥션이다. 물론 이 커넥션은 JDBC 표준 커넥션 인터페이스인 `java.sql.Connection`인터페이스를 구현하고 있다.

그러면 도대체 H2 데이터베이스 드라이버를 어떻게 찾는걸까?

**JDBC DriverManager 연결 이해**<br>
지금까지 코드로 확인해 본 과정을 좀 더 자세히 알아보자.

**JDBC 커넥션 인터페이스와 구현**<br>
<img src="./imgs/JDBC이해/JDBC_커넥션_인터페이스와_구현.png"><br>

- JDBC는 `java.sql.Connection`표준 커넥션 인터페이스를 정의한다.
- H2 데이터베이스 드라이버는 JDBC Connection 인터페이스를 구현한, `org.h2.jdbc.JdbcConnection`이라는 구현체를 제공한다.

**DriverManager 커넥션 요청 흐름**<br>
<img src="./imgs/JDBC이해/DriverManager_커넥션_요청_흐름.png"><br>

JDBC가 제공하는 `DriverManager`는 라이브러리에 등록된 DB 드라이버들을 관리하고, 커넥션을 획득하는 기능을 제공한다.
1. 애플리케이션 로직에서 커넥션이 필요하면 `DriverManager.getConnection()`을 호출한다
2. `DriverManager`는 라이브러리에 등록된 드라이버 목록을 자동으로 인식한다. 이 드라이버들에게 순서대로 다음 정보를 넘겨서 커넥션을 획득할 수 있는지 확인한다.
   - URL: 예) `jdbc:h2:tcp://localhost/~/test`
   - 이름, 비밀번호 등 접속에 필요한 추가 정보
   - 여기서 각각의 드라이버는 URL정보를 체크해서 본인이 처리할 수 있는 요청인지 확인한다. 얘를 들어서 URL이 `jdbc:h2`로 시작하면 이것은 h2 데이터베이스에 접근하기 위한 규칙이다. 따라서 H2 드라이버는 본인이 처리할 수 있으므로 실제 데이터베이스에 연결해서 커넥션을 획득하고 이 커넥션을 클라이언트에 반환한다. 반면에 URL이 `jdbc:h2`로 시작햇는데 MySQL 드라이버가 먼저 실행되면 이 경우 본인이 처리할 수 없다는 결과를 반환하게 되고, 다음 드라이버에게 순서가 넘어간다.
3. 이렇게 찾은 커넥션 구현체가 클라이언트에 반환된다.
   - 우리는 H2 데이터베이스 드라이버만 라이브러리에 등록했기 때문에 H2 드라이버가 제공하는 H2 커넥션을 제공받는다. 물론 H2 커넥션은 JDBC가 제공하는 `java.sql.Connection` 인터페이스를 구현하고 있다.

> [!TIP]
> H2 데이터베이스 드라이버 라이브러리<br>
> 
> runtimeOnly 'com.h2database:h2

## JDBC 개발 - 등록

이제 본격적으로 JDBC를 사용해서 애플리케이션을 개발해보자<br>
여기서는 JDBC를 사용해서 회원(`Member`)데이터를 데이터베이스에 관리하는 기능을 개발해보자.

코드로 적용해보자.

**Member 생성**<br>
`src/main/java/hello/jdbc/domain`<br>
```java
package hello.jdbc.domain;

import lombok.Data;

@Data
public class Member {

    private String memberId;
    private int money;

    public Member() {
    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}

```

회원의 ID와 해당 회원이 소지한 금액을 표현하는 단순한 클래스이다. 앞서 만들어둔 `member`테이블에 데이터를 저장하고 조회할 때 사용한다.

가장 먼저, JDBC를 사용해서 이렇게 만든 회원 객체를 데이터베이스에 저장해보자.

**MemberRepositoryV0생성 - 회원 등록**<br>
`src/main/java/hello/jdbc/repository/MemberRepositoryV0`<br>

```java
package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money)values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error");
            throw e;
        } finally {
            close(con, pstmt, null);

        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
```

### 코드 분석

**커넥션 획득**<br>
`getConnection()`: 이전에 만들어둔 `DBConnectionUtil`를 통해서 데이터베이스 커넥션을 획득한다.

**save() - SQL 전달**<br>
- `sql`: 데이터베이스에 전달한 SQL을 정의한다. 여기서는 데이터를 등록해야 하므로 `insert sql`을 정의했다.
- `con.prepareStatement(sql)`: 데이터베이스에 전달한 SQL과 파라미터로 전달할 데이터들을 준비한다.
  - `sql`: `insert into member(member_id, money) values(? , ?)"`
  - `pstmt.setString(1, member.getMemberId())`: SQL의 첫번째 `?`에 값을 지정한다. 문자이므로 `setString`을 사용한다.
  - `pstmt.setInt(2, member.getMoney())`: SQL의 두번째 `?`에 값을 저정한다. `Int`형 숫자이므로 `setInt`를 지정한다.
- `pstmt.executeUpdate()`: `Statement`를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다. 참고로 `executeUpdate()`은 `int`를 반환하는데, 영향받은 DB row수를 반환한다. 여기서는 하나의 row를 등록했으므로 1을 반환한다.
  - 참고) executeUpdate()<br> 
    ```java
        int executeUpdate() throws SQLException; 
    ```

**리소스 정리**<br>
- 쿼리를 실행하고 나면 리소스를 정의해야 한다. 여기서는 `Connection`, `PreparedStatement`를 사용했다.
- 리소스를 정리할 때는 항상 역순으로 해야한다. `Connection`을 먼저 획득하고 `Connection`을 통해 `PreparedStatement`를 만들었기 때문에, 리소스를 반환할때는 `PreparedStatement`를 먼저 종료하고, 그 다음에 `Connection`을 종료하면 된다.<br> (참고로 여기서 사용하지 않은 `ResultSet`은 결과를 조회할 때 사용한다. 조금 뒤에 조회 부분에서 알아보자.)

> [!WARNING]
> 리소스 정리는 꼭! 해주어야 한다. 따라서 예외가 발생하든, 하지 않든 항상 수행되어야 하므로 `finally` 구문에 주의해서 작성해야 한다. 만약 이 부분을 놓치게 되면 커넥션이 끊어지지 않고 계속 유지되는 문제가 발생할 수 있다. 이런 것을 리소스 누수라고 하는데, 걸과적으로 커넥션 부족으로 장애가 발행할 수 있다.


> [!TIP]
> `PreparedStatment`는 `Statement`의 자식타입인데, `?`를 통한 파라미터 바인딩을 가능하게 해준다. (참고로 SQL injection 공격을 예방하려면 `PreparedStatment`를 통한 파라미터 바인딩 방식을 사용해야 한다.)

### 테스트 코드

```java
package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV0", 10000);
        repository.save(member);
    }
}
```

정상적으로 실행이 되었고, 데이터베이스에서 `select * from member` 쿼리를 실행하면 데이터가 저장된 것을 확인할 수 있다.<br>
참고로 이 테스트는 반복해서 실행하면 PK 중복 오류가 발생한다. 이 경우 `delete from member`쿼리로 데이터를 삭제한 다음에 다시 실행하자.

## JDBC 개발 - 조회

이번에는 JDBC를 통해 이전에 저장한 데이터를 조회하는 기능을 개발해보자.

### MemberRepositoryV0 - 회원 조회 추가(findById)
```java
package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO member(member_id, money) VALUES (?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate();
            return member;

        }catch (SQLException e){
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,null); //rs,stmt,con이 전부 닫혀야 하기 때문에
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId =" + memberId);
            }
        } catch (SQLException e) {
            log.error("error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e){
                log.info("error",e);
            }
        }

        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error" , e);
            }
        }
        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error" , e);
            }
        }
    }

    private Connection getConnection(){
        return DBConnectionUtil.getConnection();
    }
}
```

### 코드 분석

**findById() - 쿼리 실행**<br>
- `sql`: 데이터 조회를 위한 select SQL을 준비한다.
- `rs = pstmt.executeQuery()`: 데이터를 변경할 때는 `executeUpdate()`를 사용하지만, 데이터를 조회할 때는 `executeQuery()`를 사용한다. `executeQuery()`는 결과를 `ResultSet`에 담아서 반환한다.

**ResultSet**<br>
- `ResultSet`은 다음과 같이 생긴 데이터 구조이다. 보통 select 쿼리의 결과가 순서대로 들어간다. (아래 ResultSet 결과 예시 참고)
  - 예를 들어서 `select member_id, money`라고 지정하면 `member_id`, `money`라는 이름으로 데이터가 저장된다.(참고로 `select *`을 사용하면 테이블의 모든 칼럼을 다 지정한다.)
- `ResultSet`내부에 있는 커서(`cursor`)를 이동해서 다음 데이터를 조회할 수 있다. (ResultSet은 내부에서ㅓ 커서라는 것이 있는데, 이것을 이동해서 다음 데이터를 조회할 수 있다.)
- `rs.next()`: 이것을 호출하면, 커서가 다음으로 이동한다. 참고로 최초의 커서는 데이터를 가리키고 있지 않기 때문에 `rs.next()`를 최초 한번은 호출해야 데이터를 조회할 수 있다.
  - `rs.next()`의 결과가 `true`면 커서의 이동 결과에 데이터가 있다는 뜻이다.
  - `rs.next()`의 결과가 `false`면 더이상 커서가 가리키는 데이터가 없다는 뜻이다.
- `rs.getString("member_id")`: 현재 커서가 가리키는 있는 위치의 `member_id`데이터를 `String`타입으로 반환한다.
- `rs.getInt("money")`: 현재 커서가 가리키고 있는 위치의 `money` 데이터를 `int`타입으로 반환한다.

**ResultSet 결과 예시**<br>
<img src="./imgs/JDBC이해/ResultSet_결과_예시.png"><br>

- 참고로 위 `ResultSet`의 결과 예시는 회원이 2명 조회디는 경우이다.
  - `1-1`에서 `rs.next()`를 호출한다.
  - `1-2`의 결과로 `cursor`가 다음으로 이동한다. 이 경우 `cursor`가 가리키는 데이터가 있으므로 `true`를 반환한다.
  - `2-1`에서 `rs.next()`를 호출한다.
  - `2-2`의 결과로 `cursor`가 다음으로 이동한다. 이 경우 `cursor`가 가리키는 데이터가 있으므로 `true`를 반환한다.
  - `3-1`에서 `rs.next()`를 호출한다.
  - `3-2`의 결과로 `cursor`가 다음으로 이동한다. 이 경우 `cursor`가 가리키는 데이터가 없으므로 `false`를 반환한다.
  - (참고) 예제의 `findById()`에서는 회원 하나를 조회하는 것이 목적이다. (SQL을 보면 Pk인 member_id를 항상 지정하는 것을 확인할 수 있다.) 따라서 조회 결과가 항상 1건이므로 `while`대신에 `if`를 사용한다.

### 테스트 코드

```java
package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {

        //save
        Member member = new Member("memberV0", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);
    }
}
```
중복으로 실행할 경우 PK 중복 오류가 발생한다. `delete from member`을 이용하자.

실행 결과 로그를 확인해보자.<br>
`MemberRepositoryV0Test -- findMember=Member(memberId=memberV0, money=10000)`

- 회원을 등록하고 그 결과를 바로 조회해서 확인해 보았다.
- 참고로 실행 결과에 `member` 객체의 참조 값이 아니라 실제 데이터가 보이는 이유는 롬복의 `@Data`가 `toString()`을 적절히 오버라이딩해서 보여주기 때문이다.
- `isEqualTo()`: `findMember.euals(member)`를 비교한다. 결과가 참인 이유는 롬복의 `@Data`는 해당 객체의 모든 필드를 사용하도록 `equals()`를 오버라이딩 하기 때문이다. (member == findMember와 같이 인스턴스 비교를 하게되면 서로 다르기 때문에 false이다.)

## JDBC 개발 - 수정, 삭제

수정과 삭제는 등록과 비슷하다. 등록, 수정, 삭제처럼 데이터를 변경하는 쿼리는 `executeUpdate()`를 사용하면 된다.

### MemberRepositoryV0 - 회원 수정 추가(update)

```java
public void update(String memberId, int money) throws SQLException {
    String sql = "update member set money=? where member_id=?";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {
        con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, money);
        pstmt.setString(2, memberId);

        int resultSize = pstmt.executeUpdate();
        log.info("resultSize={}", resultSize);
    } catch (SQLException e) {
        log.error("error", e);
        throw e;
    } finally {
        close(con, pstmt, null);
    }
}
```

`executeUpdate()`는 쿼리를 실행하고 영향받은 row수를 반환한다. 여기서는 하나의 데이터만 변경하기 때문에 결과로 1이 반환된다. 만약 회원이 100명이고, 모든 회원 데이터를 한번에 수정하는 update sql을 실행하면 결과는 100이 된다.

### 회원 수정 테스트

```java
//update(money : 10000 -> 20000)
repository.update(member.getMemberId(), 20000);
Member updatedMember = repository.findById(member.getMemberId());
assertThat(updatedMember.getMoney()).isEqualTo(20000);
```

### MemberRepositoryV0 - 회원 삭제 추가(delete)

```java
public void delete(String memberId) throws SQLException {
    String sql = "delete from member where member_id=?";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {
        con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, memberId);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        log.error("db error", e);
        throw e;
    } finally {
        close(con, pstmt, null);
    }
}
```

### 회원 삭제 테스트 코드

```java
repository.delete(member.getMemberId());
Assertions.assertThatThrownBy(
        () -> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
```

# 커넥션 풀과 데이터소스 이해

## 커넥션 풀 이해

먼저 커넥션 풀에 대해서 알아보자.

**데이터베이스 커넥션을 매번 획득**<br>

<img src="./imgs/커넥션_풀과_데이터소스_이해/데이터베이스_커넥션_획득.png"><br>

데이터베이스 커넥션을 획득할 때는 다음과 같은 복잡한 과정을 거친다.
1. 애플리케이션 로직은 DB 드라이버를 통해 커넥션을 조회한다.
2. DB드라이버는 DB와 `TCP/IP`커넥션을 연결한다. 물론 이 과정에서 3way handshake와 같은 `TCP/IP` 연결을 위한 네트워크 동작이 발생한다.
3. DB드라이버는 `TCP/IP` 커넥션이 연결되면 ID, PW와 기타 부가정보를 DB에 전달한다
4. DB는 ID, PW를 통해 내부 인증을 완료하고, 내부에 DB세션을 생성한다,
5. DB는 커넥션 생성이 완료되었다는 응답을 보낸다.
6. DB 드라이버는 커넥션 객체를 생성해서 클라이언트에 반환한다.

이렇게 커넥션을 새로 만드는 것은, 과정도 복잡하고 시간도 많이 소모되는 일이다. DB는 물론이고 애플리케이션 서버에서도 `TCP/IP` 커넥션을 새로 생성하기 위한 리소스를 매번 사용해야 한다.<br>
진짜 문제는 고객이 애플리케이션을 사용할 때, SQL을 실행하는 시간 뿐만 아니라 커넥션을 새로 만드는 시간이 추가되기 때문에 결과적으로 응답 속도에 영향을 준다. 이것은 사용자에게 좋지 않은 경험을 줄 수 있다.(참고, 데이터베이스마다 커넥션을 생성하는 시간은 다르다. 시스템 상황마다 다르지만 MySQL계열은 수 ms정도로 매우 빨리 커넥션을 확보할 수 있다. 반면에 수십 밀리초 이상 걸리는 데이터베이스들도 있다.)

이런 문제를 한번에 해결하는 아이디어가 바로 커넥션을 미리 생성해두고 사용하는 커넥션 풀이라는 방법이다. 커넥션 풀은 이름 그대로 커넥션을 관리하는 풀(수영장 풀을 상상하면 된다)이다.

**커넥션 풀 초기화**<br>

<img src="./imgs/커넥션_풀과_데이터소스_이해/커넥션_풀_초기화.png"><br>

애플리케이션을 시작하는 시점에, 커넥션 풀은 필요한 만큼 커넥션을 미리 확보해서 풀에 보관한다. 보통 얼마나 보관할 지는 서비스의 특징과 서버 스펙에 따라 다르지만 기본 값은 보통 10개이다.

**커넥션 풀의 연결 상태**<br>

<img src="./imgs/커넥션_풀과_데이터소스_이해/커넥션_풀의_연결_상태.png"><br>

커넥션 풀에 들어있는 커넥션은 TCP/IP로 DB와 커넥션이 연결되어 있는 상태이기 때문에 언제든지 즉시 SQL을 DB에 전달할 수 있다.

**커넥션 풀 사용 1**<br>

<img src="./imgs/커넥션_풀과_데이터소스_이해/커넥션_풀_사용1.png"><br>

- 애플리케이션 로직에서 이제는 DB 드라이버를 통해서 새로운 커넥션을 획득하는 것이 아니다.
- 이제는 커넥션 풀을 통해 이미 생성되어 있는 커넥션을 객체 참조로 그냥 가져다 쓰기만 하면 된다.
- 커넥션 풀에 커넥션을 요청하면 커넥션 풀은 자신이 가지고 있는 커넥션 중에 하나를 반환한다.

**커넥션 풀 사용2**<br>

<img src="./imgs/커넥션_풀과_데이터소스_이해/커넥션_풀_사용2.png"><br>

- 애플리케이션 로직은 커넥션 풀에서 받은 커넥션을 사용해서 SQL을 데이터베이스에 전달하고 그 결과를 받아서 처리한다.
- 커넥션을 모두 사용하고 나면 이제는 커넥션을 종료하는 것이 아니라, 다음에 다시 사용할 수 있도록 해당 커넥션을 그래로 커넥션 풀에 반환하면 된다. 여기서 주의할 점은 커넥션을 종료하는 것이 아니라 커넥션이 살아있는 상태로 커넥션 풀에 반환해야 한다는 점이다.

> [!NOTE]
> - 적절한 커넥션 풀 숫자는 서비스의 특징과 애플리케이션 서버 스펙, DB서버 스펙에 따라 다르기 때문에 성능 테스트를 통해 정해야 한다.
> - 커넥션 풀은 서버당 최대 커넥션 수를 제한할 수 있다. 따라서 DB에 무한정 연결이 생성되는 것을 막아주어서 DB를 보호하는 효과도 있다.
> - 이런 커넥션 풀은 얻는 이점이 매우 크기 때문에 **실무에서는 항상 기본으로 사용**한다.
> - 커넥션 풀은 개념적으로 단순해서 직접 구현할 수도 있지만, 사용도 편리하고 성능도 뛰어난 오픈소스 커넥션 풀이 많기 때문에 오픈소스를 사용하는 것이 좋다.
> - 대표적인 커넥션 풀 오픈소스는 `commons-dbcp2`, `tomcat-jdbc pool`, `HikariCP`등이 있다
> - 성능과 사용의 편리한 측면서에 최근에는 `hikariCP`를 주로 사용한다. 스프링 부트 2.0부터는 기본 커넥션 풀로 `hikariCP`를 제공한다. 성늘, 사용의 편리함, 안전성 측면에서 이미 검증이 되었기 때문에 커넥션 풀을 사용할 때는 고민할 것 없이 `hikariCP`를 사용하면 된다. 실무에서도 레거시 프로젝트가 이닌 이상 대부분 `hikariCP`를 사용한다.

## DataSource 이해

커넥션을 얻는 방법은 앞서 학습한 JDBC `DriverManager`를 직접 사용하거나, 커넥션 풀을 사용하는 등 다양한 방법이 존재한다.

**커넥션을 획득하는 다양한 방법**<br>

<img src="./imgs/커넥션_풀과_데이터소스_이해/커넥션을_획득하는_다양한_방법.png"><br>

직접 DriverManager를 통해서 항상 새로운 커넥션을 생성할 수도 있고, 커넥션 풀을 통해서 풀에 있는 커넥션을 사용할 수도 있다.

**DriverManager를 통해 커넥션 획득**<br>

<img src="./imgs/커넥션_풀과_데이터소스_이해/DriverManager를_통해_커넥션_획득.png"><br>

- `DriverManager`를 통해서 커넥션을 획득하게 되면, 항상 신규 커넥션이 생성되고 그것을 반환받게 된다.
- 앞서 JDBC로 개발한 애플리케이처럼 `DriverManager`를 통해서 커넥현을 획득하다가, 커넥션 풀을 사용하는 방법으로 변경하려면 어떻게 해야할까?

**DriverManager를 통해 커넥션 획득하려다 커넧션 풀로 변경시 문제**<br>

<img src="./imgs/커넥션_풀과_데이터소스_이해/DriverManager를_통해_커넥션_획득하다가_커넥션_풀로_변경시_문제.png"><br>

- 예를 들어서, 애플리케이션 로직에서 `DriverManager`를 사용해서 커넥션을 획득하다가 `HikariCP`같은 커넥션 풀을 사용하도록 변경하면 커넥션을 획득하는 애플리케이션 코드도 함께 변경해야 한다. 의존관계가 `DriverManager`에서 `HikariCP`로 변경되기 때문이다. 물론 둘의 사용법도 조금씩 다를 것이다.

**커넥션을 획득하는 방법을 추상화**<br>
(-> 커넥션을 어떻게 얻을것인가? 그 방법을 추상화)<br>

<img src="./imgs/커넥션_풀과_데이터소스_이해/커넥션을_획득하는_방법을_추상화.png"><br>

- 자바에서는 이런 문제를 해결하기 위해 `javax.sql.DataSource`라는 인터페이스를 제공한다.
- `DataSource`는 **커넥션을 획득하는 방법을 추상화**하는 인터페이스이다.
- **이 인터페이스의 핵심 기능은 커넥션 조회 하나이다.** (다른 일부 기능도 있지만 크게 중요하지 않다.)

> [!NOTE]
> - 대부분의 커넥션 풀은 `DataSource` 인터페이스를 이미 구현해두었다. 따라서 개발자는 `DBCP2 커넥션 풀`, `HikariCP 커넥션 풀`의 코드를 직접 의존하는 것이 아니라, `DataSource`인터페이스에만 의존하도록 애플리케이션 로직을 작성하면 된다.
> - 커넥션 풀 구현 기술을 변경하고 싶으면 해당 구현체로 갈아끼우기만 하면 된다.
> - `DriverManager`는 `DataSource`인터페이스를 사용하지 않는다. 따라서 `DriverManager`는 직접 사용해야 한다. 따라서 `DriverManager`를 사용하다가 `DataSource`기반의 커넥션 풀을 사용하도록 변경하면 관련 코드를 다 고쳐야 한다. 이런 문제를 해결하기 위해 스프링은 `DriverManager`도 `DataSource`를 통해서 사용할 수 있도록 `DriverManagerDataSource`라는 `DataSource`를 구현한 클래스를 제공한다. (`DriverManagerDataSource`를 사용하면, 내부에서 `DriverManager`를 통해 커넥션을 항상 새로 생성하여 반환해준다.)
> - 자바는 `DataSource`를 통해 커넥션을 획득하는 방법을 추상화했다. 이제 애플리케이션 로직은 `DataSource`인터페이스에만 의존하면 된다. 덕분에 `DriverManagerDataSource`를 통해서 `DriverManager`를 사용하다가 커넥션 풀을 사용하도록 코드를 변경해도 애플리케이션 로직은 변경하지 않아도 된다.

## DataSource 예제1 - DriverManager

예제를 통해 `DataSource`를 알아보자.

먼저 기존에 개발했던 `DriverManager`를 통해서 커넥션을 획득하는 방법을 다시 한 번 확인해보자.

### ConnectionTest - 드라이버 매니저
`test/java/hello/jdbc/connection/ConnectinoTest`<br>
```java
package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(
                ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD
        );
        Connection con2 = DriverManager.getConnection(
                ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD
        );

        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}
```

이번에는 스프링이 제공하는 `DataSource`가 적용된 `DriverManager`인 `DriverManagerDataSource`를 사용해보자.

### ConnectionTest - 데이터소스 드라이버 매니저 추가

```java
@Test
void dataSourceDriverManager() throws SQLException {
    //DriverManagerDataSource - 항상 새로운 커넥션을 획득
    DriverManagerDataSource dataSource = new DriverManagerDataSource(
            ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD
    );

    useDataSource(dataSource);
}

private void useDataSource(DataSource dataSource) throws SQLException {
    Connection con1 = dataSource.getConnection();
    Connection con2 = dataSource.getConnection();

    log.info("connection={}, class={}", con1, con1.getClass());
    log.info("connection={}, class={}", con2, con2.getClass());
}
```

기존 코드와 비슷하지만 `DriverManagerDataSource`는 `DataSource`를 통해서 커넥션을 획득할 수 있다. 참고로 `DriverManagerDataSource`는 스프링이 제공하는 코드이다.<br>

> [!TIP]
> DriverManagerDataSource에서 남겨주는 로그를 보자. "Creating new JDBC DriverManager Connection to [jdbc:h2:tcp://localhost/~/test]" -> JDBC DriverManager 를 통해서 jdbc:h2:tcp://localhost/~/test에 접근해서 커넥션을 만들었음을 알 수 있다.

### 파라미터 차이

기존 `DriverManager`를 통해서 커넥션을 획득하는 방법과 `DataSource`를 통해서 커넥션을 획득하는 방법에는 큰 차이가 있다.

**DriverManager**<br>
```java
Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
```

**Datasource**<br>
```java
    @Test
    void dataSourceDriverManager() throws SQLException {
        //DriverManagerDataSource - 항상 새로운 커넥션을 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                URL, USERNAME, PASSWORD
        );

        useDataSource(dataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();

        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
```

`DriverManager`는 커넥션을 획득할 때 마다 `URL`, `USERNAME`, `PASSWORD` 같은 파라미터를 계속 전달해야 한다. 반면에 `DataSource`를 사용하는 방식은, 처음 객체를 생성할 때만 필요한 파라미터를 남겨두고, 커넥션을 획득할 때는 단순히 `dataSource.getConnection()`만 호출하면 된다.

### 설정과 사용의 분리

- **설정**: `DataSource`를 만들고 필요한 속성들을 사용해서 `URL`, `USERNAME`, `PASSWORD`같은 부분을 입력하는 것을 말한다. 이렇게 설정과 관련된 속성들은 한 곳에 있는 것이 향후 변경에 더 유연하게 대처할 수 있다.
- **사용**: 설정은 신경쓰지 않고, `DataSource`의 `getConnection()`만 호출해서 사용하면 된다.

### 설정과 사용의 분리 설명

- 이 부분(설정과 사용의 분리)이 작아보이지만 큰 차이를 만들어내는데, 필요한 데이터를 `DataSource`가 만들어지는 시점에 미리 다 넣어두게 되면, `DataSource`를 사용하는 곳에서는 `dataSource.getConnection()`만 호출하면 되므로, `URL`, `USERNAME`, `PASSWORD`같은 속성들에 의존하지 않아도 된다. 그냥 `DataSource`만 주입받아서 `getConnection()`만 호출하면 된다.
- 쉽게 이야기해서 리포지토리(Repository)는 `DataSource`만 의존하고, 이런 속성들은 몰라도 된다.
- 애플리케이션을 개발해보면 보통 설정은 한 곳에서 하지만, 사용은 수 많은 곳에서 하게 된다.
- 덕분에 객체를 설정하는 부분과, 사용하는 부분을 좀 더 명확하게 분리할 수 있다.

## DataSource 예제2 - 커넥션 풀

이번에는 `DataSource`를 통해 커넥션 풀을 사용하는 예제를 알아보자.

### ConnectionTest - 데이터소스 풀 추가

```java
@Test
void dataSourceConnectionPool() throws SQLException, InterruptedException {
    //커넥션 풀링: HikariProxyConnection(Proxy) -> JdbcConnection(Target)
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);
    dataSource.setMaximumPoolSize(10); //default: 10 (DEFAULT_POOL_SIZE)
    dataSource.setPoolName("MyPool");

    useDataSource(dataSource);
    Thread.sleep(1000); //커넥션 풀에서 커넥션 생성 시간 대기
}

private void useDataSource(DataSource dataSource) throws SQLException {
    Connection con1 = dataSource.getConnection();
    Connection con2 = dataSource.getConnection();

    log.info("connection={}, class={}", con1, con1.getClass());
    log.info("connection={}, class={}", con2, con2.getClass());
}
```

- HikariCP 커넥션 풀을 사용했다. `HikariDataSource`는 `DataSource`인터페이스를 구현하고 있다.
- 커넥션 풀 최대 사이즈를 10으로 지정하고, 풀의 이름을 `MyPool`이라고 지정했다.
- 커넥션 풀에서 커넥션을 생성하는 작업은 애플리케이션 실행 속도에 영향을 주지 않기 위해 별도의 쓰레드에서 작동해야 한다. 별도의 쓰레드에서 동작하기 때문에 테스트가 먼저 종료되어 버린다. 예제처럼 `Thread.sleep`을 통해 대기 시간을 주어야 쓰레드 풀에 커넥션이 생성되는 로그를 확안할 수 있다.

### 만약 10개 이상 커넥션을 획득하면 어떻게 될까?

실행할 경우 `MyPool - Pool stats (total=10, active=10, idle=0, waiting=1)`로그를 보면, 커넥션 풀의 10개 커넥션이 모두 사용중이고, 1개가 대기상태인 것을 확인할 수 있따. 그리고 이후 30초 뒤에 SQLTransientConnectionException 예외가 발생하여 오류가 발생한 것을 확인할 수 있다.

### 실행결과를 분석하자

- **HikariConfig**
  - `HikariCP`관련 설정을 확인할 수 있다. 풀의 이름(`MyPool`)과 최대 풀 수 (`10`)등을 확인할 수 있다.
- **MyPool connection adder**
  - 별도의 쓰레드를 사용해서 커넥션 풀에 커넥션을 채우고 있는 것을 확인할 수 있다. 이 쓰레드는 커넥션 풀에 커녁션을 최대 풀 수 (`10`)까지 채운다.
  - 그렇다면 왜 별도의 쓰레드를 사용해서 커넥션 풀에 커넥션을 채우는 것일까?
    - 커넥션 풀에 커넥션을 채우는 것은 상대적으로 오래걸리는 일이다. 애플리케이션을 실행할 때 커넥션 풀을 채울 때 까지 마냥 대기하고 있다면 애플리케이션 실행 시간이 늦어진다. 따라서 이렇게 별도의 쓰레드를 사용해서 커넥션 풀을 채워야 애플리케이션 실행 시간에 영향을 주지 않는다.
- **커넥션 풀에서 커넥션 획득**
  - 커넥션 풀에서 커넥션을 획득하고 그 결과를 출력했다. 여기선는 커넥션 풀에서 커넥션을 2개 획득하고 반환하지는 않았다. 따라서 풀에 있는 10개의 커넥션 중에 2개를 애플리케이션이 가지고 있는 상태이다. 그래서 마지막 로그를 보면 사용중인 커넥션 `active=2`, 풀에서 대기 상태인 커넥션 `idle=8`을 확인할 수 있다.(`Mypool - After adding stats(total=10, active=2, idle=8, waiting=0)`)

## DataSource 적용

이번에는 `DataSource`를 애플리케이션에 적용해보자.

학습시 참고 잘료도 기존 코드는 그대로 유지하기 우해, 기존 코드를 복사해서 새로 만들자.

- `MemberRepositoryV0` -> `MemberRepositoryV1`
- `MemeberRepositoryV0Test` -> `MemeberRepositoryV1Test`

### MemberRepositoryV1 - 수정

```java
package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException { ... }

    public Member findById(String memberId) throws SQLException { ... }

    public void update(String memberId, int money) throws SQLException { ... }

    public void delete(String memberId) throws SQLException { ... }


    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
```

- `DataSource` 의존관계 주입
  - 외부에서 `DataSource`를 주입 받아서 사용한다. 이제 직접 만든 `DBConnectionUtil`을 사용하지 않아도 된다.
  - `DataSource`는 표준 인터페이스이기 때문에 `DriverManagerDataSource`에서 `HikariDataSource`로 변경되어도 해당 코드를 변경하지 않아도 된다. (둘 다 표준 인터페이스인 `DataSource`를 구현하고 있기 때문)
- `JdbcUtils` 편의 메서드
  - 스프링은 JDBC를 편리하게 다룰 수 있는 `JdbcUtils`라는 편의 메서드를 제공한다.
  - `JdbcUtils`을 사용하면 커넥션을 좀 더 편리하게 닫을 수 있다.

### MemberRepositoryV1Test - 수정 (DriverManagerDataSource 사용)

```java
package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                URL, USERNAME, PASSWORD
        );
        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException {

        //save
        System.out.println("-----------save-----------");
        Member member = new Member("memberV0", 10000);
        repository.save(member);

        //findById
        System.out.println("-----------findById-----------");
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);

        //update(money : 10000 -> 20000)
        System.out.println("-----------update-----------");
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        log.info("memberid={}", member.getMemberId());
        log.info("money={}", member.getMoney());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        System.out.println("-----------delete-----------");
        repository.delete(member.getMemberId());
        Assertions.assertThatThrownBy(
                () -> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }
}
```

- (참고) @BeforeEach: 각 테스트가 실행되기 직전에 호출된다.
- `MemberRepositoryV1`은 `DataSource` 의존관계 주입이 필요하다.

### MemberRepositoryV1Test - 수정 (HikariDataSource 사용)

```java
@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        //커넥션 풀링: HikariProxyConnection -> JdbcConnection
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);

        ...
    }
```

- 커넥션 풀 사용시 `conn0`커넥션이 재사용된 것을 확인할 수 있다. (참고로 커넥션 close시, 커넥션 풀인 경우에는 커넥션을 닫는게 아니라 커넥션 풀에 반환한다.)
- 테스트는 순서대로 실핻되기 때문에 커넥션을 사용하고 다시 돌려주는 것을 반복한다. 따라스 `conn0`만 사용된다.(참고. 커넥션을 꺼낼 때는 처음에 있는 사용 가능한 것을 꺼낸다.)
- 웹 애플리케이션에 동시에 여러 요청이 들어오면 여러 쓰레드에서 커넥션 풀의 커넥션을 다양하게 가져가는 상황을 확인할 수 있다.

> [!NOTE]
> HikariProxyConnection 객체 인스턴스 주소는 다 다르다. Hikari에서는 커넥션 풀에서 커넥션을 반환해줄 때, HikariProxyConnection 이라는 객체를 생성하고, 거기에 실제 커넥션을 wrapping해서 반환한다. (따라서 HikariProxyConnection 객체 인스턴스 주소는 다 다르지만, 그 안에 실제 커넥션은 동일하다.)<br>
> **핵심은 커넥션 풀을 사용하면, 같은 커넥션을 재사용할 수 있다는 점이다.**

> [!TIP]
> **DI**<br>
> `DriverManagerDataSource` -> `HikariDataSource`로 변경해도 `MemberRepositoryV1`의 코드는 전혀 변경하지 않아도 된다. `MemberRepositoryV1`는 `DataSource`인터페이에만 의존하기 때문이다. 이것이 `DataSource`를 사용하는 장점이다. (`MemberRepositoryV1`는 `DataSource` 인터페이스에만 의존한다. 구현체가 바뀌더라도 코드를 변경하지 않아도 된다.)

# 트랙잭션 이해

## 트랜젝션 - 개념 이해

데이터를 저장할 때 단순히 파일에 저장해도 되는데, 데이터베이스에 저장하는 이유는 무엇일까><br>
여러가지 이유가 있지만, 가장 대표적인 이유는 바로 데이터베이스는 트랜잭션이라는 개념을 지원하기 때문이다.

트랜잭션을 이름 그래로 번역하면 거리래는 뜻이다. 이것을 쉽게 풀어서 이야기하면, 데이터베이스에서 트랜잭션은 하나의 거래를 안전하게 처리하도록 보장해주는 것을 뜻한다. 그런데 하나의 거래를 안전하게 처리하려면 생각보다 고려해야할 점이 많다. 예를들어서 A의 5000원을 B에게 계좌이체한다고 생각해보자. A의 잔고를 5000원 감소하고, B의 잔고를 5000원 증가해야 한다.

**5000원 계좌 이체**<br>
("5000원을 계좌이체한다."라는 이 하나의 트랜잭션, 즉 이 하나의 거래는 사실 아리 2개로 이루어져있따.)

- A의 잔고를 5000원 감소
- B의 잔고를 5000원 증가

계좌이체라는 거래는 이렇게 2가지 작업이 합쳐져서 하나의 작업처럼 동작해야 한다.<br>
만약 1번은 성공했는데 2번에서 시스템에 문제가 발생하면, 계좌이체는 실패고, A의 잔고만 5000원 감소하는 심각한 문제가 발생한다.

데이터베이스가 제공하는 트랜잭션이라는 기능을 사용하면 1번과 2번 둘다 함께 성공해야 저장하고, 중간에 하나라도 실패하면 거개 전의 상태로 돌아갈 수 있다. 만약 1번은 성공했는데 2번에서 시스템에 문제가 발생하면 계좌 이체는 실패하고, 거래 전의 상태로 완전히 돌아갈 수 있다. 결과적으로 A의 잔고가 감소하지 않는다.

모든 작업이 성공해서 데이터베이스에 정상 반영하는 것을 커밋(`Commit`)이라 하고, 작업 중 하나라도 실패해서 거래 이전으로 되돌리는 것을 롤백(`Rollback`)이라 한다.

### 트랜잭션 ACID

트랙잭션은 ACID라 하는 원자성(Atomicity), 일관성(Consistency), 격리성(Isolation), 지속성(Durability)를 보장해야 한다.

- **원자성**: 트랜잭션 내에서 실행한 작업들은 마치 하나의 작업인 것처럼 모두 성공 하거나 모두 실패해야 한다.
- **일관성**: 모든 트랜잭션은 일관성이 있는 데이터베이스 상태를 유지해야 한다. 예를 들어 데이터베이스에서 정한 무결성 제약 조건을 항상 만족해야 한다.
- **격리성**: 동시에 실행되는 트랜잭션이 서로에게 영향을 미치지 않도록 격리한다. 예를 들어 동시에 같은 데이터를 수정하지 못하도록 해야한다. 격리성은 동시성과 관련된 성능 이슈로 인해 트랜잭션 격리 수준을 선택할 수 있다.
- **지속성**: 트랜잭션을 성공적으로 끝내면 그 결과가 항상 기록되어야 한다. 중간에 시스템에 문제가 발생해도 데이터베이스 로그들을 사용해서 성공한 트랜잭션 내용을 복구해야 한다.

트랜잭션은 원자성, 일관성, 지속성을 보장한다. 문제는 격리성인데 트랜잭션 간에 격리성을 완벽히 보장하려면 트랜잭션을 거의 순서대로 실행해야 한다. 이렇게 하면 동시 처리 성능이 매우 나빠진다. 이런 문제로 인해 ANSI표준은 트랜잭션의 격리수준을 4단계로 나누어 정의했다.

### 트랜잭션 격리 수준 - Isoloation level

- READ UNCOMMITED(커밋되지 않은 읽기)
- READ COMMITTED(커밋된 읽기)
- REPEATABLE READ(반복 가능한 읽기)
- SERIALIZABLE(직렬화 가능)

> [!TIP]
> 여기선느 일반적으로 많이 사용하는 READ COMMITTED(커밋된 읽기) 트랜잭션 격리 수준을 기준으로 설명한다.

## 데이터베이스 연결 구조와 DB 세션

트랜잭션을 더 자세히 이해하기 위해서, 데이터베이스 서버 연결 구조와 DB세션에 대해 알아보자.

**데이터베이스 연결구조1**<br>

<img src="./imgs/트랜잭션_이해/데이터베이스_연결구조1.png"><br>

- 사용자는 웬 애플리케이션 서버(WAS)나 DB 접근 툴 같은 클라이언트를 사용해서 데이터베이스 서버에 접근할 수 있다. 클라이언트는 데이터베이스 서버에 연결을 요청하고 커넥션을 맺게 된다. 이떼 데이터베이스 서버는 내부에 세션이라는 것을 만든다. 그리고 앞으로 해당 커넥션을 통한 모든 요청은 이 세션을 통해 실행하게 된다.
- 쉽게 이야기해서 개발자가 클라이언트를 통해 SQL을 전달하면 현재 커넥션에 연결된 세션이 SQL을 실행한다.
- 세션은 트랜잭션을 시작하고, 커밋 또는 롤백을 통해 트랜잭션을 종룧나다. 그리고 이후에 새로운 트랜잭션을 다시 시작할 수 있다.
- 사용자가 커넥션을 닫거나, 또는 DBA(DB 관리자)가 세션을 강제로 종료하면 세션은 종료된다.

**데이터베이스 연결구조2**<br>

<img src="./imgs/트랜잭션_이해/데이터베이스_연결구조2.png"><br>

커넥션 푸링 10개의 커넥션을 생성하면, 세션도 10개 만들어진다.

## 트랜잭션 - DB 예제1 - 개념이해

트랜잭션 동작을 예제를 통해 확인하자.<br>
이번 내용에서는 먼저 트랜잭션의 동작 개념의 전체 그림을 이해하는데 집중하자.<br>
(다음 내용에서부터 실제 SQL을 실행하면서 실습해보겠다.)

참고로 지금부터 설명하는 내용은 트랜잭션 개념의 이해를 돕기 위해 예시로 설명하는 것이다.<br>
구체적인 실제 구현 방식은 데이터베이스마다 다르다.

### 트랜잭션 사용법

- 데이터 변경 쿼리를 실행하고 데이터베이스에 그 결과를 반영하려면 커밋 명령어인 `commit`을 호출하고, 결과를 반영하고 싶지 않으면 롤백 명령인 `rollback`을 호출하면 된다.
- **커밋을 호출하기 전까지는 임시로 데이터를 저장**하는 것이다. 따라서 해당 트랜잭션을 시작한 세션(사용자)에게만 변경 데이터가 보이고 다른 세션(사용자)에게는 변경 데이터가 보이지 않는다.
- 등록, 수정, 삭제 모두 같은 원리로 동작한다. 앞으로는 등록, 수정, 삭제를 간단히 **변경**이라는 단어로 표현하겠다.

### 기본 데이터

<img src="./imgs/트랜잭션_이해/기본데이터.png"><br>

세션1, 세션2 둘다 가운데 있는 기본 테이블을 조회하면 해당 데이터가 그대로 조회된다.

**세션1 - 신규 데이터 추가**<br>

<img src="./imgs/트랜잭션_이해/세션1-신규_데이터_추가.png"><br>

- 세션1은 트랜잭션을 시작하고 신규 회원 1, 신규회원2를 DB에 추가했다. 아직 커밋은 하지 않은 상태이다.
- 그러면 새로운 데이터는 임시 상태로 저장된다.(위 그림에서 상태는 우리가 눈으로 볼 수 있는 것은 아니고 DB내부에서 관리하는 것이다. 참고로 이에 대한 구현은 데이터베이스마다 다르다.)
- 세션1은 `select`쿼리를 실행해서 본인이 입력한 신규 회원1, 신규 회원2를 조회할 수 있다.
- 세션2는 `select`쿼리를 실행해도 신규 회원들을 조회할 수 없다. 왜냐햐면 세션1이 아직 커밋을 하지 않았기 때문에다. (커밋을 해야 실제 데이터베이스에 반영됨)

### 커밋하지 않은 데이터를 다른 곳에서 조회할 수 있으면 어떤 문제가 발생할까?

- 예를 들어서 커밋하지 않는 데이터가 보인다면, 세션2는 데이터를 조회했을 때 신규 회원1, 2가 보일것이다. 따라서 신규 회원1, 신규 회원2가 있다고 가정하고 어떤 로직을 수행할 수 있다. 그런데 세션1이 롤백을 수행하면 신규 회원1, 신규 회원2의 데이터가 사라지게 된다. 따라서 데이터 정합성에 큰 문제가 발생한다.
- 세션2에서 세션1이 아직 커밋하지 않은 변경 데이터가 보인다면, 세션1이 롤백했을 때 심각한 문제가 발생할 수 있다. 따라서 커밋 전의 데이터는 다른 세션에서 보이지 않는다.

### 세션 1 - 신규 데이터 추가 후 commit

<img src="./imgs/트랜잭션_이해/세션1-신규_데이터_추가_후_commit.png"><br>

- 세션1이 신규 데이터를 추가한 후에 `commit`을 호출했다.
- `commit`으로 새로운 데이터가 실제 데이터베이스에 반영된다. 데이터의 상태도 임시 -> 완료로 변경되었다.
- 이제 다른 세션에서도 회원 테이블을 조회하면 신규 회원들을 확인할 수 있다.

### 세션 1 - 신규 데이터 추가 후 rollback

<img src="./imgs/트랜잭션_이해/세션1-신규_데이터_추가_후_rollback.png"><br>

- 세션1이 신규 데이터를 추가한 후에 `commit`대신에 `rollback`을 호출했다.
- 세션1이 데이터베이스에 반영한 모든 데이터가 처음 상태로 복구된다.
- 수정하거나 삭제한 데이터도 `rollback`을 호출하면 모두 트랜잭션을 시작하기 직전의 상태로 복구된다.

## 트랜잭션 - DB 예제2 - 자동 커밋, 수동 커밋

이전에 설명한 예제를 돌려보기 전에 먼저 자동 커밋, 수동 커밋에 대해 알아보자.<br>
참고로 예제에 사용되는 스키마는 이전에 실습했던 것과 동일하다. (`sql/schema.sql`)

### 자동 커밋

- 트랜잭션을 사용하려면 먼저 자동 커밋과 수동 커밋을 이해해야 한다.
- 자동 커밋으로 설정하면 각각의 쿼리 실행 직후에 자동 커밋을 호출한다. 따라서 커밋이나 롤백을 직접 호출하지 않아도 되는 편리함이 있다. 하지만 쿼리를 하나하나 실행할 때마다 자동으로 커밋이 되어버리기 때문에 우리가 원하는 트랜잭션 기능을 제대로 사용할 수 없다.

### 자동 커밋 설정

```sql
set autocommit true
insert into meber(member_id, money) values ('data1', 10000); //자동커밋
insert into meber(member_id, money) values ('data2', 10000); //자동커밋
```

- 따라서 `commit`, `rollback`을 직접 호출하면서 트랜잭션 기능을 제대로 수행하려면 자동 커밋을 끄고 수동 커밋을 사용해야 한다.
- (참고) 자동 커밋의 경우에도 쿼리마다 내부에서 짧은 트랜잭션이 일어난다.

### 수동 커밋 설정

```sql
set autocommit false; //수동 커밋 
insert into meber(member_id, money) values ('data3', 10000); //자동커밋
insert into meber(member_id, money) values ('data4', 10000); //자동커밋
commit; //수동 커밋
```

- 보통 자동 커밋 모드가 기본으로 설정된 경우가 많기 때문에, 수동 커밋 모드로 설정하는 것을 트랜잭션을 시작한다고 표현할 수 있다.
- 수동 커밋 설정을 하면 이후에 꼭 `commit`, `rollback`을 호출해야 한다. (호출하지 않으면, 데이터가 실제 DB에 반영되지 않는다.)
  - (참고) 호출하지 않고 DB 트랜잭션 수행 타임아웃 설정시간을 넘어가게 되면, 자동으로 롤백된다. (트랜잭션 수행 타임아웃 시간은 DB마다 다르니 참고하자)
  
참고로 수동 커밋 모드나 자동 커밋 모드는 한번 설정하면 해당 세션에서는 계속 유지도니다. 중간에 변경하는 것은 가능하다.

이제 본격적으로 트랜잭션 예제를 실습해보자.

## 트랜잭션 - DB 예제3 - 트랜잭션 실습

지금까지 설명한 예시를 직접 확인해보자.<br>
먼저 H2 데이터베이스 웹 콘솔 창을 2개 열어두자

> [!CAUTION]
> H2데이터베이스 웹 콘솔 창을 2개 열때, 기존 URL을 복사하면 안된다. 꼭 `http://localhost:8082`를 직접 연결해서 완전히 새로운 세션에서 연결하도록 하자.

### 1 커밋 - COMMIT

### 1-1 기본 데이터를 다음과 같이 맞추어두자.

<img src="./imgs/트랜잭션_이해/기본데이터.png"><br>

기본 데이터 SQL<br>
<img src="./imgs/트랜잭션_이해/기본_데이터_SQL.png"><br>

- 자동 커밋 모드를 사용했기 때문에 별도로 커밋을 호출하지 않아도 된다.(세션1, 세션2에서 모두 정상적으로 조회됨을 확인할 수 있습니다.)
- (참고) 참고로 이미지의 `name`필드는 이해를 돕기 위해 그린 것이고 실제로는 없다.

### 1-2 세션1 - 신규 데이터 추가

<img src="./imgs/트랜잭션_이해/세션1-신규_데이터_추가.png"><br>

세션1- 신규 데이터 추가 SQL<br>

```sql
//트랜잭션 시작
set autocommit false; //수동커밋모드

insert into member(member_id, money) values ('newId1', 10000);
insert into member(member_id, money) values ('newId2', 10000);
```
세션1, 세션2에서 `select * from member;`쿼리를 실행해서 결과를 확인하자

결과를 비교하면 아직 세션1이 커밋을 하지 않은 상태이기 때문에 세션1에서는 입력한 데이터가 보이지만, 세션 2에서는 입력한 데이터가 보이지 않은 것을 확인할 수 있다.

### 1-3 세션1 - 신규 데이터 추가 후 commit

<img src="./imgs/트랜잭션_이해/세션1-신규_데이터_추가_후_commit.png">

세션1에서 커밋(`commit`)을 호출해보고 세선1, 세센2에서 `select * from member`쿼리를 실행해서 결과를 확인해보자

세션 1이 트랜잭션을 커밋했기 때문에 데이터베이스에 실제 데이터가 반영된다. 커밋 이후에는 모든 세션에서 데이터를 조회할 수 있다.

### 2 롤백 - rollback

### 2-1 기본 데이터를 다음과 같이 맞추어두자.

<img src="./imgs/트랜잭션_이해/2-1세팅.png"><br>

기본 데이터 SQL<br>
```sql
set autocommit true;
delete from member;
insert into member(member_id, money) values ('oldid', 10000);
select * from member;
```

### 2-2 세션1 - 신규 데이터 추가

<img src="./imgs/트랜잭션_이해/2-2_신규데이터_추가.png"><br>

세션1 - 신규 데이터 추가 SQL

```sql
set autocommit false;

insert into member(member_id, money) values ('newId1', 10000);
insert into member(member_id, money) values ('newId2', 10000);
```

세션1에서 트랜잭션을 시작 상태로 만든 다음에 데이터를 추가하자.

세션1, 세션2에서 `select * from member;`쿼리를 실행해서 결과를 확인하자

세션1이 아직 커밋하지 않은 상태이므로 세션1에서는 입력한 데이터가 보이지만, 세션2에서는 입력한 데이터가 보이지 않는다.

### 2-3 세션1 - 신규 데이터 추가 후 rollback

<img src="./imgs/트랜잭션_이해/2-3_rollback.png"><br>

세션1에서 롤백을 호출해보자. (`rollback;`) 그리고 세션1, 세션2에서 `select * from member;` 쿼리를 실행해서 결과를 확인하자.

실행해보면 롤백으로 인해 데이터가 DB에 반영되지 않은 것을 확인할 수 있다.

## 트랜잭션 - DB 예제4 - 계좌이체

이번에는 계좌이체 예제를 통해 트랜잭션이 어떻게 사용되는지 조금 더 자세히 알아보자.<br>
다음 3가지 상황을 준비했다.

- 계좌이체 정상
- 계좌이체 문제 상황 - 커밋
- 계좌이체 문제 상황 - 롤백

먼저, 계좌이체가 발생하는 정상 흐름을 알아보자.

### 1 계좌이체 정상

### 1-1 기본 데이터 세팅

<img src="./imgs/트랜잭션_이해/계좌이체_기본데이터_세팅.png"><br>

기본 데이터 입력 SQL<br>

```sql
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA', 10000);
insert into member(member_id, money) values ('memberB', 10000);
select * from member
```

### 1-2 세션1 - 계좌이체 실행

<img src="./imgs/트랜잭션_이해/계좌이체_실행.png"><br>

`memberA`의 돈을 `memberB`에게 2000원 계좌이체하는 트랜잭션을 실행해보자. 아래와 같이 2번의 `update`쿼리가 수행되어야 한다.

계좌이체 실행 SQL<br>

```sql
set autocommit false;

update member set money = 10000 - 2000 where member_id = 'memberA';
update member set money = 10000 + 2000 where member_id = 'memberB';
```

- `set autocommit false`로 설정한다
- 아직 커밋하지 않았으므로 다른 세션에는 기존 데이터가 조회된다.

### 1-3 세션1 - 계좌이체 실행 커밋

<img src="./imgs/트랜잭션_이해/1-3_세션1-계좌이체_실행_커밋.png"><br>

- `commit`명령어를 실행하면 데이터베이스에 결과가 반영된다
- 다른 세션에서도 `memberA`의 금애긍ㄹ 8000원으로 줄어들고, `memberB`의 금액이 12000원으로 증가한 것을 확인할 수 있다.

이번에는 계조이체 도중에 문제가 발생하는 상황을 알아보자

### 2 계좌이체 문제 상황 - 커밋

### 2-1 기본 데이터 세팅

<img src="./imgs/트랜잭션_이해/2-1_기본_데이터_세팅.png"><br>

기본 데이터 입력 - sql<br>

```sql
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA', 10000);
insert into member(member_id, money) values ('memberB', 10000);
select * from member
```

처음으로 돌리기위해 데이터를 초기화해놓자

### 2-2 계좌이체 실행

<img src="./imgs/트랜잭션_이해/2-2_계좌이체_실행.png"><br>

- 계좌이체를 실행하는 도중에 SQL에 문제가 발생했다. 그래서 `memberA`의 돈을 2000원 줄이는 것에는 성공했지만, `memberB`의 돈을 2000원 증가시키는 것에 실패한다.
- 아래 쿼리에서 두 번째 SQL은 `member_idddd`라는 필드에 오타가 있다. 두 번째 update 쿼리를 실행하면 SQL 오류가 발생하는 것을 확인할 수 있다.

**계좌이체 실행 SQL - 오류**<br>
```sql
set autocommit false;

update member set money = 10000 - 2000 where member_id = 'memberA'; //성공
update member set money = 10000 + 2000 where member_iddd = 'memberA'; //쿼리 예외 발생
```

여기서 문제는 `memberA`의 돈은 2000원 줄어들었지만, `memberB`의 돈은 2000원 증가하지 않았다는 점이다. 결과적으로 계좌이체는 실패하고 `memberA`의 돈만 2000원 줄어든 상황이다.

**강제커밋**<br>

<img src="./imgs/트랜잭션_이해/강제커밋.png"><br>

- 만약 이 상황에서 강제로 `commit`을 호출하면 어떻게 될까?
- 결과적으로 계좌이체는 실패한 것인데, `memberA`의 돈만 2000원 줄어드는 아주 심각한 문제가 발생한다.
- 이렇게 중간에 문제가 발생했을 때는 커밋을 호출하면 안된다. 롤백을 호출해서 데이터를 트랜잭션 시작 시점으로 원복해야 한다.

중간에 문제가 발생했을 때 롤백을 호출해서 트랜잭션 시작 시점으로 데이터를 원복해보자.

### 3 계좌이체 문제 상황 - 롤백

### 3-1 기본 데이터 세팅

<img src="./imgs/트랜잭션_이해/3-1_기본_데이터_세팅.png"><br>

**기본 데이터 입력 - SQL**<br>

```sql
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA', 10000);
insert into member(member_id, money) values ('memberB', 10000);
select * from member;
```

예제를 처음으로 돌리기 위해 데이터를 초기화 하자.

### 3-2 계좌이체 실행

<img src="./imgs/트랜잭션_이해/2-3_계좌이체_실행.png"><br>

계좌이체를 실행하는 도중에 SQL에 문제가 발생한다, 그래서 `memberA`의 돈을 2000원 줄이는 것에는 성공했지만, `memberB`의 돈을 2000원 증가시키는 것에 실패한다. 아래 두 번째 update쿼리를 실행하면 SQL오류가 발생하는 것을 확인할 수 있다.

**계좌이체 실행 SQL - 오류**<br>

```sql
set autocommit false;

update member set money = 10000 - 2000 where member_id = 'memberA'; //성공
update member set money = 10000 + 2000 where member_iddd = 'memberA'; //쿼리 예외 발생

select * from member
```

여기서 문제는 `memberA`의 돈은 2000원 줄어들었지만, `memberB`의 돈은 2000원 증가하지 않았다는 점이다. 결과적으로 계좌이체는 실패하고 `memberA`의 돈만 2000원 줄어든 상황이다.

**롤백**<br>

<img src="./imgs/트랜잭션_이해/롤백.png"><br>

```sql
rollback;
select * from member;
```

- 이럴 때는 롤백을 호출해서 트랜잭션을 시작하기 전 단계롤 데이터를 복구해야 한다.
- 롤백을 사용한 덕분에 계좌이체를 실행하기 전 상태로 돌아왔다. `memberA`의 돈도 이전 상태인 10000원으로 유지되는 것을 확인할 수 있다.

### 정리

- **원자성**: 트랜잭션 내에서 실행한 작업들은 마치 하나의 작업인 것처럼 모두 성공하너가 모두 실패해야 한다.
  - 트랜잭션의 원자성 덕분에 여러 SQL 명령어를 마치 하나의 작업인 것 처럼 처리할 수 있었다. 성공하면 한 번에 반영하고, 중간에 실패해도 마치 하나의 작업을 되돌리는 것 처럼 간단히 되돌릴 수 있다.
- **오토 커밋**
  - 만약 오토 커밋 모드로 동작하는데, 계좌이체 중간에 실패하면 어떻게 될까? 쿼리를 하나 실행할 때마다 바로바로 커밋이 되어버리기 때문에 `memberA`의 돈만 2000원 줄어드는 심각한 문제가 발생한다.
- **트랜잭션 시작**
  - 따라서 이런 종류의 작업은 꼭 수동 커밋 모드를 사용해서 수동으로 커밋, 롤백할 수 있도록 해야 한다. 보통 이렇게 자동 커밋 모드에서 수동 커밋 모드로 전환하는 것을 트랜잭션을 시작한다고 표현한다.

## DB 락 - 개념 이해

세션1이 트랜잭션을 시작하고 데이터를 수정하는 동안 아직 커밋을 수행하지 않았는데, 세션2에서 동시에 같은 데이터를 수정하게 되면 여러가지 문제가 발생한다. 바로 트랜잭션의 원자성이 깨지는 것이다. 여기에 더해서 세션1이 중간에 롤백을 하게 되면 세션2는 잘못된 데이터를 수정하는 문제가 발생한다.

이런 문제를 방지하려면, 세션이 트랜잭션을 시작하고 데이터를 수정하는 동안에는 커밋이나 롤백 전까지 다른 세션에서 해당 데이터를 수정할 수 없게 막아야 한다.

### 락0

<img src="./imgs/트랜잭션_이해/락0.png"><br>

- 세션1은 `memberA`의 금액을 500원으로 변경하고 싶고, 세션2는 같은 `memberA`의 금액을 1000원으로 변경하고 싶다.
- 데이터베이스는 이런 문제를 해결하기 위해 락(Lock)이라는 개념을 제공한다.
- 다음 예시를 통해 동시에 데이터를 수정하는 문제를 락으로 어떻게 해결하는지 자세히 알아보자.

### 락1
<img src="./imgs/트랜잭션_이해/락1.png"><br>

1. 세션1은 트랙잭션을 시작한다.
2. 세션1은 `memberA`의 `money`를 500으로 변경을 시도한다. 이때, 데이터를 변경하기 위해서는 해당 로우의 락을 먼저 획득해야 한다. 락이 남아 있으므로 세션1은 락을 획득한다. (세션1이 세션2보다 조금 더 빨리 요청했다.)
3. 세션1은 락을 획득했으므로 해당 로우에 update sql을 수행한다.

### 락2

<img src="./imgs/트랜잭션_이해/락2.png"><br>

1. 세션2는 트랜잭션을 시작한다
2. 세션2도 `memberA`의 `money` 데이터를 변경하려고 시도한다. 이때 해당 로우의 락을 먼저 획득해야 한다. 그런데 락이 없으므로 락이 돌아올 때까지 대기한다.

참고로 세션2가 락을 무한정 대기하는 것은 아니다. 락 대기 시간을 넘어가면 락 타임아웃 오류가 발생한다. 락 대기 시간은 설정할 수 있다.

### 락3

<img src="./imgs/트랜잭션_이해/락3.png"><br>

1. 세션1은 커밋을 수행한다. 커밋으로 트랜잭션이 종료되었으므로 락도 반납한다.

### 락4

<img src="./imgs/트랜잭션_이해/락4.png"><br>

락을 획득하기 위해 대기하던 세션2가 락을 획득한다.

### 락5

<img src="./imgs/트랜잭션_이해/락5.png"><br>

1. 세션2는 update sql을 수행한다.

### 락6

<img src="./imgs/트랜잭션_이해/락6.png"><br>

1. 세션2는 커밋을 수행하고 트랜잭션이 종료되었으므로 락을 반납한다.

## DB 락 - 변경

앞서 배운 내용을 실습해보자

### 락0

<img src="./imgs/트랜잭션_이해/락0.png"><br>

기본 데이터 입력<br>
```sql
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA', 10000);
```

### 락1

<img src="./imgs/트랜잭션_이해/락1.png"><br>

**세션 1 - 업데이트**<br>

```sql
set autocommit false;
update member set money=500 where member_id = 'memberA';
```

세션1이 트랜잭션을 시작하고, `memberA`의 데이터를 500원으로 업데이트했다. 아직 커밋은 하지 않았다. (따라서 세션1에서만 `memberA`의 돈이 500원으로 조회된다.)

`memberA`로우의 락은 세션1이 가지게 된다.

### 락2

<img src="./imgs/트랜잭션_이해/락2.png"><br>

세션2 - 수정 시도

```sql
SET LOOK_TIMEOUT 60000;
set autocommit false;
update member set money=1000 where member_id='memberA';
```

- 세션2는 `memberA`의 데이터를 1000원으로 수정하려 한다.
- 세션1이 트랜잭션을 커밋하거나 롤백해서 종료하지 않았으므로 아직 세션1이 락을 가지고 있다. 따라서 세션2는 락을 획득하지 못하기 때문에 데이터를 수정할 수 없다. 세션2는 락이 돌아올 때 까지 대기하게 된다.
- `SET LOOK_TIMEOUT 60000`: 락 획득 시간을 60초로 설정한다. 60초안에 락을 얻지 못하면 예외가 발생한다. (참고로 H2데이터 베이스에서는 딱 60초에 예외가 발생하지는 않고, 시간이 조금 더 걸릴 수 있다.)

### 락3

<img src="./imgs/트랜잭션_이해/락3.png"><br>

세션1은 커밋을 수행한다. 커밋으로 트랜잭션이 종료되었으므로 락을 반납한다.

**세션 1 - 커밋**<br>

```sql
COMMIT;
```

세션1이 커밋하면 이후에 락을 반납하고 다음 시나리오가 이어진다..

### 락5

<img src="./imgs/트랜잭션_이해/락5.png"><br>

세션2는 update sql을 정상 수행한다.

### 락6

<img src="./imgs/트랜잭션_이해/락6.png"><br>

세션2는 커밋을 수행하고 트랜잭션이 종료되었으므로 락을 반납한다.

**세션2 - 커밋**<br>
```sql
COMMIT;
```

### 세션2 락 타임아웃

- `SET LOCK_TIMEOUT`: 락 타임아웃 시간을 설정한다.
  - 예) `SET LOCK_TIMEOUT 10000` 10초, 세션2에 설정하면 세션2가 10초 동안 대기해도 락을 얻지 못하면 락 타임아웃 오류가 발생한다.
- 위 시나리오 중간에 락을 오랜기간 대기하면 어떻게 되는지 알아보자. 10초 정도 기다리면 세션2에서는 다음과 같은 락 타임아웃 오류가 발생한다.<br>

<img src="./imgs/트랜잭션_이해/락_타임아웃_오류.png"><br>\

세션1이 `memberA`의 데이터를 변경하고, 트랜잭션을 아직 커밋하지 않았다. 따라서 세션2는 세션1이 트랜잭션을 커밋하거나 롤백할 때 까지 대기해야 한다. 기다리면 락 타임아웃 오류가 발생하는 것을 확인할 수 있다.

> [!TIP]
> 테스트 도중 락이 꼬이는 문제가 발생할 수 있다. 이럴 때는 H2 서버를 내렸다가 다시 올리자.

## DB 락 - 조회

### 일반적인 조회는 락을 사용하지 않는다.

데이터베이스마다 다르지만, 보통 데이터를 조회할 때는 락을 획득하지 않고 바로 데이터를 조회할 수 있다. 예를 들어서 세션1이 락을 획득하고 데이터를 변경하고 있어도, 세션2에서 데이터를 조회는 할 수 있다. 물론 세션2에서 조회가 아니라 데이터를 변경하려면 락이 필요하기 때문에 락이 돌아올 때 까지 대기해야 한다.

### 조회와 락

데이터를 조회할 때도 락을 획득하고 싶을때가 있다. 이럴 때는 select for update구문을 사용하면 된다. 이렇게 하면 세션1이 조회 시점에을 가져가버리기 때문에 다른 세션에서 해당 데이터를 변경할 수 없다. 물론 이 경우도 트랙잭션을 커밋하면 반납한다. 

### 조회 시점에 락이 필요한 경우는 언제일까?

트랜잭션 종료 시점까지 해당 데이터를 다른 곳에서 변경하지 못하도록 강제로 막아야 할 때 사용한다.<br>
예를 들어서 애플리케이션 로직에서 `memberA`의 금액을 조회한 다음에 이 금액 정보로 애플리케이션에서 어떤 계산을 수행한다. 그런데 이 계산이 돈과 관련된 매우 중요한 계산이여서 계산이 완료할 때 까지 `memberA`의 금액을 다른곳에서 변경하면 안된다. 이럴 때 조회 시점에 락을 획득하면 된다.

**확인해보자**<br>

실습을 위해 데이터를 기본 데이터를 입력하자.<br>

```sql
set autocommit true;
delete from member;
insert into member(member_id, money) values ('memberA', 10000);
```

***세션 1**<br>

```sql
set autocommit false;
select * from member where member_id='memberA' for update;
```

`select for update` 구문을 사용하면 조회를 하면서 동시에 선택한 로우의 락도 획득한다.<br>
세션1은 트랙잭션을 종료할 때 까지 `memberA`의 로우의 락을 보유한다.

**세션 2**<br>

```sql
set autocommit false;
update member set money=500 where member_id='memberA';
```

세션2는 데이터를 변경하고 싶다. 데이터를 변경하려면 락이 필요하다.<br>
세션1이 `memberA` 로우의 락을 획득했기 때문에 세션2는 락을 획득할 때 까지 대기한다.<br>
이후 세션1이 커밋을 수행하면 세션2가 락을 획득하고 데이터를 변경한다. 만약 락 타임아웃 시간이 지나면 락 타임아웃 예외가 발생한다.

> [!IMPORTANT]
> 트랜잭션과 락은 데이터베이스마다 실제 동작하는 방식이 조금씩 다르기 때문에, 해당 베이터베이스 메뉴얼을 확인해보고, 의도한대로 동작하는지 테스트한 이후에 사용하자.

## 트랜잭션 - 적용 1

실제 애프릴케이션에서 DB 트랜잭션을 사용해서 계좌이체 같이 원자성이 중요한 비즈니스 로직을 어떻게 구현하는지 알아보자.

먼저 트랜잭션 없이, 단순하게 계좌이체 비즈니스 로직만 구현해보자.

### MemberServiceV1 생성

```java
package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    public void accountTransform(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
```

- `fromId`의 회원을 조회해서 `toId`의 회원에게 `money`만큼의 돈을 계좌이체 하는 로직이다.
  - `fromId`회원의 돈은 `money`만큼 감소한다. -> `UPDATE SQL`실행
  - `toId`회원의 돈을 `money`만큼 증가한다. -> `UPDATE SQL`실행
- (참고) 예외 상황을 테스트해보기 위해 `toId`가 `"ex"`인 경우 예외를 발생한다

### MemberServiceV1Test 생성

```java
package hello.jdbc.service;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 기본 동작, 트랜젝션이 없어서 문제 발생
 */
class MemberServiceV1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV1 memberRepository;
    private MemberServiceV1 memberService;

    @BeforeEach
    void beforeEach() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                URL, USERNAME, PASSWORD
        );
        memberRepository = new MemberRepositoryV1(dataSource);
        memberService = new MemberServiceV1(memberRepository);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransform(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }
}
```

- 정상적으로 실행됨을 확인할 수 있다. (memberA의 돈이 차감되었고, memberB의 돈이 증가하였다.)
- (주의) 테스트를 수행하기 전에 MEMBER 테이블 내 데이터를 삭제해야 한다.

### 정상이체 - accountTransfer()

- **given**: 다음 데이터를 저장해서 테스트를 준비한다
  - memberA 10000원
  - memberB 10000원
- **when**: 계좌이체 로직을 실행한다.
  - `memberService.accountTransfer()`를 실행한다.
  - `memberA` -> `memberB`로 2000원 계좌이체 한다.
    - `memberA`의 금액이 2000원 감소한다
    - `memberB`의 금액이 2000원 증가한다.
  - **then**: 계좌이체가 정상 수행되었는지 검증한다.
    - `memberA` 8000원
    - `memberB` 12000원

### 예외발생 테스트

```java
@Test
@DisplayName("이체 중 예외 발생")
void accountTransferEx() throws SQLException {
    //given
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberEx = new Member(MEMBER_EX, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberEx);

    //when
    Assertions.assertThatThrownBy(
            () -> memberService.accountTransform(memberA.getMemberId(), memberEx.getMemberId(), 2000))
            .isInstanceOf(IllegalStateException.class);

    //then
    Member findMemberA = memberRepository.findById(memberA.getMemberId());
    Member findMemberEx = memberRepository.findById(memberEx.getMemberId());
    Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
    Assertions.assertThat(findMemberEx.getMoney()).isEqualTo(10000);
}
```

### 이체중 예외 발생 - accountTransferEx()

- **given**: 다음 데이터를 저장해서 테스트를 준비한다.
  - `memberA` 10000원
  - `memberEx` 10000원
- **when**: 계좌이체 로직을 실행한다.
  - `memberService.accountTransfer()`를 실행한다.
  - `memberA` -> `memberEx`로 2000원 계좌이체 한다.
    - `memberA`의 금액이 2000원 감소한다
    - `memberEx`의 회원ID는 `ex`이므로 **중간에 예외가 발생한다**
  - **then**: 계좌이체는 실패한다. `memberA`의 돈만 2000원 줄어든다
    - `memberA` 8000원
    - `memberEx` 10000원

> [!NOTE]
> 이체중 예외가 발생하게 되면 `memberA`의 금액은 10000원 -> 8000원으로 2000원 감소한다. 그런데 `memberEx`의 돈은 10000원으로 남아있다. 결과적으로 `memberA`의 돈만 2000원으로 감소한 것이다!

> [!TIP]
> 테스트가 끝나면 다음 테스트에 영향을 주지 않기 위해 `@AfterEach`에서 테스트에 사용한 데이터를 모두 삭제하자.

```java
@AfterEach
void after() throws SQLException {
    memberRepository.delete(MEMBER_A);
    memberRepository.delete(MEMBER_B);
    memberRepository.delete(MEMBER_EX);
}
```

## 트랜잭션 - 적용 2

이번에는 DB트랜잭션을 사용해서 앞서 발생한 문제점을 해결해보자

애플리케이션에 트랜잭션을 어떤 계층에 걸어야 할까? 쉽게 이야기해서 트랜잭션을 어디에서 시작하고, 어디에서 커밋해야 할까?

### 비즈니스 로직과 트랜잭션

<img src="./imgs/트랜잭션_이해/비즈니스_로직과_트랜잭션.png"><br>

- 트랜잭션은 비즈니스 로직이 있는 서비스 계층에서 시작해야 한다. 왜냐하면 비즈니스 로직이 잘못되면 해당 비즈니스 로직으로 인해 문제가 되는 부분을 함께 롤백해야 하기 때문이다.
- 그런데 트랜잭션을 시작하려면 커넥션이 필요하다. 결국 서비스 계층에서 커넥션을 만들고, 트랜잭션 커밋 이후에 커넥션을 종료해야 한다.
- 애플리케이션에서 DB 트랜잭션을 사용하려면 **트랜잭션을 사용하는 동안 같은 커넥션을 유지**해야한다. 그래야 같은 세션을 사용할 수 있다.

### 커넥션과 세션

<img src="./imgs/트랜잭션_이해/커넥션과_세션.png"><br>

애플리케이션에서 같은 커넥션을 유지하려면 어떻게 해야할까? 가장 단순한 방법은 커넥션을 파라미터로 전달해서 같은 커넥션이 사용되도록 유지하는 것이다.

### MemberRepositoryV2 생성

코드 유지를 위해 `MemberRepositoryV1`은 남겨두고, `MemberRepositoryV1`를 복사해서 `MemberRepositoryV2`를 만들자.

기존 findById메서드를 복사해서 Connection을 파라미터로 받도록 추가하자.

```java
public Member findById(Connection con, String memberId) throws SQLException {
    String sql = "select * from member where member_id = ?";

    //Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        //con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, memberId);

        rs = pstmt.executeQuery();
        if (rs.next()) {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        } else {
            throw new NoSuchElementException("member not found memberId =" + memberId);
        }
    } catch (SQLException e) {
        log.error("error", e);
        throw e;
    } finally {
        //lose(con, pstmt, rs);
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(pstmt);
    }
}
```

기존 update 메서드를 복사해서 Connection을 파라미터로 받도록 추가하자.

```java
public void update(Connection con, String memberId, int money) throws SQLException {
    String sql = "update member set money=? where member_id=?";

    //Connection con = null;
    PreparedStatement pstmt = null;

    try {
        //con = getConnection();
        pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, money);
        pstmt.setString(2, memberId);

        int resultSize = pstmt.executeUpdate();
        log.info("resultSize={}", resultSize);
    } catch (SQLException e) {
        log.error("error", e);
        throw e;
    } finally {
        //lose(con, pstmt, null);
        JdbcUtils.closeStatement(pstmt);
    }
}
```

### 적용한 코드

- `MemberRepositoryV2`는 기존 코드와 같고, 커넥션 유지가 필요한 다음 두 메서드가 추가되었다.<br>참고로 다음 두 메서드는 계좌이체 서비스 로직에서 호출하는 메서드이다.
  - `findById(Connection con, String memberId)`
  - `update(Connection con, String memberId, int money)`
- (중요)
  - 커넥션 유지가 필요한 두 메서드는 파라미터로 넘어온 커넥션을 사용해야 한다. 따라서 `con = getConnection()`코드가 있으면 안된다.(커넥션을 새로 만들면 안된다.)
  - 커넥션 유지가 필요한 두 메서드는 리포지토리에서 커넥션을 닫으면 안도니다. 커넥션을 전달받은 리포지토리 뿐만 아니라 이후에도 커넥션을 계속 이어서 사용하기 때문이다. 이후 서비스 로직이 끝날 때 트랜잭션을 종료하고 닫아야 한다.

이제 가장 중요한 트랙잭션 연동 로직을 작성해보자.

### MemberService생성

코드 유지를 위해 `MemberServiceV1`은 남겨두고, `MemberServiceV1`을 복사해서 `MemberServiceV2`를 만들자.

`accountTransfer()` 계좌이체 메서드를 다음과 같이 수정하자.

```java
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransform(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false); //트랜잭션 시작

            bizLogic(con, fromId, toId, money); //비즈니스 로직

            con.commit(); //성공시 커밋

        } catch (Exception e) {
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); //커넥션 풀 고려
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
```

### 로직 분석

- `Connection con = dataSource.getConnection();`
  - 트랜잭션을 시작하려면 커넥션이 필요하다.
- `con.setAutoCommit(false);`
  - 트랜잭션을 시작하려면 자동 커밋 모드를 꺼야 한다. 이렇게 하면 커넥션을 통해서 세션에 `set autocommit false`가 전달되고, 이후부터는 수동 커밋 모드로 동작한다. 이렇게 자동 커밋 모드를 수동 커밋 모드로 변경하는 것을 트랜잭션을 시작한다고 보통 표현한다.
- `bizLogic(con, fromId, toId, money);`
  - 트랜잭션이 시작된 커넥션을 전달하면서 비즈니스 로직을 수행한다.
  - 이렇게 분리한 이유는 트랜잭션을 관리하는 로직과 실제 비즈니스 로직을 구분하기 위함이다.
  - `memberRepository.findById(con...), memberRepository.update(con..)`: 비즈니스 로직을 보면 리포지토리를 호출할 때 커넥션을 전달하는 것을 확인할 수 있따.
- `con.commit();`
  - 비즈니스 로직이 정상 수행되면 트랜잭션을 커밋한다.
- `con.rollback();`
  - `catch(Ex){..}`를 사용해서 비즈니스 로직 수행 도중에 예외가 발생하면 트랜잭션을 롤백한다.
- `release(con);`
  - `finally {...}`를 사용해서 커넥션을 모두 사용하고 사용하고 나면 안전하게 종료한다. 그런데 커넥션풀을 사용하면 `con.close()`를 호출했을 때 커넥션이 종료되는 것이 아니라 풀에 반납된다. 따라서 현재 수동 커밋 모드로 동작하기 때문에 풀에 돌려주기 전에 기본 값인 자동 커밋모드로 변경하는것이 안전하다.

### MemberServiceV2Test생성 - 테스트 코드 작성

```java
package hello.jdbc.service;

/**
 * 트랜잭션 - 커넥션 파라미터 전달 방식 동기화(파라미터로 전달해서 같은 커넥션을 사용)
 */
class MemberServiceV2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV2 memberRepository;
    private MemberServiceV2 memberService;

    @BeforeEach
    void beforeEach() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                URL, USERNAME, PASSWORD
        );
        memberRepository = new MemberRepositoryV2(dataSource);
        memberService = new MemberServiceV2(dataSource, memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransform(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);

    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        //when
        Assertions.assertThatThrownBy(
                        () -> memberService.accountTransform(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberEx.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(10000);
        Assertions.assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }

}
```

### 로직 분석

- 정상이체 - accountTransfer()
  - 기존 로직과 같아서 생략한다.
- 이체중 예외 발생 - accountTransferEx()
  - 다음 데이터를 저장해서 테스트를 준비한다
    - memberA 10000원
    - memberEx 10000원
  - 계좌이체 로직을 실행한다
    - `memberService.accountTransfer()`를 실행한다.
    - 커넥션을 생성하고 트랜잭션을 시작한다
    - `memberA` -> `memberEx`로 2000원 계좌이체 한다.
      - `memberA`의 금액의 2000원 감소한다
      - `memberEx`회원 ID는 `ex`이므로 중간에 예외가 발생한다.
    - 예외가 발생했으므로 트랜잭션을 롤백한다.
  - 계좌이체는 실패했다. 롤백을 수행해서 `memberA`의 돈이 기존 10000원으로 복구되었다.
    - `memberA` 10000원 - 트랜잭션 롤백으로 복구된다.
    - `memberEx` 10000 - 중간에 실패로 로직이 수행되지 않았다. 따라서 그대로 10000원으로 남아있게 도니다.

트랜잭션 덕분에 계좌이체가 실패할 때 롤백으로 수행해서 모든 데이터를 정상적으로 초기화할 수 있게 되었다. 결과적으로 계좌이체를 수행하기 직전으로 돌아가게 된다.

### 남은 문제

- 애플리케이션에서 DB트랜잭션을 적용하려면 서비스 계층이 매우 지저분해지고, 생각보다 매우 복잡한 코드를 요구한다. 추가로 커넥션을 유지하도록 코드를 변경하는 것도 쉬운 일은 아니다. 다음 내용부터, 스프링을 사용해서 이런 문제들을 하나씩 해결해보자

> [!NOTE]
> 서비스 로직을 보면, 실제 비즈니스 로직보다 트랜잭션을 처리하기 위한 코드가 더 많다. (커넥션을 맏아와서, 트랜잭션 시작하고, 예외 잡고, 커밋/롤백하고, 마지막에 release 작업 등)<br> 다음 내용을 통해, 스프링은 이런 불편함을 어떻게 해결하도록 도와주는지 하나씩 알아보자.

# 트랜잭션

## 문제점들

### 애플리케이션 구조
여러가지 애플리케이션 구조가 있지만, 가장 단순하면서 많이 사용하는 방법은 역할에 따라 3가지 계층으로 나누는 것이다.

<img src="./imgs/트랜잭션/애플리케이션_구조.png"><br>

- 프레젠테이션 계층
  - UI와 관련된 처리 담당
  - 웹 요청과 응답
  - 사용자 요청을 검증
  - 주 사용 기술: 서블릿과 HTTP같은 웹 기술, 스프링 MVC
- 서비스 계층
  - 비즈니스 로직을 담당
  - 주 사용 기술: 가급적 특정 기술에 의존하지 않고, 순수 자바 코드로 작성
- 데이터 접근 계층
  - 실제 데이터베이스에 접근하는 코드
  - 주 사용 기술: JDBC, JPA, File, Redis, ...

### 순수한 서비스 계층

- 여기서 가중 중요한 곳은 어디일까? 바로 핵심 비즈니스 로직이 들어있는 서비스 계층이다. 시간이 흘러서 UI(웹)와 관련된 부분이 변하고, 데이터 저장 기술을 다른 기술로 변경해도, 비즈니스 로직은 최대한 변경없이 유지되어야 한다.
- 이렇게 하려면 서비스 계층을 특정 기술에 종속적이지 않게 개발해야 한다.
  - 이렇게 계층을 나눈 이유도 서비스 계층을 최대한 순수하게 유지하기 위한 목적이 크다. 기술에 종속적인 부분은 프레젠테이션 계층, 데이터 접근 계층에서 가지고 간다.
  - 프레젠테이션 계층은 클라이언트가 접근하는 UI와 관련된 기술인 웹, 서블릿, HTTP와 관련된 부분을 담당해준다. 그래서 서비스 계층을 이런 UI와 관련된 기술로부터 보호해준다. 예를 들어서 HTTP API를 사용하다가 GRPC같은 기술로 변경해도 프레젠테이션 계층의 코드만 변경하고, 서비스 계층은 변경하지 않아도 된다.
  - 데이터 접근 계층은 데이터를 저장하고 관리하는 기술을 담당해준다. 그래서 JDBC, JPA와 같은 구체적인 데이터 접근 기술로부터 서비스 계층을 보호해준다. 예를 들어서 JDBC를 사용하다가 JPA로 변경해도 서비스 계층은 변경하지 않아도 된다. 물론 서비스 계층에서 데이터 접근 계층을 직접 접근하는 것이 아니라, 인터페이스를 제공하고 서비스 계층은 이 인터페이스에 의존하는 것이 좋다. 그래야 서비스 코드의 변경 없이 `JdbcRepository`를 `JpaRepository`로 변경할 수 있다.
- 서비스 계층이 특정 기술에 종속되지 않기 때문에 비즈니스 로직을 유지보수 하기도 쉽고, 테스트 하기도 쉽다.
- 정리하자면 서비스 계층은 가급적 비즈니스 로직만 구현하고 특정 구현 기술에 직접 의존해서는 안된다. 이렇게 하면 향후 구현 기술이 변경될 때 변경의 영향 범위를 최소화할 수 있다.

### 문제점들

서비스 계층을 순수하게 유지하려면 어떻게 해야할까?<br>
지금까지 개발한 `MemberService`코드들을 살펴보자

먼저 `MemberServiceV1`코드를 살펴보자.

```java
@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    public void accountTransform(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
```

- `MemberServiceV1`은 특정 기술에 종속적이지 않고, 순수한 비즈니스 로직만 존재한다.
- 특정 기술과 관련된 코드가 거의 없어서 코드가 깔끔하고, 유지보수 하기 쉽다.
- 향후 비즈니스 로직의 변경이 필요하면 이 부분을 변경하면 된다.

그런데 사실 여기에도 남은 문제가 있다.<br>
(지금 단계에서 이 문제들은 이런게 있구나 참고만 하고 넘어가자. 뒤에서 설명한다.)

- `SQLException`이라는 JDBC기술에 의존한다는 점이다. (`SQLException`은 JDBC 기술에 종속적인 예외이다. 만약 JDBC를 사용하지 않고, JPA등을 사용하면 다른 예외가 올라온다. 따라서 나중에 데이터 접근 기술을 바꾸게 되는 경우, 이 예외 부분에서 컴파일 오류가 발생한다.)
- 이 부분은 `memberRepository`에서 올라오는 예외이기 때문에 `memberRepository`에서 해결해야 한다. 이 부분은 뒤에서 예외를 다룰때 알아보자.
- `MemberRepositoryV1`이라는 구체 클래스에 직접 의존하고 있다. `MemberRepository`인터페이스를 도입하면 향후 `MemberService`의 코드 변경 없이 다른 구현 기술로 손쉽게 변경할 수 있다.

다음으로 트랜잭션을 적용한 `MemberServiceV2`코드를 살펴보자.

```java
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransform(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false); //트랜잭션 시작

            bizLogic(con, fromId, toId, money); //비즈니스 로직

            con.commit(); //성공시 커밋

        } catch (Exception e) {
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); //커넥션 풀 고려
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
```

- 이전 섹션에서 학습한 것 처럼, 트랜잭션은 비즈니스 로직이 있는 서비스 계층에서 시작하는 것이 좋다. (왜냐하면, 비즈니스 로직이 잘못되면 해당 비즈니스 로직으로 인해 문제가 되는 부분을 함께 롤백해야 하기 때문이다.)
- 그런데 문제는 트랜잭션을 사용하기 위해서 `javax.sql.DataSource`, `java,sql,Connection`, `java.sql.SQLException`같은 JDBC 기술에 의존해야 한다는 점이다.
- 트랜잭션을 사용하기 위해 JDBC 기술에 의존해야 한다. 결과적으로 비즈니스 로직보다 JDBC를 사용해서 트랜잭션을 처리하는 코드가 더 많다.
- 향후 JDBC에서 JPA같은 다른 기술로 바꾸어 사용하게 되면 서비스 코드 모두 함께 변경해야 한다.(JPA는 트랜잭션을 사용하는 코드가 JDBC와 다르다.)
- 핵심 비즈니스 로직과 JDBC기술이 섞여 있어서 유지보수 하기 어렵다.

### 문제 정리

지금까지 개발한 애플리케이션의 문제점은 크게 3가지이다.
1. 트랜잭션 문제
2. 예외 누수 문제
3. JDBC 반복 문제

### 트랜잭션 문제
가장 큰 문제는 트랜잭션을 적용하면서 생긴 다음과 같은 문제들이다.

- JDBC 구현 기술이 서비스 계층에 누수되는 문제
  - 트랜잭션을 적용하기 위해 JDBC 구현 기술이 서비스 계층에 누수되었다.
  - 서비스 계층은 순수해야 한다. -> 구현 기술을 변경해도 서비스 계층 코드는 최대한 유지할 수 있어야 한다.(변화에 대응)
    - 그래서 데이터 접근 계층에 JDBC코들르 다 몰아두는 것이다.
    - 물론 데이터 접근 계층의 구현 기술이 변경될 수도 있으니 데이터 접근 계층은 인터페이스를 제공하는 것이 좋다.
  - 서비스 계층은 특정 기술에 종속되지 않아야 한다. 지금가지 그렇게 노력해서 데이터 접근계층으로 JDBC관련 코드를 모았는데, 트랜잭션을 적용하면서 결국 서비스 계층에 JDBC구현 기술의 누수가 발생했다.
- 트랜잭션 동기화 문제
  - 같은 트랜잭션을 유지하기 위해 커넥션을 파라미터로 넘겨야 한다.
  - 이때 파생되는 문제들도 있다. 똑같은 기능도 트랜잭션용 기능과 트랜잭션을 유지하지 않아도 되는 기능으로 분리해야 한다.
- 트랜잭션 적용 반복 문제
  - 트랜잭션 적용 코드를 보면 반복이 많다. `try`, `catch`, `finally`, ...

### 예외 누수
- 데이터 접근 계층의 JDBC 구현 기술 예외가 서비스 계층으로 전파된다.
- `SQLException`은 체크 예외이기 때문에 데이터 접근 계층을 호출한 서비스 계층에서 해당 예외를 잡아서 처리하거나 명시적으로 `throws`를 통해서 다시 밖으로 던져야 한다.
- `SQLException`은 JDBC 전용기술이다. 향후 JPA나 다른 데이터 접근 기술을 사용하면, 그에 맞는 단른 예외로 변경해야 하고, 결국 서비스 코드도 수정해야 한다.

### JDBC 반복 문제

- 지금까지 작성한 `MemberRepository`코드는 순수한 JDBC를 사용했다.
- 이 코드들은 유사한 코드의 반복이 너무 많다.
  - `try`, `catch`, `finally`, ...
  - 커넥션을 열고, `PreparedStatement`를 사용하고, 결과를 매핑하고... 실행하고, 커넥션과 리소스를 정리한다.

### 스프링과 문제 해결

스프링은 서비스 계층을 순수하게 유지하면서, 지금까지 이야기한 문제들을 해결할 수 있는 다양한 방법과 기술들을 제공한다.

지금부터 스프링을 사용해서 우리 애플리케이션이 가진 문제들을 하나씩 해결해보자.

## 트랜잭션 추상화

현재 서비스 계층은 트랜잭션을 사용하기 위해서 JDBC 기술에 의존하고 있다.<br>
향후 JDBC에서 JPA같은 다른 데이터 접근 기술로 변경하면, 서비스 계층의 트랜잭션 관련 코드도 모두 함께 수정해야 한다.

### 구현기술에 따른 트랜잭션 사용법

트랜잭션은 원자적 단위의 비즈니스 로직을 처리하기 위해 사용한다.<br>
구현 기술마다 트랜잭션을 사용하는 방법이 다르다.<br>(아래는 JDBC와 JPA 트랜잭션 코드 예시이다. 코드를 자세히 이해할 필요는 없다. 구현 기술마다 트랜잭션을 사용하는 방법이 다르다는 정도로 보고 넘어가자.)

JDBC: `con.setAutoCommit(false)`

```java
public void accountTransform(String fromId, String toId, int money) throws SQLException {
    Connection con = dataSource.getConnection();

    try {
        con.setAutoCommit(false); //트랜잭션 시작

        bizLogic(con, fromId, toId, money); //비즈니스 로직

        con.commit(); //성공시 커밋

    } catch (Exception e) {
        con.rollback(); //실패시 롤백
        throw new IllegalStateException(e);
    } finally {
        release(con);
    }
}
```

JPA: `transaction.begin()`

```java
public static void main(String[] args) {

    //엔티티 매니저 팩토리 생성
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    EntityoManager em = emf.createEntityManager(); //엔티티 매니지 생성
    EntityTransaction tx = em.getTransaction(); //트랜잭션 기능 획득

    try {
        tx.begin(); //트랜잭션 시작
        logic(em); //비즈니스 로직
        tx.commit(); //트랜잭션 커밋
    } catch (Exception e) {
        tx.rollback(); //트랜잭션 롤백
    } finally {
        em.close(); //엔티티 매니저 종료
    }
    emf.close(); //앤티티 매니저 팩토리 종료
}
```

트랜잭션을 사용하는 코드는 데이터 접근 기술마다 다르다.<br>
만약 다음 그림과 같이 JDBC 기술을 사용하고, JDBC 트랜잭션에 의존하다가 JPA 기술로 변경하게 되면 서비스 계층의 트랜잭션을 처리하는 코드도 모두 함께 변경해야 한다.

### JDBC 트랜잭션 의존

<img src="./imgs/트랜잭션/JDBC_트랜잭션_의존.png"><br>

데이터 접근 계층에서 JDBC 기술을 사용하고, 서비스 계층에서도 트랜잭션과 관련된 코드에서 JDBC 기술을 사용한다.

### JDBC 기술 -> JPA 기술로 변경

<img src="./imgs/트랜잭션/JDBC_기술_->_JPA_기술로_변경.png"><br>

이렇게 JDBC 기술을 사용하다가 JPA 기술로 변경하게 되면 서비스 계층의 코드도 JPA기술을 사용하도록 함께 수정해야 한다.

이 문제를 어떻게 해결할 수 있을까?

### 트랜잭션 추상화

이 문제를 해결하려면 트랜잭션 기능을 추상화 하면 된다.<br>
아주 단순하게 생각하면 다음과 같은 인터페이스를 만들어서 사용하면 된다.

**트랜잭션 추상화 인터페이스**<br>
```java
public interface TxManager {
    begin();
    commit();
    rollback();
}
```

- 트랜잭션은 사실 단순하다. 트랜잭션을 시작하고, 비즈니스 로직의 수행이 끝나면 커밋하거나 롤백하면 된다.
- 그리고 다음과 같이 `TxManager`인터페이스를 기반으로 각각의 기술에 맞는 구현체를 만들면 된다.
  - `JdbcTxManager`: JDBC 트랜잭션 기능을 제공하는 구현체
  - `JpaTxManager`: JPA 트랜잭션 기능을 제공하는 구현체

### 트랜잭션 추상화와 의존관계

<img src="./imgs/트랜잭션/트랜잭션_추상화와_의존관계.png"><br>

- 서비스는 특정 트랜잭션 기술에 직접 의존하는 것이 아니라, `TxManager`라는 추상화된 인터페이스에 의존한다. 이제 원하는 구현체를 DI를 통해서 주입하면 된다. 예를 들어서 JDBC트랜잭션 기능이 필요하면 `JdbcTxManager`를 서비스에 주입하고, JPA 트랜잭션 기능으로 변경해야 하면 `JpaTxManager`를 주입하면 된다.
- 클라이언트인 서비스는 인터페이스에 의존하고 DI를 사용한 덕분에 OCP 원칙을 지키게 되었다. 이제 트랜잭션을 사용하는 서비스 코드를 전혀 변경하지 않고, 트랜잭션 기술을 마음껏 변경할 수 있다.

### 스프링의 트랜잭션 추상화

스프링은 이미 이런 고민을 다 해두었다. 우리는 스프링이 제공하는 트랜잭션 추상화 기술을 사용하면 된다. 심지어 데이터 접근 기술에 따른 트랜잭션 구현체도 대부분 만들어두어서 가져다 사용하기만 하면 된다.

<img src="./imgs/트랜잭션/스프링의_트랜잭션_추상화.png"><br>

스프링 트랜잭션 추상화의 핵심은 `PlatformTransactionManager`인터페이스이다. (`org.springframework.transaction.PlatformTransactionManager`)

### PlatformTransactionManager 인터페이스

```java
package org.springframework.tranaction;

public interface PlatformTransactionManager extends TransactionManager {

    TransactionStatus getTransactin(@Nullable TransactionDefinition definition) throws TransactionException;

    void commit(TransactionStatus status) throws TransactionException;
    void rollback(TransactionStatus status) throws TransactionException;
}
```

- `getTransaction()`: 트랜잭션을 시작한다.
  - 이름이 `getTransaction()`인 이유는 기존에 이미 진행중인 트랜잭션이 있는 경우 해당 트랜재개션에 참여할 수 있기 때문이다.
  - 참고로 트랜잭션 참여, 전파에 대한 부분은 뒤에서 설명한다. 지금은 단순히 트랜잭션을 시작하는 것으로 이해하면 된다.
- `commit()`: 트랜잭션을 커밋한다
- `rollback()`: 트랜잭션을 롤백한다.

앞으로 `PlatformTransactionManager`인터페이스와 구현체를 포함해서 **트랜잭션 매니저**로 줄여서 이야기하겠다.

> [!NOTE]
> 스프링 5.3부터는 JDBC트랜잭션을 관리할 때 `DataSourceTransactionManager`를 상속받아서 약간의 기능을 확장한 `JdbcTransactionManager`를 제공한다. 둘의 기능 차이는 크지 않으므로 같은 것으로 이해하면 된다.

## 트랜잭션 동기화

스프링이 제공하는 트랜잭션 매니저는 크게 2가지 역할을 한다.

- **트랜잭션 추상화**
  - 트랜잭션 기술을 추상화 하는 부분은 앞에서 설명했다.
- **리소스 동기화**
  - 트랜잭션을 유지하려면 트랜잭션으 시작부터 끝까지 같은 데이터베이스 커넥션을 유지해야 한다. 결국 같은 커넥션을 동기화(맞추어 사용)하기 위해서 이전에는 파라미터로 커넥션을 전달하는 방법을 사용했다.
  - 파라미터로 커넥션을 전달하는 방법은 코드가 지저분해지는 것은 물론이고, 커넥션을 넘기는 메서드와 넘기지 않은 메서드를 중복해서 만들어야 하는 등 여러가지 단점들이 많다.

### 트랜잭션 매니저와 트랜잭션 동기화 매니저

<img src="./imgs/트랜잭션/트랜잭션_매니저와_트랜잭션_동기화_매니저.png"><br>

- 스프링은 **트랜잭션 동기화 매니저**를 제공한다. 이것은 쓰레드 로컬(`ThreadLocal`)을 사용해서 커넥션을 동기화 해준다. 트랜잭션 매니저는 내부에서 이 트랜잭션 동기화 매니저를 사용한다.
- 트랜잭션 동기화 매니저는 쓰레드 로컬을 사용하기 때문에 멀티쓰레드 상황에서 안전하게 커넥션을 동기화 할 수 있다. 따라서 커넥션이 필요하면 트랜잭션 동기화 매니저를 통해 커넥션을 획득하면 된다. 따라서 이전처럼 파라미터로 커넥션을 전달하지 않아도 된다.
- **동작 방식을 간단하게 설명하면 다음과 같다**
  1. 트랜잭션을 시작하려면 커넥션이 필요하다. 트랜잭션 매니저는 데이터소스를 통해 커넥션을 만들고 트랜잭션을 시작한다
  2. 트랜잭션 매니저는 트랜잭션이 사작된 커넥션을 트랜잭션 동기화 매니저에 보관한다
  3. 리포지토리는 트랜잭션 동기화 매니저에 보관된 커넥션을 꺼내서 사용한다. 따라서 파라미터로 커넥션을 전달하지 않아도 된다.
  4. 트랜잭션이 종료되면 트랜잭션 매니저는 트랜잭션 동기화 매니저에 보관된 커넥션을 통해 트랜잭션을 종료하고, 커넥션도 닫는다.

> [!TIP]
> **트랜잭션 동기화 매니저**<br>다음 트랜잭션 동기화 매니저 클래스를 열어보면 쓰레드 로컬을 사용하는 것을 확인할 수 있다.<br>`org.springframework.transaction.support.TransactionSynchronizationManager`<br><br>쓰레드 로컬을 사용하면 각각의 쓰레드마다 별도의 저장소가 부여된다. 따라서 해당 쓰레드만 해당 데이터에 접근할 수 있다.

## 트랜잭션 문제 해결 - 트랜잭션 매니저 1

이제 본격적으로 애플리케이션 코드에 트랜잭션 매니저를 적용해보자

### MemberRepositoryV3

```java
package hello.jdbc.repository;


/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {...}

    public Member findById(String memberId) throws SQLException {...}

    public void update(String memberId, int money) throws SQLException {...}

    public void delete(String memberId) throws SQLException {...}


    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //JdbcUtils.closeConnection(con);

        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {
        //Connection con = dataSource.getConnection();

        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
```

### 코드 분석

- **DataSourceUtils.getConnection()
  - `getConnection()`에서 `DataSourceUtils.getConnection()`를 사용하도록 변경된 부분을 특히 주의해야 한다.
  - `DataSourceUtils.getConnection()`는 다음과 같이 동작한다
    - **트랜잭션 동기화 매니저가 관리하는 커넥션이 있으면 해당 커넥션을 반환한다.
    - 트랜잭션 동기화 매니저가 관리하는 커넥션이 없는 경우 새로운 커넥션을 생성해서 반환한다.
- **DataSourceUtils.releaseConnection()**
  - `close()`에서 `DataSourceUtils.releaseConnection()`를 사용하도록 변경된 부분을 특히 주의해야 한다. 커넥션을 `con.close`를 사옹해서 직접 닫아버리면 커넥션이 유지되지 않는 문제가 발생한다. 이 커넥션은 이후 로직은 물론이고, 트랜잭션을 종료(커밋, 롤백)할 때 까지 살아있어야 한다.
  - `DataSourceUtils.releaseConnection()`을 사용하면 커넥션을 바로 닫는 것이 아니다.
    - 트랜잭션을 상요하기 위해 동기화된 커넥션은 커넥션을 닫지 않고 그래로 유지해준다.
    - 트랜잭션 동기화 매니저가 관리하는 커넥션이 없는 경우 해당 커넥션을 닫는다.

이제 트랜잭션 매니저를 사용하는 서비스 코드를 작성해보자.

### MemberServiceV3_1

```java
package hello.jdbc.service;

/**
 * 트랜잭션 - 트랜잭션 메니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    //private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransform(String fromId, String toId, int money) throws SQLException {

        //Connection con = dataSource.getConnection();

        TransactionStatus status = transactionManager.getTransaction(
                new DefaultTransactionDefinition()
        ); //트랜잭션 시작

        try {
            //con.setAutoCommit(false); //트랜잭션 시작

            //bizLogic(con, fromId, toId, money); //비즈니스 로직
            bizLogic(fromId, toId, money);

            transactionManager.commit(status); //성공시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status); //실패시 롤백

            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }


    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
```

- 이제 release 메서드는 삭제해도 된다. (트랜잭션 매니저 내부에서 커밋되거나 롤백될 때 커넥션을 정리해준다.)
- 이제 비즈니스 로직에서 Connection을 파라미터로 받을 필요가 없다.

### 코드 분석

- `private final PlatformTransactionManager transactionManager`
  - 트랜잭션 매니저를 주입 받는다. 지금은 JDBC기술을 사용하기 때문에 `DataSourceTransactionManager`구현체를 주입 받아야 한다.(서비스에서 직접 구현체를 넣어주면 OCP가 지켜지지 않으니, DI를 통해 외부에서 주입받을 것이다.)
- `transactionManager.getTransaction()`
  - 트랜잭션을 시작한다
  - `TransactionStatus status`를 반환한다. 여기에는 현재 트랜잭션의 상태 정보가 포함되어 있다. 이후 트랜잭션을 커밋, 롤백할 때 필요하다
- `new DefaultTransactionDefinition()`
  - 트랜잭션과 관련된 옵션을 지정할 수 있다. 자세한 내용은 뒤에서 설명한다
- `transactionManager.commit(status)`
  - 트랜잭션이 성공하면 이 로직이 호출해서 커밋하면 된다.
- `transactionManager.rollback(status)`
  - 문제가 발생하면 이 로직을 호출해서 트랜잭션을 롤백하면 된다.

### MemberServiceV3_1Test

```java
package hello.jdbc.service;

class MemberServiceV3_1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach
    void beforeEach() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                URL, USERNAME, PASSWORD
        );
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

        memberRepository = new MemberRepositoryV3(dataSource);
        memberService = new MemberServiceV3_1(transactionManager, memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransform(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);

    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        //when
        Assertions.assertThatThrownBy(
                        () -> memberService.accountTransform(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberEx.getMemberId());
        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(10000);
        Assertions.assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }
}
```

### 코드 분석

- `new DataSourceTransactionManager(dataSource)`
  - JDBC 기술을 사용하므로, JDBC용 트랜잭션 매니저(`DataSourceTransactionManager`)를 선택해서 서비스에 주입한다
  - 트랜잭션 매니저는 데이터소스를 통해 커넥션을 생성하므로 `DataSource`가 필요하다.

## 트랜잭션 문제 해결 - 트랜잭션 매니저 2

그림으로 트랜잭션 매니저의 전체 동작 흐름을 자세히 이해해보자.<br>
(이 매커니즘을 이해햐고 있어야 스프링 트랜잭션에 대해서 깊이있게 이해할 수 있다.)

### 트랜잭션 매니저1 - 트랜잭션 시작

<img src="./imgs/트랜잭션/트랜잭션_매니저1-트랜잭션_시작.png"><br>

- 클라이언트의 요청으로 서비스 로직을 실행한다.
1. 서비스 계층에서 `transactionManager.getTransaction()`을 호출해서 트랜잭션을 시작한다.
2. 트랜잭션을 시작하려면 먼저 데이터베이스 커넥션이 필요하다. 트랜잭션 매니저는 내부에서 데이터소스를 사용해서 커넥션을 생성한다.
3. 커넥션을 수동 커밋모드로 변경해서 실제 데이터베이스 트랜잭션을 시작한다.
4. (트랜잭션을 시작한) 커넥션을 트랜잭션 동기화 매니저에 보관한다.
5. 트랜잭션 동기화 매니저는 쓰레드 로컬에 커넥션을 보관한다. 따라서 멀티 쓰레드 환경에서 안전한게 커넥션을 보관할 수 있다.

### 트랜잭션 매니저2 - 로직 실행

<img src="./imgs/트랜잭션/트랜잭션_매니저2-로직_실행.png"><br>

6. 서비스는 비즈니스 로직을 실행하면서 리포지토리의 메서드들을 호출한다. 이때 커넥션을 이제는 파라미터로 전달하지 않는다.
7. 리포지토리 메서들은 트랜잭션이 시작된 커넥션이 필요하다. 리포지토리는 `DataSourceUtils.getConnection()`을 사용해서 트랜잭션 동기화 매니저에 보관된 커넥션을 꺼내서 사용한다. 이 과정을 통해서 자연스럽게 같은 커넥션을 사용하고, 트랜잭션도 유지된다,.
8. 획득한 커넥션을 사용해서 SQL을 데이터베이스에 전달해서 실행한다.

### 트랜잭션 매니저3 - 트랜잭션 종료

<img src="./imgs/트랜잭션/트랜잭션_매니저3-트랜잭션_종료.png"><br>

9. 비즈니스 로직이 끝나고 트랜잭션을 종료한다. 트랜잭션은 커밋하거나 롤백하면 종료된다
10. 트랜잭션을 종료하려면 동기화된 커넥션이 필요하다. 트랜잭션 동기화 매니저를 통해 동기화된 커넥션을 획득한다.
11. 획득한 커넥션을 통해 데이터베이스에 트랜잭션을 커밋하거나 롤백한다
12. 전체 리소스를 정리한다
    - 트랜잭션 동기화 매니저를 정리한다. 쓰레드 로컬은 사용 후 꼭 정리해야 한다.
    - `con.setAutoCommit(true)`로 되돌린다. 커넥션 풀을 고려해야 한다.
    - `con.close()`를 호출해서 커넥션을 종료한다. 커넥션 풀을 사용하는 경우 `con.close()`를 호출하면 커넥션 풀에 반환된다.

> [!NOTE]
> - 트랜잭션 추상화 덕분에 서비스 코드는 이제 JDBC 기술에 의존하지 않는다.<br>(PlatformTransactionManager에 의존한다.)
>   - 이후 JDBC에서 JPA로 변경해도 서비스 코드를 그대로 유지할 수 있다.
>   - 기술 변경시 의존관계 주입만 `DataSourceTransactionManager`에서 `JpaTransactionManager`로 변경해주면 된다.
>   - `java.sql.SQLException`이 아직 남아있지만 이 부분은 뒤에 예외 문제에서 해결하자.
> - 트랜잭션 동기화 매니저 덕분에 커넥션을 파라미터로 넘기지 않아도 된다.

> [!TIP]
> 여기서는 `DataSourceTransactionManager`의 동작 방식을 위주로 설명했다. 다른 트랜잭션 매니저는 해당 기술에 맞도록 변형되어서 동작한다.

## 트랜잭션 문제 해결 - 트랜잭션 템플릿

트랜잭션을 사용하는 로직을 살펴보면 다음과 같은 패턴이 반복되는 것을 확인할 수 있다.

**트랜잭션 사용 코드**<br>

```java
TransactionStatus status = transactionManager.getTransaction(
    new DefaultTransactionDefinition()); // 트랜잭션 시작

try {
    bizLogic(fromId, toId, money); //비즈니스 로직
    transactionManager.commit(status); //성공시 커밋
} catch (Exception e) {
    transactionManager.rollback(status); //실패시 롤백
    throw new IllegalStateException(e);
}
```

- 트랜잭션을 시작하고, 비즈니스 로직을 실행하고, 성공하면 커밋하고, 예외가 발생해서 실패하면 롤백한다.
- 다른 서비스에서 트랜잭션을 시작하려면 `try`, `catch`, `finally`를 포함한 성공시 커밋, 실패시 롤백 코드가 반복될 것이다.
- 이런 형태는 각각의 서비스에서 반복된다. 달라지는 부분은 비즈니스 로직 뿐이다.

이럴때 템플릿 롤백 패턴을 활용하면 이런 반복 문제를 깔끔하게 해결할 수 있다.

- (참고) 템플릿 콜백 패턴에 대해서 지금은 자세히 이해하지 못해도 괜찮다. 스프링이 `TransactionTemplate`이라는 편리한 기능을 제공하는구나 정도로 이해해도 된다.
- (참고) (템플릿 콜백 패턴으로 구현되어 있는) `TransactionTemplate`을 사용하면, 우리는 비즈니스 로직만 작성하면 된다. `TransactionTemplate`내부에서 트랜잭션을 시작하고, 성공하면 커밋, 실패하면 롤백하는 코들르 대신 처리해준다.

### 트랜잭션 템플릿

템플릿 콜백 패턴을 적용하려면 템플릿을 제공하는 클래스를 작성해야 하는데, 스프링은 `TransactionTemplate`라는 템플릿 클래스를 제공한다

**TransactionTemplate 참고**<br>
```java
public class TransactionTemplate {

    private PlatformTransactionManager transactionManager;

    public <T> T execute(TransactionCallback<T> action) {...}
    void executeWithoutResult(Consumer<TransactionStatus> action) {...}
}
```

- `execute()`: 응답 값이 있을 때 사용한다
- `executeWithoutResult()`: 응답 값이 없을 때 사용한다.

트랜잭션 템플릿을 사용해서 반복하는 부분을 제거해보자

### MemberServiceV3_2 생성

```java
/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_2 {

    //private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager,
                             MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransform(String fromId, String toId, int money) throws SQLException {

        txTemplate.executeWithoutResult((status) -> {
            try {
                bizLogic(fromId, toId, money); // 비즈니스로직
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }
    // ...
}
```

`TransactionTemplate`을 사용하려면 `transactionManager`가 필요하다. 생성자에서 `transactionManager`를 주입 받으면서 `TransactionTemplate`을 생성했다.

### 코드 분석

- 트랜잭션 템플릿 덕분에 트랜잭션을 시작하고, 커밋하거나 롤백하는 코드가 모두 제거되었다.
- 트랜잭션 템플릿의 기본 동작은 다음과 같다.
  - 비즈니스 로직이 정상 수행되면 커밋한다
  - 언체크 예외가 발생하면 롤백한다. 그 외의 경우 커밋한다.(체크 예외의 경우에는 커밋하는데, 이 부분은 뒤에서 설명한다)
- 코드에서 예외를 처리하기 위해 `try~catch`가 들어갔는데, `bizLogic()`메서드를 호출하면 `SQLException`체크 예외를 넘겨준다. 해당 람다에서 체크 예외를 밖으로 던질 수 없기 때문에 언체크 예외로 바꾸어 던지도록 예외를 전환했다.

### MemberServiceV3_2Test

```java
class MemberServiceV3_2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_2 memberService;

    @BeforeEach
    void beforeEach() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                URL, USERNAME, PASSWORD
        );
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

        memberRepository = new MemberRepositoryV3(dataSource);
        memberService = new MemberServiceV3_2(transactionManager, memberRepository);
    }
    // ...
}
```

> [!NOTE]
> - 트랜잭션 템플릿 덕분에, 트랜잭션을 사용할 때 반복하는 코드를 제거할 수 있었다.
> - 핮만 이곳은 서비스 로직인데 비즈니스 로직 뿐만 아니라 트랜잭션을 처리하는 기술 로직이 함께 포함되어 있다
> - 애플리케이션을 구성하는 로직을 핵심 기능과 부가 기능을 구분하자면 서비스 입장에서 비즈니스 로직은 핵심 기능이고, 트랜잭션은 부가 기능이다.
> - 이렇게 비즈니스 로직과 트랜잭션을 처리하는 기술 로직이 한 곳에 있으면 두 관심사를 하나의 클래스에서 처리하게 된다. 결과적으로 코드를 유지보수하기 여려워진다.
> - 서비스 로직은 가급적 핵심 비즈니스 로지만 있어야 한다. 하지만 트랜잭션 기술을 사용하려면 어쩔 수 없이 트랜잭션 코드가 나와야 한다. 어떻게 하면 이 문제를 해결할 수 있을까?

## 트랜잭션 문제 해결 - 트랜잭션 AOP 이해

- 지금까지 트랜잭션을 편리하게 처리하기 위해서 트랜잭션 추상화도 도입하고, 추가로 반복적인 트랜잭션 로직을 해결하기 위해 트랜잭션 템플릿도 도입했다.
- 트랜잭션 템플릿 덕분에 트랜잭션을 처리하는 반복 코드는 해결할 수 있었다. 하지만 서비스 계층에 순수한 비즈니스 로직만 남긴다는 목표는 아직 달성하지 못했다.
- 이럴때 스프링 AOP를 통해 프록시를 도입하면 문제를 깔끔하게 해결할 수 있다.
  - (참고) 스프링 AOP와 프록시에 대해서 지금은 자세히 이해하지 못해도 괜찮다. 지금은 `@Transactional`을 사용하면 스프링이 AOP를 사용해서 트랜잭션을 편리하게 처리해준다 정도로 이해해도 된다.

### 프록시를 통해 문제 해결

#### 프록시 도입 전

<img src="./imgs/트랜잭션/프록시_도입_전.png"><br>

프록시 도입하기 전에는 기존처럼 서비스의 로직에서 트랜잭션을 직접 시작한다.<br>
**서비스 계층의 트랜잭션 사용 코드 예시**<br>

```java
TransactionStatus status = transactionManager.getTransaction(
    new DefaultTransactionDefinition()); //트랜잭션 시작

try {
    bizLogic(fromId, toId, money); //비즈니스 로직
    transactionManager.commit(status); //성공시 커밋
} catch(Exception e) {
    transactionManager.rollback(status); // 실패시 롤백
    throw new IllegalStateException(e);
}
```

프록시 도입 전: 서비스에 비즈니스 로직과 트랜잭션 처리 로직이 함께 섞여있다.

#### 프록시 도입 후

<img src="./imgs/트랜잭션/프록시_도입_후.png"><br>

프록시를 사용하면 트랜잭션을 처리하는 객체와 비즈니스 로직을 처리하는 서비스 객체를 명확하게 분리할 수 있다.<br>
**트랜잭션 프록시 코드 예시**<br>
```java
public class TransactionProxy {
    private MemberService target;

    public void logic() {
        //트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(...);
        try {
            //실제 대상 호출
            target.logic();
            transactionManager.commit(status); //성공시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status); //실패시 롤백
            throw new IllegalStateException(e);
        }
    }
}
```

**트랜잭션 프록시 적용 후 서비스 코드 예시**<br>
```java
public class Service {
    public void logic() {
        //트랜잭션 관련 코드 제거, 순수 비즈니스 로직만 남음
        bizLogic(fromId, toId, money);
    }
}
```

프록시 도입 후: 트랜잭션 프록시(트랜잭션을 처리하는 프록시)가 트랜잭션을 모두 가져간다. 그리고 트랜잭션을 시작한 후에 실제 서비스 대신 호출한다. 트랜잭션 프록시 덕분에 서비스 계층에는 순수한 비즈니스 로직만 남길 수 있다.

### 트랜잭션이 제공하는 트랜잭션 AOP

스프링이 제공하는 AOP기능을 사용하면 프록시를 매우 편리하게 적용할 수 있다.

물론 스프링AOP를 지금 사용해서 트랜잭션을 처리해도 되지만, 트랜잭션은 매우 중요한 기능이고, 전세계 누구나 다 사용하는 기능이다. 스프링은 트랜잭션 AOP를 처리하기 위한 모든 기능을 제공한다. 스프링 부트를 사용하면 트랜잭션 AOP를 처리하기 위해 필요한 스프링 빈들도 자동을 등록해준다.

개발자는 트랜잭션 처리가 필요한 곳에 `@Transactional( org.springframework.transaction.annotation.Transactional )`애노테이션만 붙여주면 된다. 그러면 스프링의 트랜잭션 AOP는 이 애노테이션을 인식해서 트랜잭션 프록시를 적용해준다.

> [!TIP]
> 참고로 스프링 AOP를 적용하려면 어드바이저, 포인트컷, 어드바이스가 필요하다. 스프링은 트랜잭션 AOP 처리를 위해 다음 클래스를 제공한다. 스프링 부트를 사용하면 해당 빈들은 스프링 컨테이너에 자동으로 등록된다.<br>
> 어드바이저 `BeanFactoryTransactionAttributeSourceAdvisor`<br>
> 포인트컷: `TransactionAttributeSourcePointcut`<br>
> 어드바이스: `TransactionInterceptor`<br>

## 트랜잭션 문제 해결 - 트랜잭션 AOP 적용

앞서 배운 트랜잭션 AOP를 실제 적용해보자.

### MemberServiceV3_3

```java
package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }


    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
```

- 순수한 비즈니스 로직만 남기고, 트랜잭션 관련 코드는 모두 제거했다
- 스프링이 제공하는 트랜잭션 AOP를 적용하기 위해 `@Transactional`애노테이션을 추가했다.
- `@Transactional` 애노테이션은 메서드에 붙어도 되고, 클래스에 붙여도 된다. 클래스에 붙이면 외부에서 호출 가능한 `public`메서드가 AOP 적용대상이 된다.

### 테스트 코드

```java
@Slf4j
@SpringBootTest
class MemberServiceV3_3Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepository;
    @Autowired
    private MemberServiceV3_3 memberService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource());
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }
    // .....
}
```

(참고) @BeforeEach는 이제 주석처리 하였다.

### 코드 분석

- `@SpringBootTest`: 스프링 AOP를 적용하려면 스프링 컨테이너가 필요하다. 이 애노테이션이 있으면 테스트시 스프링 부트를 통해 스프링 컨테이너를 생성한다. 그리고 테스트에서 `@Autowired`등을 통해 스프링 컨테이너가 관리하는 빈들을 사용할 수 있다.
- `@TestConfiguration`: 테스트 안에서 내부 설정 클래스를 만들어서 사용하면서 이 애노테이션을 붙이면, 스프링 부트가 자동으로 만들어주는 빈들에 추가로 필요한 스프링 빈들을 등록하고 테스트를 수행할 수 있다.
- `TestConfig`
  - `DataSource`: 스프링에서 기본으로 사용할 데이터소스를 스프링 빈으로 등록한다. 추가로 트랜잭션 매니저에서도 사용한다.
  - `DataSourceTransactionManager`: 트랜잭션 매니저를 스프링 빈으로 등록한다.
    - 스프링이 제공한느 트랜잭션 AOP는 스프링 빈에 등록된 트랜잭션 매니저를 찾아서 사용하기 때문에 트랜잭션 매니저를 스프링 빈으로 등록해두어야 한다.

**참고**<br>

```java
@Test
void AopCheck() {
    log.info("memberService class={}", memberService.getClass());
    log.info("memberRepository class={}", memberRepository.getClass());
    assertThat(AopUtils.isAopProxy(memberService)).isTrue();
    assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
}
```

- 정상적으로 실행됨을 확인할 수 있다.
- 먼저 AOP 프록시가 적용되었는지 확인해보자. `AopCheck()`의 실행결과를 보면 `memberService`에 `EnhancerBySpringCGLIb...`라는 부분을 통해 프록시(CGLIB)가 적용된 것을 확인할 수 있다. `memberRepository`에는 AOP를 적용하지 않았기 때문에 프록시가 적용되지 않는다.
- 나머지 테스트 코드들을 실행해보면 트랜잭션이 정상 수행되고, 실패시 정상 롤백된 것을 확인할 수 있다.

## 트랜잭션 문제 해결 - 트랜잭션 AOP 정리

앞서 학습한 트랜잭션 AOP를 정리해보자.<br>(트랜잭션 AOP가 적용된 전체 흐름을 그림으로 정리해보자)<br>

### 트랜잭션 AOP적용 전체 흐름

<img src="./imgs/트랜잭션/트랜잭션_AOP적용_전체_흐름.png"><br>

- (참고) `@Transactional`애노테이션이 적용되어 있으면, 스프링은 트랜잭션 로직을 처리해주는 AOP프록시를 생성한다. 여기에서 트랜잭션 로직이 처리되고 실제 서비스가 호출된다.(클라이언트에서 의존관계 주입시에도 실제 서비스가 아닌 AOP 프록시가 주입된다.)
- 1: 클라이언트가 AOP프록시를 호출한다
- 2-3: 프록시 내부 코드에서는 트랜잭션 매니저를 통해서 트랜잭션을 시작한다. (스프링 빈으로 등록된 트랜잭션 매니저를 가져다 사용한다.)
- 4-7: 트랜잭션 매니저로 트랜잭션을 시작할 때 내부에서는 다음과 같이 처리한다.(dataSource를 가지고 커넥션을 획득한다. 그리고 setAutoCommit(false)를 통해서 수동 커밋 모드로 만들고 트랜잭션을 시작한다. 그리고 이 커넥션을 동기화하기 위해서 트랜잭션 동기화 매니저에 보관한다.)
- 8: 그런 다음, AOP프록시에 실제 서비스 로직을 호출한다. 비즈니스 로직에서는 리포지토리를 호출한다
- 9: 리포지토리에서는 트랜잭션 동기화 매니저에 있는 커넥션을 획득한다.
- 비즈니스 로직 처리가 끝나면, 성공시 commit, (Runtime) 예외가 발생하면 롤백하는 로직이 수행된다.
- 이런 모든 부분을 `@Transactional` 애노테이션 하나로 스프링이 해결해주는 것이다.

### 선언적 트랜잭션 관리 vs 프로그래밍 방식 트랜잭션 관리

- 선언적 트랜잭션 과리(Declarative Transaction Management)
  - `@Transactional` 애노테이션 하나만 선언해서 매우 편리하게 트랜잭션을 적용하는 것을 선언적 트랜잭션 관리라 한다.
  - 선언적 트랜잭션 관리는 과거 XML에 설정하기도 했다. 이름 그대로 해당 로직에 트랜잭션을 적용하겠다라고 어딘가에 선언하기만 하면 트랜잭션이 적용되는 방식이다.
- 프로그래밍 방식의 트랜잭션 관리(Programmatic Transaction Management)
  - 트랜잭션 매니저 또는 트랜잭션 템플릿 등을 사용해서 트랜잭션 관련 코드를 직접 작성하는 것을 프로그래밍 방식의 트랜잭션 관리라 한다.

- 선언적 트랜잭션 관리가 프로그래밍 방식에 비해 훨씬 간편하고 실용적이기 때문에 실무에서는 대부분 선언적 트랜잭션 관리를 사용한다.
- 프로그래밍 방식의 트랜잭션 관리는 스프링 컨테이너나 스프링 AOP 기술 없이 간단히 사용할 수 있지만 실무에서는 대부분 스프링 컨테이너와 스프링 AOP를 사용하기 때문에 거의 사용되지 않는다.
- 프로그래밍 방식 트랜잭션 관리는 테스트 시에 가끔 사용될 때는 있다.

### 정리

- 스프링이 제공하는 선언적 트랜잭션 관리 덕분에 드디어 트랜잭션 관련 코드를 순수한 비즈니스 로직에서 제거할 수 있었다.
- 개발자는 트랜잭션이 필요한 곳에 `@Transactional`애노테이션 하나만 추가하면 된다. 나머지는 스프링 트랜잭션 AOP가 자동으로 처리해준다.
- `@Transactional`애노테이션의 자세한 사용법은 뒤에서 설명한다. 지금은 전체 구조를 이해하는데 초점을 맞추자.

## 스프링 부트의 자동 리소스 등록

이번에는 스프링 부트의 자동 리소스 등록에 대해서 알아보자.<br>
스프링 부트가 등장하기 이전에는 데이터소싀와 트랜잭션 매니저를 개발자가 직접 스프링 빈으로 등록해서 사용했다.<br>
그런데 스프링 부트로 개발을 시작한 개발자라면 데이터소스나 트랜잭션 매니저를 직접 등록한 적이 없을 것이다.

이 부분을 잠시 살펴보자.

### 데이터소스와 트랜잭션 매니저를 스프링 빈으로 직접 등록

```java
@Bean
Datasource dataSource() {
    return new DriverManagerDataSource(
        URL, USERNAME, PASSWORD
    );
}

@Bean
DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(datasource());
}
```
- 기존에는 이렇게 데이터소스와 트랜잭션 매니저를 직접 스프링 빈으로 등록해야 했다. 그런데 스프링 부트가 나오면서 많은 부분이 자동화되었다. (더 오래전에 스프링을 다루어왔다면 해당 부분을 주로 XML로 등록하고 관리했을 것이다.)


### 데이터소스 - 자동 등록

- 스프링 부트는 데이터소스(`DataSource`)를 스프링 빈에 자동으로 등록한다
- 자동으로 등록되는 스프링 빈 이름: `dataSource`
- 스프링 부트는 다음과 같이 `application.properties`에 있는 속성을 사용해서 `DataSource`를 생성한다. 그리고 스프링 빈에 등록한다.

`application.properties`<br>
```properties
spring.datasource.url=jdbc:h2:tcp://localhost/~/jdbc
spring.datasource.username=sa
spring.datasource.password=
```

- 스프링 부트가 기본으로 생성하는 데이터 소스는 커넥션풀을 제공하는 `HikariDataSource`이다. 커넥션풀과 관련된 설정도 `application.properties`를 통해서 지정할 수 있다.
- `spring.datasource.url`속성이 없으면 내장 데이터베이스(메모리 DB)를 생성하려고 시도한다.

참고로 개발자가 직접 데이터소스를 빈으로 등록하면 스프링 부트는 데이터소스를 자동으로 등록하지 않는다.

### 트랜잭션 매니저 - 자동 등록

- 스프링 부트는 적절한 트랜재겻ㄴ 매니저(`PlatformTransactionManager`)를 자동으로 스프링 빈에 동록한다.
- 자동으로 등록되는 스프링 빈 이름: `transactionManager`
- 어떤 트랜잭션 매니저(구현체)를 선택할지(스프링 빈으로 등록할지)는 현재 등록된 라이브러리를 보고 판단하는데, JDBC를 기술로 사용하면 `JpaTransactionManager`를 빈으로 등록한다. 둘다 사용하는 경우 `JpaTransactionManager`를 등록한다. 참고로 `JpaTransactionManager`는 `DataSourceTransactionManager`가 제공하는 기능도 대부분 지원한다.
- 참고로 개발자가 직접 트랜잭션 매니저를 빈으로 등록하면 스프링 부트는 트랜잭션 매니저를 자동으로 등록하지 않는다.

### 데이버소스, 트랜잭션 매니저 직접 등록

```java
@TestConfiguration
static class TestConfig {
    @Bean
    DataSource dataSource() {
        return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Bean
    DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    MemberRepositoryV3 memberRepositoryV3() {
        return new MemberRepositoryV3(dataSource());
    }

    @Bean
    MemberServiceV3_3 memberServiceV3_3() {
        return new MemberServiceV3_3(memberRepositoryV3());
    }
}
```

이전에 작성한 코드이다. 이렇게 데이터소스와 트랜잭션 매니저를 직접 등록하면 스프링 부트는 데이터소스와 트랜잭션 매니저를 자동으로 등록하지 않는다.

### 데이터 소스와 트랜잭션 매니저 자동 등록

#### MemberServiceV3_4Test

```java
@Slf4j
@SpringBootTest
class MemberServiceV3_4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepository;
    @Autowired
    private MemberServiceV3_3 memberService;

    @TestConfiguration
    static class TestConfig {
        /*
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

         */

        public final DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource);
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }
```

- 스프링 컨테이너에 등록된 스프링 빈을 주입받는다. (스프링 부트가 만들어준 데이터소스 빈을 주입, 참고로 @Autowired로 받아도 된다.)
- 기존(`MemberServiceV3_3Test`)과 같은 코드이고 `TestConfig`부분만 다르다.
- 데이터소스와 트랜잭션 매니저를 스프링 빈으로 등록하는 코드가 생략되었다. 따라서 스프링 부트가 `application.properties`에 지정된 속성을 참고해서 데이터소스와 트랜잭션 매니저를 자동으로 생성해준다.

> [!TIP]
> - 데이터소스와 트랜잭션 매니저는 스프링 부트가 제공하는 자동 빈 등록 기능을 사용하는 것이 편리하다
> - 추가로 `application.properties`를 통해 설정도 편리하게 할 수 있다.
> - 스프링 부트의 데이터 소스 자동 등록에 대한 자세한 내용은 다음 스프링 부트 공식 메뉴얼을 참고하자
>   - https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.datasource.production
> - 자세한 설명 속성은 다음을 참고하자
>   - https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html

이번에는 3가지 문제(트랜잭션 문제, 예외 누수 문제, JDBC반복 문제)중, 스프링이 트랜잭션과 관련된 문제들을 어떻게 해결하는지 알아보았다.<br>
다음 섹션에서는 예외 누수 문제를 어떻게 해결하는지 알아보자.

# 자바 예외 이해

## 예외 계층

## 예외 기본 규칙

## 체크 예외 기본 이해

## 언체크 예외 기본 이해

## 체크 예외 활용

## 언체크 예외 활용

## 예외 포함과 스텍 트레이스
