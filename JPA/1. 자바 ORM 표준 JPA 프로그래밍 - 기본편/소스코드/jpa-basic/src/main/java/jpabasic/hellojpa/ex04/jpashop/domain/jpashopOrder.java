package jpabasic.hellojpa.ex04.jpashop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "JPASHOP_ORDERS")
public class jpashopOrder {

    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;



}
