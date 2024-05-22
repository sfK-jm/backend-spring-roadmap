package jpabasic.hellojpa.ex01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JpaMain01 {

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
        Member01 member = new Member01();
        member.setId(id);
        member.setUsername("unknown");
        member.setAge(2);

        //등록
        em.persist(member);

        //수정
        member.setAge(20);

        //한 건 조회
        Member01 findMember = em.find(Member01.class, id);
        System.out.println("findMember = " + findMember.getUsername() + ", age=" + findMember.getAge());

        //목록 조회
        List<Member01> members = em.createQuery("select m from Member01 m", Member01.class).getResultList();
        for (Member01 m : members) {
            System.out.println("member.name = " + m.getUsername());
        }
    }
}
