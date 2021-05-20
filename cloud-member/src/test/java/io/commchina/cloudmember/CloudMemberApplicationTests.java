package io.commchina.cloudmember;

import io.commchina.cloudmember.biz.ThirdLoginStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CloudMemberApplicationTests {

    @Autowired
    ThirdLoginStrategy thirdLoginStrategy;

    @Test
    void contextLoads() {
        System.out.println(thirdLoginStrategy);
    }

}
