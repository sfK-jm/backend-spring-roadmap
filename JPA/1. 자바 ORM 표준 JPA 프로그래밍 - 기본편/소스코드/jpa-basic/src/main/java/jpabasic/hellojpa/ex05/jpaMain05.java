package jpabasic.hellojpa.ex05;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class jpaMain05 {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //팀저장
            Team05 team = new Team05();
            team.setName("TeamA");
            em.persist(team);

            //회원저장
            Member05 member = new Member05();
            member.setName("member1");
            member.setTeam(team); //단방향 연관관계 설정, 참조 저장
            em.persist(member);

            em.flush();
            em.clear();

            //조회
            Team05 findTeam = em.find(Team05.class, team.getId());

            int memberSize = findTeam.getMembers().size();
            System.out.println("memberSize = " + memberSize);
            tx.commit();
        } catch (Exception e) {
            System.out.println("===롤백 수행===");
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
