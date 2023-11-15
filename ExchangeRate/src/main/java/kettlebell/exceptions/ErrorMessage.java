package kettlebell.exceptions;

import static jakarta.servlet.http.HttpServletResponse.*;

public enum ErrorMessage {
	SOMETHING_DATABASE("Something happened with the database, try again later!", SC_INTERNAL_SERVER_ERROR),
	INCORRECT_RATE_VALUE("incorrect exchange rate value", SC_BAD_REQUEST),
	INCORRECT_AMOUNT_VALUE("incorrect value for the 'amount' parameter", SC_BAD_REQUEST),
	CURRENCY_STANDART("Currency code must be in ISO 4217 format", SC_BAD_REQUEST),
	CURRENCY_MISS("The currency code is missing in the address", SC_BAD_REQUEST),
	CURRENCY_NOT_FOUND("Currency not found", SC_NOT_FOUND),
	FIELD_MISS("A required form field is missing", SC_BAD_REQUEST),
	CURRENCY_ALREADY_EXISTS("A currency with this code already exists", SC_CONFLICT),
	CURRENSIES_ERROR("The pair currency codes are missing or incorrectly filled in the address", SC_BAD_REQUEST),
	RATE_NOT_FOUND("Exchange rate for currency pair not found", SC_NOT_FOUND),
	CURRENCY_PAIR_ALREADY_EXISTS("A currency pair with this code already exists", SC_CONFLICT);

	private String massage;
	private Integer status;

	ErrorMessage(String massage, Integer status) {
		this.massage = massage;
		this.status = status;
	}

	public String getMassage() {
		return massage;
	}

	public Integer getStatus() {
		return status;
	}

}
