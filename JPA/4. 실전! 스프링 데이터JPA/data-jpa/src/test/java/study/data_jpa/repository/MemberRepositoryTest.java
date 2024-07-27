package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.List;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        Assertions.assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    @DisplayName("@Query, 리포리토리 메소드에 쿼리 정의하기")
    public void query_method() {
        Member member = new Member("memberA", 20);
        memberRepository.save(member);

        List<Member> findMember = memberRepository.findUser("memberA", 20);

        Assertions.assertThat(findMember.get(0)).isEqualTo(member);
    }

    @Test
    public void findByUsername() {
        Member member = new Member("memberA", 5);
        memberRepository.save(member);

        List<Member> findMember = memberRepository.findByUsername("memberA");

        Assertions.assertThat(findMember.get(0)).isEqualTo(member);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member memberA = new Member("memberA", 100);
        Member memberB = new Member("memberA", 30);
        Member memberC = new Member("memberA", 20);
        Member memberD = new Member("memberA", 10);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);
        memberRepository.save(memberD);

        List<Member> findMember = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 20);

        for (Member member : findMember) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void findUsernameList() {
        Member member1 = new Member("memberA", 10);
        Member member2 = new Member("memberB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> MemberList = memberRepository.findUsernameList();

        for (String s : MemberList) {
            System.out.println(s);
        }
    }

    @Test
    public void findMemberDto() {
        Team teamA = new Team("TeamA");
        teamRepository.save(teamA);

        Member memberA = new Member("memberA", 10, teamA);
        memberRepository.save(memberA);

        Member memberB = new Member("memberB", 20, teamA);
        memberRepository.save(memberB);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }
}