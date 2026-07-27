package com.shopsphere.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@SpringBootTest
class ApiGatewayApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Test
	void contextLoads() {
	}
	@Test
void rateLimitExceeded_returns429() throws Exception {
    for (int i = 0; i < 11; i++) {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"testuser999@gmail.com\",\"password\":\"12345678\"}"))
            .andReturn();
        int status = result.getResponse().getStatus();
        if (i < 10) {
            assertEquals(200, status);
        } else {
            assertEquals(429, status);
        }
    }
}
}
