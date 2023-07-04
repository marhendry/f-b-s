package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.CustomUserDetailsService;
import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.exception.GlobalExceptionHandler;
import com.example.fbs.fbs.model.dto.UserLoginRequestDto;
import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.service.impl.UserServiceImpl;
import com.example.fbs.fbs.utility.impl.PasswordEncoderImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
public class UserControllerTest {

    public static final String USER_NAME = "Test Amigo";

    public static final String USER_EMAIL = "testamigo@email.com";

    public static final String USER_PASSWORD = "Password";

    public static final String UUID = "uuid";

    public static final String UPDATED_NAME = "Updated name";

    @Autowired
    private UserController userController;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private PasswordEncoderImpl passwordEncoder;
    @MockBean
    private UserRepository userRepository;

    private UserRequestDto userRequestDto;

    @BeforeEach
    void setup() {
        userRequestDto = UserRequestDto.builder()
                .name(USER_NAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .build();
    }

    @BeforeEach
    void setUpEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerUser() throws Exception {
        when(userService.saveInitialUserInfo(any(UserRequestDto.class))).thenReturn(UUID);

        mockMvc.perform(post("/system/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserByEmail() throws Exception {
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
        userUpdateRequestDto.setName(UPDATED_NAME);

        doNothing().when(userService).updateUserByEmail(anyString(), any(UserUpdateRequestDto.class));

        mockMvc.perform(put("/system/test@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void registerAdmin() throws Exception {
        when(userService.saveInitialAdminInfo(any(UserRequestDto.class))).thenReturn(UUID);

        mockMvc.perform(post("/system/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk());
    }
}