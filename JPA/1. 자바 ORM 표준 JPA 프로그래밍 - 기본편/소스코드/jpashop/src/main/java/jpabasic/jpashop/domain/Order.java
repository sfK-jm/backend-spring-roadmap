package jpabasic.jpashop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS")
public class Order {

    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @Column(name = "MEMBER_ID")
    private Long memberid;

    private LocalDateTime orderdate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
