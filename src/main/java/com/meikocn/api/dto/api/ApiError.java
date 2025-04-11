package com.meikocn.api.dto.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Getter
@Setter
// @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use =
// JsonTypeInfo.Id.CUSTOM, property =
// "error", visible = true)
// @JsonTypeIdResolver(LowerCaseClassNameResolver.class)
public class ApiError {

  private HttpStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;

  private String message;
  private String debugMessage;
  private List<ApiSubError> errors;

  private ApiError() {
    errors = new ArrayList<>();
    timestamp = LocalDateTime.now();
  }

  public ApiError(HttpStatus status) {
    this();
    this.status = status;
  }

  public ApiError(HttpStatus status, Throwable ex) {
    this();
    this.status = status;
    this.message = "Unexpected error";
    this.debugMessage = ex.getLocalizedMessage();
  }

  public ApiError(HttpStatus status, Throwable ex, String message) {
    this();
    this.status = status;
    this.message = message;
    this.debugMessage = ex.getLocalizedMessage();
  }

  private void addSubError(ApiSubError subError) {
    errors.add(subError);
  }

  private void addValidationError(String message) {
    addSubError(new ApiValidationError(message));
  }

  private void addValidationError(String field, Object rejectedValue, String message) {
    addSubError(new ApiValidationError(field, rejectedValue, message));
  }

  private void addValidationError(ObjectError objectError) {
    this.addValidationError(objectError.getDefaultMessage());
  }

  private void addValidationError(FieldError fieldError) {
    this.addValidationError(
        fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
  }

  public void addValidationError(List<ObjectError> globalErrors) {
    globalErrors.forEach(this::addValidationError);
  }

  public void addValidationErrors(List<FieldError> fieldErrors) {
    fieldErrors.forEach(this::addValidationError);
  }

  public void addValidationError(InvalidFormatException ifx) {
    String field = ifx.getPath().get(ifx.getPath().size() - 1).getFieldName();
    String rejectedValue = ifx.getValue().toString();
    String message = ifx.getMessage();
    if (ifx.getTargetType().isEnum()) {
      message =
          String.format(
              "Invalid enum value: '%s' for the field: '%s'. The value must be one of: %s.",
              ifx.getValue(),
              ifx.getPath().get(ifx.getPath().size() - 1).getFieldName(),
              Arrays.toString(ifx.getTargetType().getEnumConstants()));
    }
    this.addValidationError(field, rejectedValue, message);
  }

  /**
   * Utility method for adding error of ConstraintViolation. Usually when a @Validated validation
   * fails.
   *
   * @param cv the ConstraintViolation
   */
  private void addValidationError(ConstraintViolation<?> cv) {
    this.addValidationError(cv.getPropertyPath().toString(), cv.getInvalidValue(), cv.getMessage());
  }

  public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
    constraintViolations.forEach(this::addValidationError);
  }
}
