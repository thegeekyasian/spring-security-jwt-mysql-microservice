package com.thegeekyasian.security;

import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author thegeekyasian.com
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class BaseServiceTest {
}
