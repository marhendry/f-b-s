package com.example.fbs.fbs.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;
}