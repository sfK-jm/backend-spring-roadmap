package hellojpa.jpql;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("memberA");
            member.setAge(10);

            Member member1 = new Member();
            member.setUsername("memberB");
            em.persist(member1);

            member.setTeam(team);
            team.getMembers().add(member);

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("team.getMembers() = " + team.getMembers());
            for (Member m : team.getMembers()) {
                System.out.println("m = " + m);
            }

            String query = "SELECT m FROM Member m JOIN m.team t";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();


            System.out.println("result.size() = " + result.size());
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
