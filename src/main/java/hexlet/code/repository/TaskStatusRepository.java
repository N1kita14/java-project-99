package hexlet.code.repository;

import hexlet.code.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    boolean existsByNameIgnoreCaseOrSlugIgnoreCase(String name, String slug);

    Optional<TaskStatus> findBySlug(String slug);
}
