package hexlet.code.repository;

import hexlet.code.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {

    boolean existsByName(String name);

    Optional<Label> findByName(String name);
}
