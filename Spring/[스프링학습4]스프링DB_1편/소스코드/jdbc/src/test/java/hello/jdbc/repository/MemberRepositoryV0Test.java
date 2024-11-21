package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

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