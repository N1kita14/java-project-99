package hexlet.code.repository;

import hexlet.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u left join fetch u.roles where u.email = :email")
    Optional<User> findByEmailWithRoles(String email);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
