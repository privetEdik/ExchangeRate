package kettlebell.controller.exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import java.util.stream.Collectors;

import static kettlebell.utils.Validation.isValidCurrencyCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kettlebell.dao.CurrencyRepository;
import kettlebell.dao.ExchangeRateRepository;
import kettlebell.dto.ExchangeRateDTO;
import kettlebell.mapper.ResponseMapper;
import kettlebell.model.ExchangeRate;
import kettlebell.repository.JdbcCurrencyRepository;
import kettlebell.repository.JdbcExchangeRateRepository;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ExchangeRateRepository exchangeRateRepository = new JdbcExchangeRateRepository();
	private final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();

	private final static Integer SQLITE_CONSTRAINT_UNIQUE = 19;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			List<ExchangeRate> rates = exchangeRateRepository.getAll();
			//@formatter:off
			List<ExchangeRateDTO> rateDTOs = rates.stream()
					.map(r -> new ExchangeRateDTO(
								r.getId(),
								r.getBaseCurrency(),
								r.getTargetCurrency(),
								r.getRate().stripTrailingZeros().toPlainString()))
					.collect(Collectors.toList());
			//@formatter:on
			new ResponseMapper(resp).successfulOut(rateDTOs);
		} catch (SQLException e) {
			new ResponseMapper(resp).errorDatabase();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String baseCurrencyCode = req.getParameter("baseCurrencyCode");
		String targetCurrencyCode = req.getParameter("targetCurrencyCode");
		String rateParam = req.getParameter("rate");

		String missParam = "";
		if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
			missParam = "baseCurrencyCode";
		} else if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
			missParam = "targetCurrencyCode";
		} else if (rateParam == null || rateParam.isBlank()) {
			missParam = "rate";
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

		BigDecimal rate;
		try {
			rate = BigDecimal.valueOf(Double.parseDouble(rateParam)).setScale(6, RoundingMode.HALF_UP);
		} catch (NumberFormatException e) {
			new ResponseMapper(resp, "rate").incorectParameter();

			return;
		}

		try {
			//@formatter:off
			ExchangeRate exchangeRate = new ExchangeRate(
					currencyRepository.findByCode(baseCurrencyCode).orElseThrow(),
					currencyRepository.findByCode(targetCurrencyCode).orElseThrow(),
					rate);
			//@formatter:on
			Integer exchangeRateId = exchangeRateRepository.add(exchangeRate);
			//@formatter:off
			ExchangeRateDTO rateDTO = new ExchangeRateDTO(
								exchangeRateId,
								exchangeRate.getBaseCurrency(),
								exchangeRate.getTargetCurrency(),
								rate.stripTrailingZeros().toPlainString());
			//@formatter:on
			new ResponseMapper(resp).successfulOut(rateDTO);

		} catch (NoSuchElementException e) {
			new ResponseMapper(resp).noCurrenciesInDatabase();

		} catch (SQLException e) {
			if (e.getErrorCode() == SQLITE_CONSTRAINT_UNIQUE) {
				new ResponseMapper(resp, "Exchange rate").alreadyExists();
				return;
			}
			new ResponseMapper(resp).errorDatabase();

		}
	}

}
