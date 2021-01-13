package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

/**
 * Created by morrie kim on 2020/12/12.
 */
@Data
public class MemberDto {

    private Long id;
    private String userName;
    private String teamName;

    public MemberDto(Long id, String userName, String teamName) {
        this.id = id;
        this.userName = userName;
        this.teamName = teamName;
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.userName = member.getUserName();
    }
}
