package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("select (count(t) > 0) from Task t where t.assignee.id = ?1")
    boolean existsByAssigneeId(Long id);

    @Query("select (count(t) > 0) from Task t where t.taskStatus.id = ?1")
    boolean existsByTaskStatusId(Long id);

    @Query("select (count(t) > 0) from Task t inner join t.labels labels where labels.id = ?1")
    boolean existsByLabelsId(Long id);
}
