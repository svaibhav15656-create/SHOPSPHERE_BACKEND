package com.vaibhavs_backend.proper_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import com.vaibhavs_backend.proper_backend.entity.Role;
import com.vaibhavs_backend.proper_backend.entity.User;
import com.vaibhavs_backend.proper_backend.entity.Role;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vaibhavs_backend.proper_backend.repository.UserRepository;


@AutoConfigureMockMvc
@SpringBootTest
class ProperBackendApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Test
	void contextLoads() {
	}


	@Test
	void adminCanAccessUSerEndpoint()throws Exception{
		User admin = new User();
		admin.setEmail("Test123@gmail.com");
		admin.setPassword(passwordEncoder.encode("12345678"));
		admin.setRole(Role.ADMIN);
		userRepository.save(admin);
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
	        .contentType(MediaType.APPLICATION_JSON)
			.content("{\"email\":\"Test123@gmail.com\",\"password\":\"12345678\"}"))
			.andReturn();
		String responseBody = loginResult.getResponse().getContentAsString();
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,String > responseMap = objectMapper.readValue(responseBody, Map.class);
		String token = responseMap.get("token");
		MvcResult userResult = mockMvc.perform(get("/api/auth/Users")
	            .header("Authorization", "Bearer " + token))
				.andReturn();
		int status = userResult.getResponse().getStatus();
		assertEquals(200, status);
	}

}
