package hello.jdbc.exception.basic;

import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


@Slf4j
public class UnCheckedTest {


    @Test
    void unchecked_catch(){
        Service service = new Service();
        assertThatThrownBy(()-> service.callTrow())
                .isInstanceOf(MyUncheckedException.class);
    }

    @Test
    void unchecked_throw(){
        Service service = new Service();
        service.callTrow();
    }


    static class Service{
        Repository repository = new Repository();
        public void callCatch(){
            try{
                repository.call();
            }catch (MyUncheckedException e){
                log.info("예외 처리, message={}", e.getMessage(),e);
            }
        }

        public void callTrow(){
            repository.call();
        }
    }

    static class Repository{
        public void call(){
            throw new MyUncheckedException("언체크 예외 발생");
        }
    }


    // RuntimeException을 상속받은 예외는 언체크 예외가 된다
    static class MyUncheckedException extends RuntimeException{
        public MyUncheckedException(String message){
            super(message);
        }
    }
}