package kimspring.splearn.domain.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTest {

    @Test
    void equality() {
        var email1 = new Email("kim@splearn.app");
        var email2 = new Email("kim@splearn.app");

        assertThat(email1).isEqualTo(email2);
    }
}