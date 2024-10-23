package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor //생성자 자동 주입 final 이 붙거나 @NotNULL 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
public class MemberServiceV1 {

    private final MemberRepositoryV2 memberRepository;

    //계좌 이체
    public void accountTransfer(String fromId , String toId , int money) throws SQLException {
        Member findMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(findMember.getMemberId(),findMember.getMoney() - money );
        validation(toMember);
        memberRepository.update(toMember.getMemberId(),toMember.getMoney()+money);

    }

    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
