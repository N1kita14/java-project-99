package hexlet.code.demo.repository;

import hexlet.code.demo.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {

    boolean existsByName(String name);

    Optional<Label> findByName(String name);
}