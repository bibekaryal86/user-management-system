package user.management.system.app.controller;

import static user.management.system.app.util.CommonUtils.getBaseUrlForLinkInEmail;
import static user.management.system.app.util.JwtUtils.encodeAuthCredentials;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import user.management.system.app.model.dto.AppTokenRequest;
import user.management.system.app.model.dto.AppUserDto;
import user.management.system.app.model.dto.AppUserRequest;
import user.management.system.app.model.dto.AppUserResponse;
import user.management.system.app.model.dto.ResponseStatusInfo;
import user.management.system.app.model.dto.UserLoginRequest;
import user.management.system.app.model.dto.UserLoginResponse;
import user.management.system.app.model.entity.AppTokenEntity;
import user.management.system.app.model.entity.AppUserEntity;
import user.management.system.app.model.entity.AppsAppUserEntity;
import user.management.system.app.model.entity.AppsEntity;
import user.management.system.app.service.AppTokenService;
import user.management.system.app.service.AppUserPasswordService;
import user.management.system.app.service.AppUserService;
import user.management.system.app.service.AppsAppUserService;
import user.management.system.app.service.AppsService;
import user.management.system.app.service.EmailService;
import user.management.system.app.util.EntityDtoConvertUtils;

@Tag(name = "Users Management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/basic_app_users/user")
public class AppUserBasicAuthController {

  private final AppUserService appUserService;
  private final AppsService appsService;
  private final AppsAppUserService appsAppUserService;
  private final AppUserPasswordService appUserPasswordService;
  private final EntityDtoConvertUtils entityDtoConvertUtils;
  private final EmailService emailService;
  private final AppTokenService appTokenService;

  @Operation(
      summary = "Create a new user for an application",
      description = "Creates a new user for the specified application ID",
      security = @SecurityRequirement(name = "Basic"),
      parameters = {
        @Parameter(
            name = "appId",
            description = "ID of the application for which the user is created",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Details of the user to create",
              required = true,
              content = @Content(schema = @Schema(implementation = AppUserRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "User created successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AppUserResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid or missing data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AppUserResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Missing/Incorrect credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AppUserResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Invalid credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AppUserResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - Application Not Found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AppUserResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Other Errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AppUserResponse.class)))
      })
  @PostMapping("/{appId}/create")
  public ResponseEntity<AppUserResponse> createAppUser(
      @PathVariable final String appId,
      @RequestBody final AppUserRequest appUserRequest,
      final HttpServletRequest request) {
    try {
      final String baseUrl = getBaseUrlForLinkInEmail(request);
      final AppsEntity appsEntity = appsService.readApp(appId);
      final AppUserEntity appUserEntity =
          appUserService.createAppUser(appsEntity, appUserRequest, baseUrl);
      return entityDtoConvertUtils.getResponseSingleAppUser(appUserEntity);
    } catch (Exception ex) {
      return entityDtoConvertUtils.getResponseErrorAppUser(ex);
    }
  }

  @Operation(
      summary = "Login a user for an application",
      description =
          "Authenticates a user for the specified application ID and returns access and refresh tokens",
      security = @SecurityRequirement(name = "Basic"),
      parameters = {
        @Parameter(
            name = "appId",
            description = "ID of the application for which the user is logging in",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "User login details",
              required = true,
              content = @Content(schema = @Schema(implementation = UserLoginRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Missing/Incorrect credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Invalid credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - User Not Found for App",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Other Errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class)))
      })
  @PostMapping("/{appId}/login")
  public ResponseEntity<UserLoginResponse> loginAppUser(
      @PathVariable final String appId, @RequestBody final UserLoginRequest userLoginRequest) {
    try {
      final AppUserEntity appUserEntity = appUserPasswordService.loginUser(appId, userLoginRequest);
      final AppUserDto appUserDto =
          entityDtoConvertUtils.convertEntityToDtoAppUser(appUserEntity, true);
      final String accessToken = encodeAuthCredentials(appUserDto, 1000 * 60 * 15); // 15 min
      final String refreshToken = encodeAuthCredentials(appUserDto, 1000 * 60 * 60 * 24); // 1 day
      final AppTokenEntity appTokenEntity =
          appTokenService.saveToken(null, null, appUserEntity, accessToken, refreshToken);
      return ResponseEntity.ok(
          UserLoginResponse.builder()
              .aToken(appTokenEntity.getAccessToken())
              .rToken(appTokenEntity.getRefreshToken())
              .user(appUserDto)
              .build());
    } catch (Exception ex) {
      return entityDtoConvertUtils.getResponseErrorAppUserLogin(ex);
    }
  }

  @Operation(
      summary = "Refresh authentication token",
      description =
          "Refreshes auth token for the specified application ID and returns access and refresh tokens",
      security = @SecurityRequirement(name = "Basic"),
      parameters = {
        @Parameter(
            name = "appId",
            description = "ID of the application for which the token is being refreshed",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Token request details",
              required = true,
              content = @Content(schema = @Schema(implementation = AppTokenRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Refresh successful",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Missing/Incorrect credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Invalid Credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - User Token not found for input refresh token",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Other Errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class)))
      })
  @PostMapping("/{appId}/refresh")
  public ResponseEntity<UserLoginResponse> refreshToken(
      @PathVariable final String appId, @RequestBody final AppTokenRequest appTokenRequest) {
    try {
      final AppTokenEntity appTokenEntity =
          appTokenService.readTokenByRefreshToken(appTokenRequest.getRefreshToken());
      final AppUserDto appUserDto =
          entityDtoConvertUtils.convertEntityToDtoAppUser(appTokenEntity.getUser(), true);
      final String newAccessToken = encodeAuthCredentials(appUserDto, 1000 * 60 * 15); // 15 min
      final String newRefreshToken =
          encodeAuthCredentials(appUserDto, 1000 * 60 * 60 * 24); // 1 day
      final AppTokenEntity newAppTokenEntity =
          appTokenService.saveToken(
              appTokenEntity.getId(),
              null,
              appTokenEntity.getUser(),
              newAccessToken,
              newRefreshToken);
      return ResponseEntity.ok(
          UserLoginResponse.builder()
              .aToken(newAppTokenEntity.getAccessToken())
              .rToken(newAppTokenEntity.getRefreshToken())
              .user(appUserDto)
              .build());
    } catch (Exception ex) {
      return entityDtoConvertUtils.getResponseErrorAppUserLogin(ex);
    }
  }

  @Operation(
      summary = "Logout user from an application",
      description = "Logs out user by removing tokens from the system",
      security = @SecurityRequirement(name = "Basic"),
      parameters = {
        @Parameter(
            name = "appId",
            description = "ID of the application from which user is logging out",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Logout request details",
              required = true,
              content = @Content(schema = @Schema(implementation = AppTokenRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Missing/Incorrect credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Invalid Credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - User Token not found for input access token",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Other Errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserLoginResponse.class)))
      })
  @PostMapping("/{appId}/logout")
  public ResponseEntity<ResponseStatusInfo> logout(
      @PathVariable final String appId, @RequestBody final AppTokenRequest appTokenRequest) {
    try {
      final AppTokenEntity appTokenEntity =
          appTokenService.readTokenByAccessToken(appTokenRequest.getAccessToken());
      appTokenService.saveToken(
          appTokenEntity.getId(),
          LocalDateTime.now(),
          appTokenEntity.getUser(),
          appTokenEntity.getAccessToken(),
          appTokenEntity.getRefreshToken());
      return ResponseEntity.noContent().build();
    } catch (Exception ex) {
      return entityDtoConvertUtils.getResponseErrorResponseStatusInfo(ex);
    }
  }

  @Operation(
      summary = "Reset user password",
      description = "Resets the password for a user associated with the specified application ID",
      security = @SecurityRequirement(name = "Basic"),
      parameters = {
        @Parameter(
            name = "appId",
            description = "ID of the application for which the user's password is being reset",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Details of the user for whom the password will be reset",
              required = true,
              content = @Content(schema = @Schema(implementation = UserLoginRequest.class))),
      responses = {
        @ApiResponse(responseCode = "204", description = "Password reset successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Missing/Incorrect Credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Invalid Credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - Application or User Not Found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Other Errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class)))
      })
  @PostMapping("/{appId}/reset")
  public ResponseEntity<ResponseStatusInfo> resetAppUser(
      @PathVariable final String appId, @RequestBody final UserLoginRequest userLoginRequest) {
    try {
      appUserPasswordService.resetUser(appId, userLoginRequest);
      return ResponseEntity.noContent().build();
    } catch (Exception ex) {
      return entityDtoConvertUtils.getResponseErrorResponseStatusInfo(ex);
    }
  }

  @Operation(
      summary = "Send user validation email",
      description =
          "Sends an email to validate the user associated with the specified application ID",
      security = @SecurityRequirement(name = "Basic"),
      parameters = {
        @Parameter(
            name = "appId",
            description = "ID of the application for which the user validation email is sent",
            required = true),
        @Parameter(
            name = "email",
            description = "Email address of the user to validate",
            required = true)
      },
      responses = {
        @ApiResponse(responseCode = "204", description = "Validation email sent successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Missing/Incorrect Credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Invalid Credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - Application or User Not Found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Other Errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class)))
      })
  @GetMapping("/{appId}/validate_init")
  public ResponseEntity<ResponseStatusInfo> validateAppUserInit(
      @PathVariable final String appId,
      @RequestParam final String email,
      final HttpServletRequest request) {
    try {
      final AppsAppUserEntity appsAppUserEntity = appsAppUserService.readAppsAppUser(appId, email);
      final String baseUrl = getBaseUrlForLinkInEmail(request);
      emailService.sendUserValidationEmail(
          appsAppUserEntity.getApp(), appsAppUserEntity.getAppUser(), baseUrl);
      return ResponseEntity.noContent().build();
    } catch (Exception ex) {
      return entityDtoConvertUtils.getResponseErrorResponseStatusInfo(ex);
    }
  }

  @Operation(
      summary = "Send password reset email",
      description =
          "Sends an email to reset the password for the user associated with the specified application ID",
      security = @SecurityRequirement(name = "Basic"),
      parameters = {
        @Parameter(
            name = "appId",
            description = "ID of the application for which the password reset email is sent",
            required = true),
        @Parameter(
            name = "email",
            description = "Email address of the user to reset password",
            required = true)
      },
      responses = {
        @ApiResponse(responseCode = "204", description = "Password reset email sent successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Missing/Incorrect Credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Invalid Credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - Application or User Not Found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Other Errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseStatusInfo.class)))
      })
  @GetMapping("/{appId}/reset_init")
  public ResponseEntity<ResponseStatusInfo> resetAppUserInit(
      @PathVariable final String appId,
      @RequestParam final String email,
      final HttpServletRequest request) {
    try {
      final AppsAppUserEntity appsAppUserEntity = appsAppUserService.readAppsAppUser(appId, email);
      final String baseUrl = getBaseUrlForLinkInEmail(request);
      emailService.sendUserResetEmail(
          appsAppUserEntity.getApp(), appsAppUserEntity.getAppUser(), baseUrl);
      return ResponseEntity.noContent().build();
    } catch (Exception ex) {
      return entityDtoConvertUtils.getResponseErrorResponseStatusInfo(ex);
    }
  }
}
