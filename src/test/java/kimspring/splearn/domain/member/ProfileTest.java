package kimspring.splearn.domain.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTest {
    @Test
    void profile() {
        new Profile("kim");
        new Profile("kim2");
        new Profile("0044");
        new Profile("");
    }

    @Test
    void profileFail() {
        assertThatThrownBy(() -> new Profile("toolongtoolongtoolong"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("KIM"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("킴"))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void url() {
        Profile profile = new Profile("kim");

        assertThat(profile.url()).isEqualTo("@kim");
    }

}