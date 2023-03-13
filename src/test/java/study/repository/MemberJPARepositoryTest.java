package study.repository;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.entity.Member;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class MemberJPARepositoryTest {

    @Autowired
    MemberJPARepository memberJpaRepository;
    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("memberA");
        Member member2 = new Member("memberB");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("user1");


        //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //count 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deleteCount = memberJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 15);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThen("aaa", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("aaa");
        assertThat(result.get(0).getAge()).isEqualTo(15);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void paging(){
        memberJpaRepository.save(new Member(("member1"), 10));
        memberJpaRepository.save(new Member(("member2"), 10));
        memberJpaRepository.save(new Member(("member3"), 10));
        memberJpaRepository.save(new Member(("member4"), 10));
        memberJpaRepository.save(new Member(("member5"), 10));
        memberJpaRepository.save(new Member(("member6"), 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(6);

    }

    @Test
    public void buldUpdate(){
        memberJpaRepository.save(new Member(("member1"), 10));
        memberJpaRepository.save(new Member(("member2"), 17));
        memberJpaRepository.save(new Member(("member3"), 18));
        memberJpaRepository.save(new Member(("member4"), 20));
        memberJpaRepository.save(new Member(("member5"), 24));
        memberJpaRepository.save(new Member(("member6"), 28));

        int resultCount = memberJpaRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(3);
    }

}