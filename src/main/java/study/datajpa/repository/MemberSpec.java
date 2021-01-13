package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.*;

/**
 * Created by morrie kim on 2020/12/18.
 */
public class MemberSpec {
    public static Specification<Member> teamName(final String teamName) {
        return (Specification<Member>) (root, query, builder) -> {

            if (StringUtils.isEmpty(teamName)) {
                return null;
            }


            Join<Member, Team> t = root.join("team", JoinType.INNER); // 회원과 조인
            return builder.equal(t.get("name"), teamName);

        };
    }

    public static Specification<Member> userName(final String userName) {
        return (Specification<Member>) (root, query, builder) ->
            builder.equal(root.get("userName"), userName);
    }
}
