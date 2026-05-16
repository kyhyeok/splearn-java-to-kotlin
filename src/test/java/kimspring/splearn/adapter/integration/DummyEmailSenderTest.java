package kimspring.splearn.adapter.integration;

import kimspring.splearn.domain.shared.Email;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import static org.assertj.core.api.Assertions.assertThat;

class DummyEmailSenderTest {
    @Test
    @StdIo
    void dummyEmailSender(StdOut out) {
        DummyEmailSender dummyEmailSender = new DummyEmailSender();

        dummyEmailSender.send(new Email("kim@splearn.app"), "subject", "body");

        assertThat(out.capturedLines()[0])
                .isEqualTo("DummyEmailSender.send email: Email[address=kim@splearn.app]");
    }
}