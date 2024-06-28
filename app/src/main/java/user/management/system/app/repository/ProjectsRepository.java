package user.management.system.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user.management.system.app.model.entity.ProjectEntity;

public interface ProjectsRepository extends JpaRepository<ProjectEntity, Integer> {}
