package jpabasic.jpashop;

import jakarta.persistence.*;
import jpabasic.jpashop.domain.Member;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        //엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager(); //엔티티 매니저 생성

        EntityTransaction tx = em.getTransaction(); //트랜잭션 기능 획득

        try {
            tx.begin(); //트랜잭션 시작

            Member member = new Member();
            member.setAge(23);

            em.persist(member);

            em.flush();
            em.clear();

            //검색
            String jpql = "select m from Member m where m.age > 18";

            List<Member> result = em.createQuery(jpql, Member.class)
                    .getResultList();

            for (Member m : result) {
                System.out.println("m = " + m);
            }

            tx.commit(); //트랜잭션 커밋
        } catch (Exception e) {
            //e.printStackTrace();
            tx.rollback(); //트랜잭션 롤백
        } finally {
            em.close(); //엔티티 매니저 종료
        }

        emf.close(); //엔티티 매니저 팩토리 종료
    }
}
