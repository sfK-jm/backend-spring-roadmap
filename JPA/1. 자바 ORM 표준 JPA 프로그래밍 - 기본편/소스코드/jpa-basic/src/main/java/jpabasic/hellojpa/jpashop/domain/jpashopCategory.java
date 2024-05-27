package jpabasic.hellojpa.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "JPASHOP_CATEGORY")
public class jpashopCategory {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private jpashopCategory parent;

    @OneToMany(mappedBy = "parent")
    private List<jpashopCategory> child = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "CATEGORY_ITEM",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private List<jpashopItem> items = new ArrayList<>();
}
