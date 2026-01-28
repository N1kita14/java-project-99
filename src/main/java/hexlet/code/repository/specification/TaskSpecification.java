package hexlet.code.repository.specification;

import hexlet.code.dto.task.TaskFiltrationDto;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskFiltrationDto filtration) {
        return withTitle(filtration.getTitleCont())
                .and(withAssignee(filtration.getAssigneeId()))
                .and(withStatus(filtration.getStatus()))
                .and(withLabel(filtration.getLabelId()));
    }

    private Specification<Task> withTitle(String titleSubstring) {
        return (root, query, cb) -> !hasText(titleSubstring)
                ? cb.conjunction()
                : cb.like(root.get("name"), "%" + titleSubstring + "%");
    }

    private Specification<Task> withAssignee(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null
                ? cb.conjunction()
                : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) -> !hasText(status)
                ? cb.conjunction()
                : cb.equal(root.get("taskStatus").get("slug"), status);
    }

    private Specification<Task> withLabel(Long labelId) {
        return (root, query, cb) -> labelId == null
                ? cb.conjunction()
                : cb.equal(root.get("labels").get("id"), labelId);
    }
}
