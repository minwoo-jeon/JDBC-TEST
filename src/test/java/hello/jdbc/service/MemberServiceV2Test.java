package hello.jdbc.service;


import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MemberServiceV2Test {


    private  MemberRepositoryV2 memberRepository;
    private MemberServiceV2 memberService;

    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);
         memberRepository = new MemberRepositoryV2(dataSource);
         memberService = new MemberServiceV2(memberRepository,dataSource);
    }

    @AfterEach
    void after() throws SQLException{
        memberRepository.delete("memberA");
        memberRepository.delete("memberB");
        memberRepository.delete("ex");
    }

    @Test
    @DisplayName("계좌 이체")
    public void accountTransfer() throws SQLException {

        //given
        Member memberA = new Member("memberA", 10000);
        Member memberB = new Member("memberB", 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);


        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }


    @Test
    @DisplayName("이체중 예외 발생")
    public void accountTransferEx()throws SQLException{

        //given - 다음 데이터를 저장해서 테스트를 준비한다
        Member memberA = new Member("memberA", 10000);
        Member memberC = new Member("ex", 10000);
        Member member1 = memberRepository.save(memberA);
        Member member2 = memberRepository.save(memberC);

        //when 계좌이체 로직을 실행한다
        assertThatThrownBy(()->
        memberService.accountTransfer(member1.getMemberId(),member2.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);


        //then: 계좌이체 실패
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberC.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);

    }
}
