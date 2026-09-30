package com.demo2.docker.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "OAuth2 Authentication", description = "Endpoints for OAuth2 Client-Credentials token retrieval")
@CrossOrigin(origins = "*")
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);
    private final TokenService tokenService;

    public OAuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(
            summary = "Request OAuth2 Bearer Access Token",
            description = "Simulates enterprise OAuth2 client-credentials flow. Provide grant_type=client_credentials, client_id=legacy-app, client_secret=cm-secret-123.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token issued successfully",
                            content = @Content(schema = @Schema(implementation = OAuthTokenResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid client credentials")
            }
    )
    @PostMapping(
            value = {"/oauth/token", "/api/v1/auth/token"},
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> obtainToken(
            @RequestParam(name = "grant_type", required = false, defaultValue = "client_credentials") String grantType,
            @RequestParam(name = "client_id", required = false, defaultValue = "legacy-app") String clientId,
            @RequestParam(name = "client_secret", required = false, defaultValue = "cm-secret-123") String clientSecret,
            @RequestParam(name = "scope", required = false, defaultValue = "documents:read documents:write") String scope,
            @RequestBody(required = false) OAuthTokenRequest jsonRequest
    ) {
        // Fallback to JSON body if provided
        if (jsonRequest != null) {
            if (jsonRequest.getGrant_type() != null) grantType = jsonRequest.getGrant_type();
            if (jsonRequest.getClient_id() != null) clientId = jsonRequest.getClient_id();
            if (jsonRequest.getClient_secret() != null) clientSecret = jsonRequest.getClient_secret();
            if (jsonRequest.getScope() != null) scope = jsonRequest.getScope();
        }

        log.info("OAuth token request received: grant_type='{}', client_id='{}', scope='{}'", grantType, clientId, scope);

        if (!tokenService.validateClientCredentials(clientId, clientSecret, grantType)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "invalid_client",
                            "error_description", "Invalid client credentials or unsupported grant_type. Expected client_id='legacy-app', client_secret='cm-secret-123', grant_type='client_credentials'"
                    ));
        }

        OAuthTokenResponse tokenResponse = tokenService.generateToken(scope);
        return ResponseEntity.ok(tokenResponse);
    }
}
