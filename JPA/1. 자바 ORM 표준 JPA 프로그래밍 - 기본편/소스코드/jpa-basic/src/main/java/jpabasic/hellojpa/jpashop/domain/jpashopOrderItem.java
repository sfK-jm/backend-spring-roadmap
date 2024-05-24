package jpabasic.hellojpa.jpashop.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "JPASHOP_ORDERITEM")
public class jpashopOrderItem {

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

//    @Column(name = "ORDER_ID")
//    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID")
    private jpashopOrder order;

//    @Column(name = "ITEM_ID")
//    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private jpashopItem item;

    private int orderPrice;

    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public jpashopOrder getOrder() {
        return order;
    }

    public void setOrder(jpashopOrder order) {
        this.order = order;
    }

    public jpashopItem getItem() {
        return item;
    }

    public void setItem(jpashopItem item) {
        this.item = item;
    }

    public int getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
