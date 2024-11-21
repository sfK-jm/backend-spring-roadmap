package hellojpa.jpql;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member1 = new Member();
            member1.setUsername("회원1");

            Member member2 = new Member();
            member2.setUsername("회원2");

            Member member3 = new Member();
            member3.setUsername("회원3");

            Team team1 = new Team();
            team1.setName("팀1");

            Team team2 = new Team();
            team2.setName("팀2");

            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
            em.persist(team1);
            em.persist(team2);

            member1.setTeam(team1);
            member2.setTeam(team1);
            member3.setTeam(team2);

            em.flush();
            em.clear();

            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();

            for (Member member : resultList) {
                System.out.println("member = " + member);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
