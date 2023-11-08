package kettlebell.controller.exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kettlebell.mapper.ResponseMapper;
import kettlebell.dto.ConvertDTO;
import kettlebell.service.ExchangeRateService;

import static kettlebell.utils.Validation.isValidCurrencyCode;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	ObjectMapper objectMapper = new ObjectMapper();
	ExchangeRateService service = new ExchangeRateService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String baseCurrencyCode = req.getParameter("from");
		String targetCurrencyCode = req.getParameter("to");
		String amountToConvert = req.getParameter("amount");

		String missParam = "";
		if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
			missParam = "from";
		} else if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
			missParam = "to";
		} else if (amountToConvert == null || amountToConvert.isBlank()) {
			missParam = "amount";
		}

		if (!missParam.equals("")) {
			new ResponseMapper(resp, missParam).missParameter();
			return;
		}

		if (!isValidCurrencyCode(baseCurrencyCode)) {
			new ResponseMapper(resp, "Base c").formatISO();
			return;
		}
		if (!isValidCurrencyCode(targetCurrencyCode)) {
			new ResponseMapper(resp, "Target c").formatISO();
			return;
		}

		BigDecimal amount;
		try {
			amount = BigDecimal.valueOf(Double.parseDouble(amountToConvert));
		} catch (NumberFormatException e) {
			new ResponseMapper(resp, "amount").incorectParameter();
			return;
		}

		try {
			//@formatter:off
			ConvertDTO convertDTO = service.convertCurrency(
					baseCurrencyCode,
					targetCurrencyCode,
					amount);
			//@formatter:on
			new ResponseMapper(resp).successfulOut(convertDTO);

		} catch (NoSuchElementException e) {
			new ResponseMapper(resp).noRate();
		} catch (SQLException e) {
			new ResponseMapper(resp).errorDatabase();
		}

	}

}
