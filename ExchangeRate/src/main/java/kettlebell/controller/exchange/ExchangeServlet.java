package kettlebell.controller.exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kettlebell.mapper.RespMapper;
import kettlebell.dto.ConvertDTO;
import kettlebell.exceptions.AppException;
import kettlebell.exceptions.ErrorMessage;
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

		if (baseCurrencyCode == null || baseCurrencyCode.isBlank() || targetCurrencyCode == null
				|| targetCurrencyCode.isBlank() || amountToConvert == null || amountToConvert.isBlank()) {
			new RespMapper(resp, new AppException(ErrorMessage.FIELD_MISS)).getMapperErr();
			return;
		}

		if (!isValidCurrencyCode(baseCurrencyCode) || !isValidCurrencyCode(targetCurrencyCode)) {
			new RespMapper(resp, new AppException(ErrorMessage.CURRENCY_STANDART)).getMapperErr();
			return;
		}

		BigDecimal amount;
		try {
			amount = BigDecimal.valueOf(Double.parseDouble(amountToConvert));
		} catch (NumberFormatException e) {
			new RespMapper(resp, new AppException(ErrorMessage.INCORRECT_AMOUNT_VALUE)).getMapperErr();
			return;
		}

		try {
			//@formatter:off
			ConvertDTO convertDTO = service.convertCurrency(
					baseCurrencyCode,
					targetCurrencyCode,
					amount);
			//@formatter:on
			new RespMapper(resp, convertDTO).getMapperLuck();

		} catch (NoSuchElementException e) {
			new RespMapper(resp, new AppException(ErrorMessage.RATE_NOT_FOUND)).getMapperErr();
		} catch (AppException e) {
			new RespMapper(resp, e).getMapperErr();
		}

	}

}
