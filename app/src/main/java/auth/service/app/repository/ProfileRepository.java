package auth.service.app.repository;

import auth.service.app.model.entity.ProfileEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
  Optional<ProfileEntity> findByEmail(final String email);
}