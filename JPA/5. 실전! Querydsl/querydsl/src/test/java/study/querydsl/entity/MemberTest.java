package study.querydsl.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
@Commit
class MemberTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

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
//        em.flush();
//        em.clear();
    }

    @Test
    public void testEntity() {
        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    public void 대량_데이터_수정() {
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();


        em.clear();

        System.out.println("count = " + count);

        List<Member> members = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member m : members) {
            System.out.println("m = " + m);
        }
    }


    @Test
    public void 숫자_1더하기() {
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
                .execute();

        em.flush();
        em.clear();

        System.out.println("count = " + count);
    }

    @Test
    public void 한번에_대량_삭제() {
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();

        System.out.println("count = " + count);
        
        em.flush();
        em.clear();
        List<Member> members = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member m : members) {
            System.out.println("m = " + m);
        }
    }
}