package study.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.dto.MemberDto;
import study.entity.Member;
import study.entity.Team;
import study.repository.MemberRepository;
import study.repository.TeamRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @GetMapping("/members1/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable){ //1페이지 내 데이터 수 기본값 설정
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

//    public string pagingLists(@Qualifier("member") Pageable memberPageable, @Qualifier("order") Pageable orderPageable){
//        return memberRepository.find
//    }

//    @PostConstruct
//    public void init(){
//        for(int i = 1; i <= 30; i++){
//            memberRepository.save(new Member("member"+i, 15+i));
//        }
//
//        teamRepository.save(new Team("teamA"));
//        teamRepository.save(new Team("teamB"));
//
//    }
}
