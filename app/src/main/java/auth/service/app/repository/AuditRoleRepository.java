package auth.service.app.repository;

import auth.service.app.model.entity.AuditRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRoleRepository extends JpaRepository<AuditRoleEntity, Long> {}