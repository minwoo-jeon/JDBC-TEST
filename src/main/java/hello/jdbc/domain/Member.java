package hello.jdbc.domain;

import lombok.Data;

@Data   //getter, setter, toString, equals, hashcode , requiredArgsConstructor 를 모아논 종합세트
public class Member {

    private String memberId;
    private int money;

    public Member(){
        
    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}
