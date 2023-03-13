package study.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.dto.MemberDto;
import study.entity.Member;
import study.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void testQuery(){ //@Query 어노테이션으로 레포 메소드에 쿼리 정의하기 테스트
        Member m1 = new Member("aaaa", 10);
        Member m2 = new Member("bbbb", 16);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("aaaa", 10);
        assertThat(result.get(0)).isEqualTo(m1);

    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member("aaaa", 10);
        Member m2 = new Member("bbbb", 16);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for(String s : usernameList){
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto(){
        Team t1 = new Team("teamA");
        teamRepository.save(t1);

        Member m1 = new Member("aaaa", 10);
        m1.setTeam(t1);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for(MemberDto md : memberDto){
            System.out.println("dto = " + md);
        }
    }

    @Test
    public void findByNames(){
        Member m1 = new Member("aaaa", 10);
        Member m2 = new Member("bbbb", 16);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("aaaa", "bbbb"));

        for (Member member: result){
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType(){
        Member m1 = new Member("aaaa", 10);
        Member m2 = new Member("bbbb", 16);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaaa = memberRepository.findListByUsername("aaaa");
        System.out.println("aaaa = " + aaaa.get(0));
        Member aaaa1 = memberRepository.findMemberByUsername("aaaa");
        System.out.println("aaaa1 = " + aaaa1);

        List<Member> empty_result = memberRepository.findListByUsername("asdfas"); //컬렉션을 조회하는데 없는 값이면, 빈 컬랙션 반환됨.
        System.out.println(empty_result);

        Optional<Member> findMember = memberRepository.findOprtioanlByUsername("aaaa"); //단건이 아닌 2건 이상의
        System.out.println("findMember : " + findMember);
    }

    @Test
    public void paging(){ //페이징 처리 테스트
        memberRepository.save(new Member(("member1"), 10));
        memberRepository.save(new Member(("member2"), 10));
        memberRepository.save(new Member(("member3"), 10));
        memberRepository.save(new Member(("member4"), 10));
        memberRepository.save(new Member(("member5"), 10));
        memberRepository.save(new Member(("member6"), 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username")); //페이지는 0부터 시작임.

        Page<Member> page = memberRepository.findByAge(age, pageRequest);
            //페이지를 가져옴으로써, JPA가 total 쿼리도 같이 보내기 때문에 total 쿼리를 따로 설정하지 않아도 됨.

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
            //

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements(); //total count

        for(Member member : content){
            System.out.println("member = " + member);
        }
        System.out.println("total count: " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(6); //총 몇개의 데이터가 나오는가?
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호 가져오기
        assertThat(page.isFirst()).isTrue(); //페이지가 첫번째 페이지인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    public void slice(){ //페이징 처리 테스트
        memberRepository.save(new Member(("member1"), 10));
        memberRepository.save(new Member(("member2"), 10));
        memberRepository.save(new Member(("member3"), 10));
        memberRepository.save(new Member(("member4"), 10));
        memberRepository.save(new Member(("member5"), 10));
        memberRepository.save(new Member(("member6"), 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username")); //슬라이스는 0부터 시작임.

        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);
        //슬라이스는 total 쿼리를 안 날리기 때문에 따로 만들어야 함.

        List<Member> content = slice.getContent();
//        long totalElements = page.getTotalElements(); //total count

        for(Member member : content){
            System.out.println("member = " + member);
        }
//        System.out.println("total count: " + totalElements);

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(6); //총 몇개의 데이터가 나오는가?
        assertThat(slice.getNumber()).isEqualTo(0); //페이지 번호 가져오기
        assertThat(slice.isFirst()).isTrue(); //페이지가 첫번째 페이지인가?
        assertThat(slice.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    public void buldUpdate(){
        memberRepository.save(new Member(("member1"), 10));
        memberRepository.save(new Member(("member2"), 17));
        memberRepository.save(new Member(("member3"), 18));
        memberRepository.save(new Member(("member4"), 20));
        memberRepository.save(new Member(("member5"), 24));
        memberRepository.save(new Member(("member6"), 28));

        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();
        //영속성 컨텍스트 데이터를 동일하게 하기 위해 flush, clear를 함


        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0); //EntityManager 를 flush, clear 안 하면 벌크 연산은 영속성 컨텍스트를 안 거치고 DB에 가기 때문에 member5의 age는 여전히 24임.
            //현재는 @Modify 어노테이션에 clear~ 설정을 true로 했기 때문에 25살로 잘 조회됨.
        System.out.println(member5);

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("user1", 15, teamA);
        Member member2 = new Member("user1", 17, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //where
        //select Member 쿼리 1개 + 팀명 쿼리 1개 => N + 1 문제임.
        //해결책: 페치조인
//        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin1(); //left join을 해서 member, team의 정보를 1개의 쿼리로 한꺼번에 다 가져옴
//        List<Member> members = memberRepository.findAll(); //findAll() 함수를 Override 해서 @EntityGraph 어노테이션으로 페치조인을 간편하게 해결함.
        List<Member> members = memberRepository.findNamedEntityGraphByUsername("user1"); //findAll() 함수를 Override 해서 @EntityGraph 어노테이션으로 페치조인을 간편하게 해결함.

        for(Member member : members){
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getClass()); //처음 데이터를 가져올 때 member 데이터만 가져오기 때문에 LAZY로 설정되어 있다면 가짜 클래스로 채워놓고
            System.out.println("member.team = " + member.getTeam().getName());
                //팀명을 가져올 때 데이터가 없기 때문에 getName()에서 팀 데이터 가져오는 쿼리가 1개 더 나가게 됨.
        }

    }

    @Test
    public void queryHint(){
        //given
        Member member1 = new Member("member1", 15);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //where
//        Member findMember = memberRepository.findById(member1.getId()).get(); //실무에서 .get()은 쓰면 안됨.
        Member findMember = memberRepository.findReadOnlyByUsername("member1"); //하이버네이트에서 제공하는 readOnly 기능 사용
//        findMember.setUsername("member2"); //변경 감지로 인해 JPA가 update 쿼리를 보냄
        findMember.setUsername("member2"); //readOnly이기 때문에 JPA가 변경 감지를 안 함.

        em.flush(); //여기서 변경감지를 함.
    }

    @Test
    public void queryLock(){
        //given
        Member member1 = new Member("member1", 15);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //where
        List<Member> findMember = memberRepository.findLockByUsername("member1");
            //쿼리에 for update가 붙어서 나가게 됨.

        em.flush(); //여기서 변경감지를 함.
    }
}