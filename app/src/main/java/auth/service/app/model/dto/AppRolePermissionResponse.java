package auth.service.app.model.dto;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AppRolePermissionResponse extends ResponseMetadata {
  private List<AppRolePermissionDto> rolesPermissions;

  public AppRolePermissionResponse(
      final List<AppRolePermissionDto> rolesPermissions,
      final ResponseCrudInfo responseCrudInfo,
      final ResponsePageInfo responsePageInfo,
      final ResponseStatusInfo responseStatusInfo) {
    super(responseCrudInfo, responsePageInfo, responseStatusInfo);
    this.rolesPermissions = rolesPermissions;
  }
}