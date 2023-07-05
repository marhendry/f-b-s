package com.example.fbs.fbs.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserUpdateRequestDto {

    @Schema(example = "Ron Burgundy")
    @JsonProperty("name")
    private String name;

    @Schema(example = "a4afcb30-49eb-4ea1-a4f7-6831a66bcd92")
    @JsonProperty("uuid")
    private String uuid;
}