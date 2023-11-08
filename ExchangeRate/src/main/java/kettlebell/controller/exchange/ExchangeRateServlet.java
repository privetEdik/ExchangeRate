package kettlebell.controller.exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
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
import kettlebell.mapper.ResponseMapper;
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
			new ResponseMapper(resp).syntaxErrorCurrency();
			return;
		}

		baseCurrencyCode = url.substring(0, 3);
		targetCurrencyCode = url.substring(3);

		if (!isValidCurrencyCode(baseCurrencyCode)) {
			new ResponseMapper(resp, "Base c").formatISO();
			return;
		}

		if (!isValidCurrencyCode(targetCurrencyCode)) {
			new ResponseMapper(resp, "Target c").formatISO();
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
				new ResponseMapper(resp).noRate();
			}
			ExchangeRate exchangeRate = optionalExchangeRate.get();
			//@formatter:off
			ExchangeRateDTO rateDTO = new ExchangeRateDTO(
					exchangeRate.getId(),
					exchangeRate.getBaseCurrency(),
					exchangeRate.getTargetCurrency(),
					exchangeRate.getRate().stripTrailingZeros().toPlainString());
			//@formatter:on
			new ResponseMapper(resp).successfulOut(rateDTO);
		} catch (SQLException e) {
			new ResponseMapper(resp).errorDatabase();
		}
	}

	protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String parameter = req.getReader().readLine();

		if (parameter == null || !parameter.contains("rate")) {
			new ResponseMapper(resp, "rate").missParameter();
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
			new ResponseMapper(resp).successfulOut(rateDTO);

		} catch (NumberFormatException e) {
			new ResponseMapper(resp, "rate").incorectParameter();

		} catch (NoSuchElementException e) {
			new ResponseMapper(resp).noRate();

		} catch (SQLException e) {
			new ResponseMapper(resp).errorDatabase();
		}

	}

}
