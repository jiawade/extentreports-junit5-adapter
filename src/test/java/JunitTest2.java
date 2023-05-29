import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JunitTest2 extends BaseTest {


    @Test
    public void case03() {
        System.out.println("case 3");
    }

    @Test
    public void case04() {
        Assertions.fail();
        System.out.println("case 4");
    }

}
