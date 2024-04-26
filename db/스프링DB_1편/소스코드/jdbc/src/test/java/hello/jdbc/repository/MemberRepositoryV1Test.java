package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
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
        //기본 DriverManager를 통한 항상 새로운 커넥션을 획득
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(
        //        URL, USERNAME, PASSWORD
        //);
        //repository = new MemberRepositoryV1(dataSource);

        //커넥션 풀링: HikariProxyConnection -> JdbcConnection
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

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