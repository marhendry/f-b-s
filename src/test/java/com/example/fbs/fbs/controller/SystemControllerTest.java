package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.exception.GlobalExceptionHandler;
import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import com.example.fbs.fbs.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SystemControllerTest {

    public static final String USER_NAME = "Test Amigo";

    public static final String USER_EMAIL = "testamigo@email.com";

    public static final String USER_PASSWORD = "Password";

    public static final String UUID = "uuid";

    public static final String UPDATED_NAME = "Updated name";

    @Autowired
    private SystemController systemController;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;
    @MockBean

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
                .standaloneSetup(systemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerUser() throws Exception {
        when(userService.saveInitialUserInfo(any(UserRequestDto.class))).thenReturn(UUID);

        mockMvc.perform(post("/system/register-client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserByUuid() throws Exception {
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
        userUpdateRequestDto.setName(UPDATED_NAME);

        doNothing().when(userService).updateUserByUuid(anyString(), any(UserUpdateRequestDto.class));

        mockMvc.perform(put("/system/{uuid}", UUID)
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