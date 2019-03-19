package tms.model.view;

import java.util.ArrayList;
import java.util.List;

public class ErrorVM {

  private boolean error = true;
  private List<String> errorMsgs;

  public ErrorVM(String errorMsg) {
    this.errorMsgs = new ArrayList<>();
    this.errorMsgs.add(errorMsg);
  }

  public ErrorVM(List<String> errorMsgs) {
    this.errorMsgs = errorMsgs;
  }

  public boolean isError() {
    return error;
  }

  public List<String> getErrorMsgs() {
    return errorMsgs;
  }

}
