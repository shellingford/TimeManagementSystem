package tms.bl.error;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

  private String field;
  private List<String> params;

  private static final long serialVersionUID = 7615496594468035837L;

  public ValidationException(String message, String field) {
    super(message);
    this.field = field;
    this.params = new ArrayList<>();
  }

  public ValidationException(String message, String field, List<String> params) {
    super(message);
    this.field = field;
    this.params = params;
  }

  public String getField() {
    return field;
  }

  public List<String> getParams() {
    return params;
  }
}
