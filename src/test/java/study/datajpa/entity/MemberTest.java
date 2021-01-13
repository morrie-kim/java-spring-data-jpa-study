package study.datajpa.entity;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by morrie kim on 2020/11/29.
 */

@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)
public class MemberTest {

    @PersistenceContext EntityManager em;
    @Autowired MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();

        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            log.info(">>>>>>>>> member : {}", member);
            log.info(">>>>>>>>> -> member.team : {}", member.getTeam());

        }
    }

    @Test
    public void JpaEventBaseEntity() throws InterruptedException {
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist

        Thread.sleep(100);
        member.setUserName("member2");

        em.flush(); //@PreUpdate
        em.clear();


        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.getCreatedDate : " + findMember.getCreatedDate());
        System.out.println("findMember.getCreatedBy : " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedDate : " + findMember.getLastModifiedDate());
        System.out.println("findMember.getLastModifiedBy : " + findMember.getLastModifiedBy());



    }
}