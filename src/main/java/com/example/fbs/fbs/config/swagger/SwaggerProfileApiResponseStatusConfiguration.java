package com.example.fbs.fbs.config.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.example.fbs.fbs.config.swagger.ControllerResponseStatus.STATUS_CODE_BAD_REQUEST;
import static com.example.fbs.fbs.config.swagger.ControllerResponseStatus.STATUS_CODE_FORBIDDEN;
import static com.example.fbs.fbs.config.swagger.ControllerResponseStatus.STATUS_CODE_INTERNAL_SERVER_ERROR;
import static com.example.fbs.fbs.config.swagger.ControllerResponseStatus.STATUS_CODE_NOT_FOUND;
import static com.example.fbs.fbs.config.swagger.ControllerResponseStatus.STATUS_CODE_SUCCESS;
import static com.example.fbs.fbs.config.swagger.ControllerResponseStatus.STATUS_CODE_UNAUTHORIZED;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = STATUS_CODE_SUCCESS, content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "400", description = STATUS_CODE_BAD_REQUEST, content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = STATUS_CODE_UNAUTHORIZED, content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "403", description = STATUS_CODE_FORBIDDEN, content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = STATUS_CODE_NOT_FOUND, content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = STATUS_CODE_INTERNAL_SERVER_ERROR, content = @Content(schema = @Schema(implementation = String.class)))
})
public @interface SwaggerProfileApiResponseStatusConfiguration {

}
