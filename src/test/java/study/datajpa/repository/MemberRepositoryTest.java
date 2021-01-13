package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by morrie kim on 2020/08/17.
 */
@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired MemberQueryRepository memberQueryRepository;

    @Autowired EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장

    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUserNameAndAgeGreaterThen() {
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUserNameAndAgeGreaterThan("member", 15);

        assertThat(result.get(0).getUserName()).isEqualTo("member");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> results = memberRepository.findByUserName("member");
        Member findMember = results.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> results = memberRepository.findUser("member", 10);
        Member findMember = results.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void findUserNameList() {
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> results = memberRepository.findUserNameList();

        assertThat(results.get(0)).isEqualTo(member1.getUserName());
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("member", 10);
        memberRepository.save(member1);

        List<MemberDto> memberDtoList = memberRepository.findMemberDto();

        for (MemberDto dto : memberDtoList) {
            System.out.println("dto : " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> results = memberRepository.findByNames(Arrays.asList("member1", "member2"));

        for (Member dto : results) {
            System.out.println("dto : " + dto.getUserName());
        }
    }

    @Test
    public void testReturnType() {
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> results = memberRepository.findListByUserName("member1");
        for (Member dto : results) {
            System.out.println("dto : " + dto.getUserName());
        }

        Member result = memberRepository.findMemberByUserName("member1");
        System.out.println("dto : " + result.getUserName());

        Optional<Member> result2 = memberRepository.findOptionalByUserName("memqqber1234");
        System.out.println("dto : " + result2);

    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member : " + member);
        }

        System.out.println("totalElements : " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void pagingSlice() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent();
//        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member : " + member);
        }

//        System.out.println("totalElements : " + totalElements);

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void pagingList() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        List<Member> page = memberRepository.findListByAge(age, pageRequest);

        assertThat(page.size()).isEqualTo(3);
    }

    @Test
    public void pagingV2() {
        // count query 분리(성능 향상을 위해서)
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Page<Member> page = memberRepository.findV2ByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUserName(), member.getTeam().getName()));

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member : " + member);
        }

        System.out.println("totalElements : " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        // bulk 연산이후에는 실제 DB와 영속성 컨텍스트 내의 값이 다르므로 캐시를 삭제한다.
//        em.flush();
//        em.clear();

        List<Member> results = memberRepository.findByUserName("member5");
        Member member5 = results.get(0);
        System.out.println("member5 : " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);

    }

    @Test
    public void findMemberLaze() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();

        //when
        //select only Member
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member : " + member.getUserName());
            System.out.println("member.teamClass : " + member.getTeam().getClass());
            System.out.println("member.teamName : " + member.getTeam().getName()); // getName() 호출 시, Team 객채를 위해 쿼리 호출
        }
    }

    @Test
    public void findMemberFetchJoin() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();

        //when
        //select only Member
        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member : " + member.getUserName());
            System.out.println("member.teamClass : " + member.getTeam().getClass());
            System.out.println("member.teamName : " + member.getTeam().getName()); // getName() 호출 시, Team 객채를 위해 쿼리 호출
        }
    }

    @Test
    public void findMemberEntityGraph() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();

        //when
        //select only Member
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member : " + member.getUserName());
            System.out.println("member.teamClass : " + member.getTeam().getClass());
            System.out.println("member.teamName : " + member.getTeam().getName()); // getName() 호출 시, Team 객채를 위해 쿼리 호출
        }
    }

    @Test
    public void findMemberEntityGraphUserName() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member1", 20, teamB));

        em.flush();
        em.clear();

        //when
        //select only Member
        List<Member> members = memberRepository.findEntityGraphByUserName("member1");

        for (Member member : members) {
            System.out.println("member : " + member.getUserName());
            System.out.println("member.teamClass : " + member.getTeam().getClass());
            System.out.println("member.teamName : " + member.getTeam().getName()); // getName() 호출 시, Team 객채를 위해 쿼리 호출
        }
    }

    @Test
    public void findMemberNamedEntityGraphUserName() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member1", 20, teamB));

        em.flush();
        em.clear();

        // 쿼리가 간단하면, EntityGraph 사용
        // 쿼리가 복잡하면, JPQL + FetchJoin 사

        //when
        //select only Member
        List<Member> members = memberRepository.findNamedEntityGraphByUserName("member1");

        for (Member member : members) {
            System.out.println("member : " + member.getUserName());
            System.out.println("member.teamClass : " + member.getTeam().getClass());
            System.out.println("member.teamName : " + member.getTeam().getName()); // getName() 호출 시, Team 객채를 위해 쿼리 호출
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트가 남아 있고, db와 동기화한다.
        em.clear(); // 영속성 컨텍스트가 삭제되어, 다음 조회 시 db에서 불러온다.

        //when
        Member findMember = memberRepository.findReadOnlyByUserName("member1");
        findMember.setUserName("member2");

        em.flush();
    }


    @Test
    public void findLockByUserName() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트가 남아 있고, db와 동기화한다.
        em.clear(); // 영속성 컨텍스트가 삭제되어, 다음 조회 시 db에서 불러온다.

        //when
        List<Member> findMember = memberRepository.findLockByUserName("member1");

    }

    @Test
    public void callCustom() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트가 남아 있고, db와 동기화한다.
        em.clear(); // 영속성 컨텍스트가 삭제되어, 다음 조회 시 db에서 불러온다.

        //when
        List<Member> findMember = memberRepository.findMemberCustom();

    }

    @Test
    public void testMemberQueryRepository() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트가 남아 있고, db와 동기화한다.
        em.clear(); // 영속성 컨텍스트가 삭제되어, 다음 조회 시 db에서 불러온다.

        //when
        List<Member> findMember = memberQueryRepository.findAllMembers();
    }

    @Test
    public void specBasic() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.userName("m1").and(MemberSpec.userName("m1").and(MemberSpec.teamName("teamA")));
        List<Member> result = memberRepository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //probe
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUserName()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        List<UserNameOnly> result = memberRepository.findProjectionsByUserName("m1");

        for (UserNameOnly userNameOnly : result) {
            System.out.println("userNameOnly : " + userNameOnly);
        }

        List<UserNameOnlyDto> resultDto = memberRepository.findProjectionsOnlyDtoByUserName("m1");

        for (UserNameOnlyDto userNameOnly : resultDto) {
            System.out.println("userNameOnlyDto : " + userNameOnly);
        }

        List<UserNameOnlyDto> resultDtoClass = memberRepository.findProjectionsOnlyDtoByUserName("m1", UserNameOnlyDto.class);

        for (UserNameOnlyDto userNameOnly : resultDtoClass) {
            System.out.println("userNameOnlyDto : " + userNameOnly);
        }

        List<NestedClosedProjections> resultNested = memberRepository.findProjectionsOnlyDtoByUserName("m1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : resultNested) {
            System.out.println("nestedClosedProjections.getUserName : " + nestedClosedProjections.getUserName());
            System.out.println("nestedClosedProjections.getTeamName : " + nestedClosedProjections.getTeam().getName());
        }
    }

    @Test
    public void nativeQuery() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Member result = memberRepository.findByNativeQuery("m1");
        System.out.println("result : " + result);
    }


    @Test
    public void nativePageableQuery() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("getUserName : " + memberProjection.getUserName());
            System.out.println("getTeamName : " + memberProjection.getTeamName());

        }


    }


}