package jpabasic.hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Set;

public class JpaMain {

    public static void main(String[] args) {

        //엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager(); //엔티티 매니저 생성

        EntityTransaction tx = em.getTransaction(); //트랜잭션 기능 획득

        try {
            tx.begin(); //트랜잭션 시작

            Member member = new Member();
            member.setName("member1");

            member.setHomeAddress(new Address("homeCity", "street", "zipcode"));

            member.getFavoriteFoods().add("chicken");
            member.getFavoriteFoods().add("bibimbap");

            member.getAddressHistory().add(new Address("old1", "street", "zipcode"));
            member.getAddressHistory().add(new Address("old2", "street", "zipcode"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("===조회===");
            Member findMember = em.find(Member.class, member.getId());

            List<Address> addressHistory = findMember.getAddressHistory();

            Set<String> favoriteFoods = findMember.getFavoriteFoods();

            for (Address address : addressHistory) {
                System.out.println("address = " + address.getCity());
            }

            for (String favoriteFood : favoriteFoods) {
                System.out.println("favoriteFood = " + favoriteFood);
            }

            System.out.println("===수정===");

            Address a = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("new city", a.getStreet(), a.getZipcode()));

            findMember.getFavoriteFoods().remove("chicken");
            findMember.getFavoriteFoods().add("치킨");

            findMember.getAddressHistory().remove(new Address("old1", "street", "zipcode"));
            findMember.getAddressHistory().add(new Address("new1", "street", "zipcode"));


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
