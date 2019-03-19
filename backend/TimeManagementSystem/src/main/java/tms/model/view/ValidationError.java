package tms.model.view;

import java.util.ArrayList;
import java.util.List;

public class ValidationError {

  private String msg;
  private List<String> params;

  public ValidationError(String field) {
    this.msg = field;
    this.params = new ArrayList<>();
  }

  public ValidationError(String field, List<String> params) {
    this.msg = field;
    this.params = params;
  }

  public String getMsg() {
    return msg;
  }

  public List<String> getParams() {
    return params;
  }
}
