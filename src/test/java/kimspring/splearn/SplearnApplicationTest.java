package kimspring.splearn;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class SplearnApplicationTest {
    @Test
    void run() {

        try(MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            SplearnApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(SplearnApplication.class, new String[]{}));
        }
    }

}