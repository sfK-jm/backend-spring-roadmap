package study.data_jpa.domain;

import lombok.Data;
import study.data_jpa.entity.Member;

@Data
public class MemberDto {
    private Long id;
    private String username;

    public MemberDto(Member m) {
        this.id = m.getId();
        this.username = m.getUsername();
    }
}
