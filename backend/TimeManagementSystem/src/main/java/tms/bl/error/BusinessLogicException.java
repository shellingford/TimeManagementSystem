package tms.bl.error;

public class BusinessLogicException extends RuntimeException {

	private static final long serialVersionUID = 8159089520406669065L;

	public BusinessLogicException(String message) {
		super(message);
    }

	public BusinessLogicException(String message, Throwable cause) {
		super(message, cause);
    }
}
