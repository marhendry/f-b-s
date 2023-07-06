package com.example.fbs.fbs.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRequestDto {

    @Schema(example = "Ron Burgundy")
    @JsonProperty("name")
    private String name;

    @Schema(example = "user@example.com")
    @JsonProperty("email")
    @Email
    private String email;

    @Schema(example = "password1234")
    @JsonProperty("password")
    private String password;
}
