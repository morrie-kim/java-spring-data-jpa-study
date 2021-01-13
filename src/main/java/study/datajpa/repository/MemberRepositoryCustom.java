package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

/**
 * Created by morrie kim on 2020/12/13.
 */
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
