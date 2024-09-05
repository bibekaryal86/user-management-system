package user.management.system.app.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserRoleDto {
  private AppUserDto user;
  private AppRoleDto role;
  private LocalDateTime assignedDate;
}
