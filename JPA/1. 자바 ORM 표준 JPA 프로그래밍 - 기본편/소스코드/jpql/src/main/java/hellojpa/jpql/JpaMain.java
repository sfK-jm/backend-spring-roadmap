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
            member1.setUsername("관리자1");

            Member member2 = new Member();
            member2.setUsername("관리자2");

            Team team1 = new Team();
            team1.setName("TeamA");

            em.persist(member1);
            em.persist(member2);
            em.persist(team1);

            member1.setTeam(team1);
            member2.setTeam(team1);

            em.flush();
            em.clear();

            List<String> resultList = em.createQuery("select m.username from Team t join t.members m", String.class)
                    .getResultList();

            for (String s : resultList) {
                System.out.println("s = " + s);
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
