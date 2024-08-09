package user.management.system.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import user.management.system.app.model.entity.UserProjectRoleEntity;
import user.management.system.app.model.entity.UserProjectRoleId;

@Repository
public interface UserProjectRoleRepository
    extends JpaRepository<UserProjectRoleEntity, UserProjectRoleId> {}
