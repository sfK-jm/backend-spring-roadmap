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

            String jpql = "select m from Member m join fetch m.team";
            List<Member> members = em.createQuery(jpql, Member.class)
                    .getResultList();

            for (Member member : members) {
                //페치 조인으로 회원과 팀을 함께 조회해서 지연로딩 X
                System.out.println("username = " + member.getUsername() + ", " +
                        "teamName = " + member.getTeam().getName());
            }

            em.flush();
            em.clear();

            System.out.println("========================================");

            String query = "select t from Team t join fetch t.members where t.name = '팀1'";
            List<Team> teams = em.createQuery(query, Team.class).getResultList();

            for (Team team : teams) {
                System.out.println("teamname = " + team.getName() + ", team = " + team);
                for (Member member : team.getMembers()) {
                    //페치 조인으로 팀과 회원을 함께 조회해서 지연로딩 발생 안함
                    System.out.println("-> username = " + member.getUsername() + ", member = " + member);
                }
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
