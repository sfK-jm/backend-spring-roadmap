package jpabasic.hellojpa.ex04;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class JpaMain04 {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Member04 member = new Member04();
            member.setId(2L);
            member.setUsername("B");
            member.setAge(10);
            member.setRoleType(RoleType.ADMIN);

            System.out.println("memberID = " + member.getId());
            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            System.out.println("e = " + e);
            System.out.println("====롤백 수행==");
        } finally {
            em.close();
        }
        emf.close();
    }
}
