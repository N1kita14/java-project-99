package hexlet.code.handler;

import hexlet.code.dto.error.ErrorMessageResponse;
import hexlet.code.dto.error.ValidationError;
import hexlet.code.dto.error.Violation;
import hexlet.code.exception.AlreadyExistException;
import hexlet.code.exception.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String LABEL_DELETE_ERROR_MESSAGE = "The label cannot be deleted "
            + "because it's applied to a task";
    public static final String TASK_STATUS_DELETE_ERROR_MESSAGE = "The task_status cannot be deleted "
            + "because it's applied to a task";
    public static final String USER_DELETE_ERROR_MESSAGE = "It's impossible to delete a user "
            + "because he's assigned to a task";

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageResponse handleException(Exception e) {
        log.error("Handle Exception", e);
        String errorMessage = e.getMessage() != null ? e.getMessage() : "Exception!";
        return ErrorMessageResponse.builder().error(errorMessage).build();
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessageResponse handleException(BadCredentialsException e) {
        log.debug("Handle BadCredentialsException", e);
        String errorMessage = e.getMessage() != null ? e.getMessage() : "Exception!";
        return ErrorMessageResponse.builder().error(errorMessage).build();
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessageResponse handleException(AuthorizationDeniedException e) {
        log.debug("Handle AuthorizationDeniedException", e);
        String errorMessage = e.getMessage() != null ? e.getMessage() : "Exception!";
        return ErrorMessageResponse.builder().error(errorMessage).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleConstraintViolationException(ConstraintViolationException e) {
        ValidationError validationError = new ValidationError();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            validationError.getViolations().add(new Violation(violation.getPropertyPath().toString(),
                    violation.getMessage()));
        }
        log.warn("Handle ConstraintViolationException", e);
        return ErrorMessageResponse.builder().error(validationError.getValidationErrorMessage()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationError validationError = new ValidationError();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            validationError.getViolations().add(
                    new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        log.warn("Handle MethodArgumentNotValidException", e);
        return ErrorMessageResponse.builder().error(validationError.getValidationErrorMessage()).build();
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageResponse handleNotFoundException(NotFoundException e) {
        String errorMessage = e.getMessage() != null ? e.getMessage() : "NotFoundException!";
        return ErrorMessageResponse.builder().error(errorMessage).build();
    }

    @ExceptionHandler(value = AlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessageResponse handleAlreadyExistException(AlreadyExistException e) {
        String errorMessage = e.getMessage() != null ? e.getMessage() : "AlreadyExistException!";
        return ErrorMessageResponse.builder().error(errorMessage).build();
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessageResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String errorMessage = e.getMessage() != null ? e.getMessage() : "DataIntegrityViolationException!";
        if (errorMessage.contains("PUBLIC.TASKS FOREIGN KEY(TASK_STATUS_ID) REFERENCES PUBLIC.TASK_STATUSES(ID)")) {
            errorMessage = TASK_STATUS_DELETE_ERROR_MESSAGE;
        }
        if (errorMessage.contains("PUBLIC.TASK_LABELS FOREIGN KEY(LABEL_ID) REFERENCES PUBLIC.LABELS(ID)")) {
            errorMessage = LABEL_DELETE_ERROR_MESSAGE;
        }
        if (errorMessage.contains("PUBLIC.TASKS FOREIGN KEY(ASSIGNEE_ID) REFERENCES PUBLIC.USERS(ID)")) {
            errorMessage = USER_DELETE_ERROR_MESSAGE;
        }
        return ErrorMessageResponse.builder().error(errorMessage).build();
    }
}
