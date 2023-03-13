package study.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.dto.MemberDto;
import study.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); //메소드 이름으로 쿼리 생성

    @Query(name = "Member.findByUsername") //이건 없어도 됨. JPA가 Member 엔티티를 탐색할 때 NamedQuery를 먼저 탐색하기 때문에 굳이 입력하지 않아도 JPA가 알아서 찾음.
    List<Member> findByUsername(@Param("username") String username); //NamedQuery 사용

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.dto.MemberDto( m.id, m.username, t.name ) from Member m join m.team t")
    List<MemberDto> findMemberDto(); //Dto 조회

    @Query("select m from Member m where m.username in :names") //이름기반으로 파라미터를 바인딩해서 결과 가져오기
    List<Member> findByNames(@Param("names") List<String> names);


    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOprtioanlByUsername(String name);


    //페이징 처리
    Page<Member> findByAge(int age, Pageable pageable);
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findCountByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) //영속성 컨텍스트 데이터를 동일화 시키기 위해 clearAutomatically 설정을 true로 함.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team") //페치 조인 하면 연관된 테이블의 정보를 다 가져옴.
    List<Member> findMemberFetchJoin1();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberFetchJoin2();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(value = "Member.all")
    List<Member> findNamedEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true")) //JPA Hint 기능 사용
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE) //JPA가 제공하는 기능임.
    List<Member> findLockByUsername(String username);



}
