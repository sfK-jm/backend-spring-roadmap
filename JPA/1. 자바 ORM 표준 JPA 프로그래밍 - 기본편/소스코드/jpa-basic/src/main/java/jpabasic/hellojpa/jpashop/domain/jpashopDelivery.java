package jpabasic.hellojpa.jpashop.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "JPASHOP_DELIVERY")
public class jpashopDelivery {
    @Id
    @GeneratedValue
    private Long id;

    private String city;
    private String street;
    private String zipcode;
    private DeliveryStatus status;

    @OneToOne(mappedBy = "delivery")
    private jpashopOrder order;
}
