package kettlebell.controller.exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kettlebell.utils.Validation.isValidCurrencyCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kettlebell.dao.ExchangeRateRepository;
import kettlebell.dto.ExchangeRateDTO;
import kettlebell.exceptions.AppException;
import kettlebell.exceptions.ErrorMessage;
import kettlebell.mapper.RespMapper;
import kettlebell.model.ExchangeRate;
import kettlebell.repository.JdbcExchangeRateRepository;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ExchangeRateRepository exchangeRateRepository = new JdbcExchangeRateRepository();
	private String baseCurrencyCode;
	private String targetCurrencyCode;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = req.getPathInfo().replaceAll("/", "");

		if (url.length() != 6) {
			new RespMapper(resp, new AppException(ErrorMessage.CURRENSIES_ERROR)).getMapperErr();
			return;
		}

		baseCurrencyCode = url.substring(0, 3);
		targetCurrencyCode = url.substring(3);

		if (!isValidCurrencyCode(baseCurrencyCode) || !isValidCurrencyCode(targetCurrencyCode)) {
			new RespMapper(resp, new AppException(ErrorMessage.CURRENCY_STANDART)).getMapperErr();
			return;
		}

		if (req.getMethod().equalsIgnoreCase("PATCH")) {
			doPatch(req, resp);
		} else {
			super.service(req, resp);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			Optional<ExchangeRate> optionalExchangeRate = exchangeRateRepository.getByCode(baseCurrencyCode,
					targetCurrencyCode);
			if (optionalExchangeRate.isEmpty()) {
				new RespMapper(resp, new AppException(ErrorMessage.RATE_NOT_FOUND)).getMapperErr();
				return;
			}
			ExchangeRate exchangeRate = optionalExchangeRate.get();
			//@formatter:off
			ExchangeRateDTO rateDTO = new ExchangeRateDTO(
					exchangeRate.getId(),
					exchangeRate.getBaseCurrency(),
					exchangeRate.getTargetCurrency(),
					exchangeRate.getRate().stripTrailingZeros().toPlainString());
			//@formatter:on
			new RespMapper(resp, rateDTO).getMapperLuck();
		} catch (AppException e) {
			new RespMapper(resp, e).getMapperErr();
		}
	}

	protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String parameter = req.getReader().readLine();

		if (parameter == null || !parameter.contains("rate")) {
			new RespMapper(resp, new AppException(ErrorMessage.FIELD_MISS)).getMapperErr();
			return;
		}
		String rateParameter = parameter.replace("rate=", "");
		try {
			BigDecimal rate = BigDecimal.valueOf(Double.valueOf(rateParameter)).setScale(6, RoundingMode.HALF_UP);

			ExchangeRate exchangeRate = exchangeRateRepository.getByCode(baseCurrencyCode, targetCurrencyCode)
					.orElseThrow();

			exchangeRate.setRate(rate);
			exchangeRateRepository.put(exchangeRate);
			//@formatter:off
			ExchangeRateDTO rateDTO = new ExchangeRateDTO(
					exchangeRate.getId(),
					exchangeRate.getBaseCurrency(),
					exchangeRate.getTargetCurrency(),
					rate.stripTrailingZeros().toPlainString());
			//@formatter:on
			new RespMapper(resp, rateDTO).getMapperLuck();

		} catch (NumberFormatException e) {
			new RespMapper(resp, new AppException(ErrorMessage.INCORRECT_RATE_VALUE)).getMapperErr();

		} catch (NoSuchElementException e) {
			new RespMapper(resp, new AppException(ErrorMessage.RATE_NOT_FOUND)).getMapperErr();

		} catch (AppException e) {
			new RespMapper(resp, e);
		}

	}

}
