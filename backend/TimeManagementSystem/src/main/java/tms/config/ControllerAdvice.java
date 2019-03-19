package tms.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import tms.bl.error.ValidationException;
import tms.model.view.ErrorVM;
import tms.model.view.ValidationError;

/**
 * Class for providing controller advice.
 *
 */
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

  /**
   * Logger for writing status messages.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ControllerAdvice.class);

  /**
   * Exception handler for Exception class.
   *
   * @param e exception
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorVM handle(Exception e) {
    LOG.error("Bad request", e);
    return new ErrorVM(e.getMessage());
  }

  /**
   * Exception handler for IllegalArgumentException class.
   *
   * @param e illegal argument exception
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public String handleIllegalArgument(IllegalArgumentException e) {
    LOG.error("General illegal argument exception", e);
    return e.getMessage();
  }

  /**
   * Exception handler for Exception class.
   *
   * @param e exception
   */
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  public ErrorVM handle(AccessDeniedException e) {
    LOG.error("Forbidden", e);
    return new ErrorVM("forbidden_access");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Map<String, ValidationError> handleValidationError(MethodArgumentNotValidException e) {
    LOG.trace("Validation error", e);
    BindingResult result = e.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();
    Map<String, ValidationError> errors = new HashMap<>();
    fieldErrors.stream().forEach(fieldError -> {
      if (fieldError.getArguments() != null && fieldError.getArguments().length > 1) {
        errors.put(fieldError.getField(), new ValidationError(fieldError.getDefaultMessage(), parseValidationParams(fieldError)));
      } else {
        errors.put(fieldError.getField(), new ValidationError(fieldError.getDefaultMessage()));
      }
    });
    return errors;
  }

  private List<String> parseValidationParams(FieldError fieldError) {
    List<String> params = new ArrayList<>();
    for (int i = fieldError.getArguments().length - 1; i > 0; i--) {
      Object param = fieldError.getArguments()[i];
      if (param instanceof Integer) {
        params.add(Integer.toString((int) param));
      }
      if (param instanceof Long) {
        params.add(Long.toString((long) param));
      }
    }
    return params;
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Map<String, ValidationError> handleValidationError(ValidationException e) {
    LOG.trace("Validation error", e);
    Map<String, ValidationError> errors = new HashMap<>();
    errors.put(e.getField(), new ValidationError(e.getMessage(), e.getParams()));
    return errors;
  }

}
