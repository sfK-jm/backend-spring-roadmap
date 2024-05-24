package jpabasic.hellojpa.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabasic.hellojpa.jpashop.domain.OrderStatus;
import jpabasic.hellojpa.jpashop.domain.jpashopMember;
import jpabasic.hellojpa.jpashop.domain.jpashopOrder;
import jpabasic.hellojpa.jpashop.domain.jpashopOrderItem;

public class jpashopMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {



            tx.commit();
        } catch (Exception e) {
            System.out.println("=== 롤백 수행 ===");
            System.out.println("e = " + e);
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
