package sistemapedidos;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class PessoaApplicationTest {

    @Test
    void mainDeveDelegarParaSpringApplicationRun() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            PessoaApplication.main(new String[]{"--spring.main.web-application-type=none"});

            springApplication.verify(() -> SpringApplication.run(PessoaApplication.class,
                    new String[]{"--spring.main.web-application-type=none"}));
        }
    }
}
