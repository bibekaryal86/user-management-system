package user.management.system.app.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import user.management.system.app.connector.AuthenvServiceConnector;
import user.management.system.app.service.AppUserPasswordService;
import user.management.system.app.util.EntityDtoConvertUtils;

@Hidden
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/na_app_users/user")
public class AppUserNoAuthController {

  private final AppUserPasswordService appUserPasswordService;
  private final EntityDtoConvertUtils entityDtoConvertUtils;
  private final AuthenvServiceConnector authenvServiceConnector;

  @Operation(
      summary = "Validate a user",
      description = "Validates a user after user is created, is executed from validation email",
      parameters = {
        @Parameter(
            name = "appId",
            description = "ID of the application for which the user is validated",
            required = true),
        @Parameter(
            name = "toValidate",
            description = "Encoded detail of user to validate",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "302",
            description = "Redirect to the URL indicating the validation status",
            headers =
                @Header(
                    name = HttpHeaders.LOCATION,
                    description =
                        "Location header containing the redirect URL with validation status",
                    schema = @Schema(type = "string")),
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid or missing validation token",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - Application User Not Found",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Other Errors",
            content = @Content(mediaType = "application/json"))
      })
  @GetMapping("/{appId}/validate_exit")
  public ResponseEntity<Void> validateAppUserExit(
      @PathVariable final String appId, @RequestParam final String toValidate) {
    final String redirectUrl = authenvServiceConnector.getRedirectUrls().getOrDefault(appId, "");
    try {
      appUserPasswordService.validateAndResetUser(appId, toValidate, true);
      return entityDtoConvertUtils.getResponseValidateUser(redirectUrl, true);
    } catch (Exception ex) {
      return entityDtoConvertUtils.getResponseValidateUser(redirectUrl, false);
    }
  }

  @Operation(
      summary = "Reset a user",
      description = "Takes user password reset process further",
      parameters = {
        @Parameter(
            name = "appId",
            description = "ID of the application for which the user is to reset",
            required = true),
        @Parameter(
            name = "toValidate",
            description = "Encoded detail of user to reset",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "302",
            description = "Redirect to the URL indicating the reset status",
            headers =
                @Header(
                    name = HttpHeaders.LOCATION,
                    description = "Location header containing the redirect URL with reset status",
                    schema = @Schema(type = "string")),
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid or missing reset token",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - Application User Not Found",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - Other Errors",
            content = @Content(mediaType = "application/json"))
      })
  @GetMapping("/{appId}/reset_exit")
  public ResponseEntity<Void> resetAppUserMid(
      @PathVariable final String appId, @RequestParam final String toReset) {
    final String redirectUrl = authenvServiceConnector.getRedirectUrls().getOrDefault(appId, "");
    try {
      final String userToReset = appUserPasswordService.validateAndResetUser(appId, toReset, false);
      return entityDtoConvertUtils.getResponseResetUser(redirectUrl, true, userToReset);
    } catch (Exception ex) {
      return entityDtoConvertUtils.getResponseResetUser(redirectUrl, true, "");
    }
  }
}
