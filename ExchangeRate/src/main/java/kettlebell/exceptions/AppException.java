package kettlebell.exceptions;

public class AppException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private ErrorMessage errorMessage;

	public AppException(ErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

}
