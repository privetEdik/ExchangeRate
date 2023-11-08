package kettlebell.mapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import static jakarta.servlet.http.HttpServletResponse.*;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseMapper {
	private HttpServletResponse response;
	private String parameter;

	private static final String DATABASE = "Something happened with the database, try again later!";
	private static final String NO_RATE = "There is no exchange rate for this currency pair";
	private static final String SYNTAX_ERROR_CURRENCY = "Currency codes are either not provided or provided in an incorrect format";
	private static final String NOTHING = "There is no such in the database";
	private static final String CURRENCIES_NO_BASE = "One or both currencies for which you are trying to add an exchange rate does not exist in the database";

	private ObjectMapper mapper = new ObjectMapper();

	public ResponseMapper(HttpServletResponse resp, String parameter) {
		this.response = resp;
		this.parameter = parameter;
	}

	public ResponseMapper(HttpServletResponse resp) {
		this.response = resp;
	}

	public void successfulOut(Object o) throws ServletException, IOException {
		mapper.writeValue(response.getWriter(), o);
	}

	//@formatter:off
	public void missParameter() throws ServletException, IOException {
		response.setStatus(SC_BAD_REQUEST);
		mapper.writeValue(response.getWriter(),
				new Massage(String.format("Missing parameter - %s", parameter)));
	}

	public void incorectParameter() throws ServletException, IOException {
		response.setStatus(SC_BAD_REQUEST);
		mapper.writeValue(response.getWriter(),
				new Massage(String.format("Incorrect value of %s parameter", parameter)));
	}

	public void errorDatabase() throws ServletException, IOException {
		response.setStatus(SC_INTERNAL_SERVER_ERROR);
		mapper.writeValue(response.getWriter(), new Massage(DATABASE));
	}

	public void alreadyExists() throws ServletException, IOException {
		response.setStatus(SC_CONFLICT);
		mapper.writeValue(response.getWriter(),
				new Massage(String.format("%s already exists", parameter)));
	}

	public void formatISO() throws ServletException, IOException {
		response.setStatus(SC_BAD_REQUEST);
		mapper.writeValue(response.getWriter(),
				new Massage(String.format("%surrency code must be in ISO 4217 format", parameter)));
	}

	public void noRate() throws ServletException, IOException {
		response.setStatus(SC_NOT_FOUND);
		mapper.writeValue(response.getWriter(), new Massage(NO_RATE));
	}

	public void syntaxErrorCurrency() throws ServletException, IOException {
		response.setStatus(SC_BAD_REQUEST);
		mapper.writeValue(response.getWriter(),
				new Massage(SYNTAX_ERROR_CURRENCY));
	}

	public void nothingInDatabase() throws ServletException, IOException {
		response.setStatus(SC_NOT_FOUND);
		mapper.writeValue(response.getWriter(), new Massage(NOTHING));
	}

	public void noCurrenciesInDatabase() throws ServletException, IOException {
		response.setStatus(SC_NOT_FOUND);
		mapper.writeValue(response.getWriter(),
				new Massage(CURRENCIES_NO_BASE));
	}
	//@formatter:on
}
