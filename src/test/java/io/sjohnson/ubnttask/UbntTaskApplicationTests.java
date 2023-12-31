package io.sjohnson.ubnttask;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UbntTaskApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void exampleTest() {
        assertThat(true).isEqualTo(true);
    }

}
