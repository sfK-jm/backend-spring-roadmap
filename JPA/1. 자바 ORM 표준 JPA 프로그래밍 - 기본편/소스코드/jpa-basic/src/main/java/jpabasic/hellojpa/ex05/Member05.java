package jpabasic.hellojpa.ex05;

import jakarta.persistence.*;

@Entity
public class Member05 {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team05 team;

    //Getter, Setter

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

    public Team05 getTeam() {
        return team;
    }

    public void setTeam(Team05 team) {
        this.team = team;
    }
}
