package hello.jdbc.service;


/**
* 예외 누수 문제 해결
 * SQLException 제거
 * MemberRepository 인터페이스 의존
* */

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Slf4j

public class MemberServiceV4 {

    private final MemberRepository memberRepository;


    public MemberServiceV4( MemberRepository  memberRepository){
        this.memberRepository = memberRepository;
    }

//    private final PlatformTransactionManager transactionManager;
//    private final DriverManagerDataSource dataSource;

    //계좌 이체
    @Transactional
    public void accountTransfer(String fromId , String toId , int money)  {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic( String fromId , String toId , int money){
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById( toId);

        memberRepository.update(fromId,fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId,toMember.getMoney()+money);
    }


    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
