package user.management.system.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import user.management.system.app.model.entity.AppRolePermissionEntity;
import user.management.system.app.model.entity.AppRolePermissionId;

@Repository
public interface AppRolePermissionRepository
    extends JpaRepository<AppRolePermissionEntity, AppRolePermissionId> {
  List<AppRolePermissionEntity> findByAppRoleIdOrderByAppPermissionNameAsc(
      @Param("roleId") final int roleId);

  List<AppRolePermissionEntity> findByAppPermissionAppAndAppRoleIdInOrderByAppPermissionNameAsc(
      @Param("app") final String app, @Param("roleIds") final List<Integer> roleIds);
}
