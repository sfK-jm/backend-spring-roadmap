package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    public void 지연로딩_확인() {
        //given

        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        //when

        //findAll을 엔티티그래프를 이용해서
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            //Hibernate 기능으로 확인
            System.out.println("Hibernate으로 확인: " + Hibernate.isInitialized(member.getTeam()));

            //JPA표준 방법으로 확인
            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            System.out.println("JPA표준으로 확인: " + util.isLoaded(member.getTeam()));

            System.out.println(member.getTeam().getName());

        }
    }

    @Test
    public void 엔티티그래프_이용() throws Exception {
        //given

        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findMemberEntityGraph();

        //then
        for (Member member : members) {
            //Hibernate 기능으로 확인
            System.out.println("Hibernate으로 확인: " + Hibernate.isInitialized(member.getTeam()));

            //JPA표준 방법으로 확인
            PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            System.out.println("JPA표준으로 확인: " + util.isLoaded(member.getTeam()));

            System.out.println(member.getTeam().getName());

        }
    }

}