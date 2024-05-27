package jpabasic.hellojpa.jpashop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "JPASHOP_ORDERS")
public class jpashopOrder {

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private jpashopMember member;

    @OneToOne
    @JoinColumn(name = "DELIVERY_ID")
    private jpashopDelivery delivery;

    @OneToMany(mappedBy = "order")
    private List<jpashopOrderItem> orderItems = new ArrayList<>();


    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;


    public void addOrderItem(jpashopOrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public jpashopMember getMember() {
        return member;
    }

    public void setMember(jpashopMember member) {
        this.member = member;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }


}
