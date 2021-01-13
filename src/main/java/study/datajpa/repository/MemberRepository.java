package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

/**
 * Created by morrie kim on 2020/08/17.
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);

    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUserName")
        // Optional : NamedQuery를 먼저 찾고, 없으면 Method 명으로 Query로 생성해서 호출한다. NamedQuery는 application loading 시점에서 파싱 후 오류를 표시함
    List<Member> findByUserName(@Param("userName") String userName);

    @Query("select m from Member m where m.userName = :userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);

    @Query("select m from Member m")
    List<String> findUserNameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUserName(String userName); // collection

    Member findMemberByUserName(String userName); // single

    Optional<Member> findOptionalByUserName(String userName); // sigle Optional

    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    List<Member> findListByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    Page<Member> findV2ByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUserName(@Param("userName") String userName);

    @EntityGraph("Member.all")
    List<Member> findNamedEntityGraphByUserName(@Param("userName") String userName);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String userName); // 성능 최적화를 위해 읽기전용인 쿼리에 추

    //select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE) // money 관련된 부분은 해당 타입으로 처리하고, 나머지는 OPTIMISTIC 타입 또는 Lock 없이 처리하도록 한다.
    List<Member> findLockByUserName(String userName);

    List<UserNameOnly> findProjectionsByUserName(@Param("userName") String userName);

    List<UserNameOnlyDto> findProjectionsOnlyDtoByUserName(@Param("userName") String userName);

    <T> List<T> findProjectionsOnlyDtoByUserName(@Param("userName") String userName, Class<T> type);

    @Query(value = "select * from member where user_name = ?", nativeQuery = true)
    Member findByNativeQuery(String userName);

    @Query(value = "select m.member_id as id, m.user_name as userName, t.name as teamName " +
            "from Member m left join Team t",
            countQuery = "select count(*) from Member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
