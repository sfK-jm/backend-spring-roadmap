package jpabasic.hellojpa.ex05;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team05 {

    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    List<Member05> members = new ArrayList<>();

    //Getter, Setter

    public List<Member05> getMembers() {
        return members;
    }

    public void setMembers(List<Member05> members) {
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
