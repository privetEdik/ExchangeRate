package kettlebell.controller.exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
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
import kettlebell.exceptions.AppException;
import kettlebell.exceptions.ErrorMessage;
import kettlebell.mapper.RespMapper;
import kettlebell.model.ExchangeRate;
import kettlebell.repository.JdbcCurrencyRepository;
import kettlebell.repository.JdbcExchangeRateRepository;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ExchangeRateRepository exchangeRateRepository = new JdbcExchangeRateRepository();
	private final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();

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
			new RespMapper(resp, rateDTOs).getMapperLuck();
		} catch (AppException e) {
			new RespMapper(resp, e).getMapperErr();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String baseCurrencyCode = req.getParameter("baseCurrencyCode");
		String targetCurrencyCode = req.getParameter("targetCurrencyCode");
		String rateParam = req.getParameter("rate");

		//@formatter:off
		if (baseCurrencyCode == null || baseCurrencyCode.isBlank()||
			 targetCurrencyCode == null || targetCurrencyCode.isBlank()||
			 rateParam == null || rateParam.isBlank()) {
		//@formatter:on
			new RespMapper(resp, new AppException(ErrorMessage.FIELD_MISS)).getMapperErr();
			return;
		}

		if (!isValidCurrencyCode(baseCurrencyCode) || !isValidCurrencyCode(targetCurrencyCode)) {
			new RespMapper(resp, new AppException(ErrorMessage.CURRENCY_STANDART)).getMapperErr();
			return;
		}

		BigDecimal rate;
		try {
			rate = BigDecimal.valueOf(Double.parseDouble(rateParam)).setScale(6, RoundingMode.HALF_UP);
		} catch (NumberFormatException e) {
			new RespMapper(resp, new AppException(ErrorMessage.INCORRECT_RATE_VALUE)).getMapperErr();
			return;
		}

		try {
			//@formatter:off
			ExchangeRate exchangeRate = new ExchangeRate(
					currencyRepository.findByCode(baseCurrencyCode)
						.orElseThrow(()-> new AppException(ErrorMessage.CURRENCY_NOT_FOUND)),
					currencyRepository.findByCode(targetCurrencyCode)
						.orElseThrow(()-> new AppException(ErrorMessage.CURRENCY_NOT_FOUND)),
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
			new RespMapper(resp, rateDTO).getMapperLuck();

		} catch (AppException e) {
			new RespMapper(resp, e).getMapperErr();

		}
	}

}
