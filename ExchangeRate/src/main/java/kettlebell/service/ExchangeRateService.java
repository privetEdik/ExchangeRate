package kettlebell.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import kettlebell.dao.ExchangeRateRepository;
import kettlebell.dto.ConvertDTO;
import kettlebell.exceptions.AppException;
import kettlebell.model.ExchangeRate;
import kettlebell.repository.JdbcExchangeRateRepository;

import static java.math.MathContext.DECIMAL64;

public class ExchangeRateService {

	ExchangeRateRepository repository = new JdbcExchangeRateRepository();

	public ConvertDTO convertCurrency(String baseCurrenctCode, String tergetCurrenctCode, BigDecimal amount)
			throws AppException, NoSuchElementException {

		ExchangeRate exchangeRate = getExchangeRate(baseCurrenctCode, tergetCurrenctCode).orElseThrow();
		BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate());
		//@formatter:off
		return new ConvertDTO(
				exchangeRate.getBaseCurrency(),
				exchangeRate.getTargetCurrency(),
				exchangeRate.getRate().setScale(6,RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
				amount.stripTrailingZeros().toPlainString(),
				convertedAmount.setScale(2,RoundingMode.HALF_UP) .stripTrailingZeros().toPlainString());
		//@formatter:on
	}

	private Optional<ExchangeRate> getExchangeRate(String baseCurrencyId, String targetCurrencyId) throws AppException {
		Optional<ExchangeRate> exchangeRateOpt = getDirectRate(baseCurrencyId, targetCurrencyId);

		if (exchangeRateOpt.isEmpty()) {
			exchangeRateOpt = getReverseRate(baseCurrencyId, targetCurrencyId);
		}

		if (exchangeRateOpt.isEmpty()) {
			exchangeRateOpt = getRateForUSD(baseCurrencyId, targetCurrencyId);
		}

		return exchangeRateOpt;
	}

	private Optional<ExchangeRate> getDirectRate(String baseCurrencyId, String targetCurrencyId) throws AppException {
		return repository.getByCode(baseCurrencyId, targetCurrencyId);
	}

	private Optional<ExchangeRate> getReverseRate(String baseCurrencyId, String targetCurrencyId) throws AppException {
		Optional<ExchangeRate> exchangeRateOpt = repository.getByCode(targetCurrencyId, baseCurrencyId);

		if (exchangeRateOpt.isEmpty()) {
			return Optional.empty();
		}
		ExchangeRate reversedExchangeRate = exchangeRateOpt.get();
		//@formatter:off
		ExchangeRate directExchangeRate = new ExchangeRate(
				reversedExchangeRate.getTargetCurrency(),
				reversedExchangeRate.getBaseCurrency(),
				BigDecimal.ONE.divide(reversedExchangeRate.getRate(), DECIMAL64)); 
		//@formatter:on

		return Optional.of(directExchangeRate);
	}

	private Optional<ExchangeRate> getRateForUSD(String baseCurrencyId, String targetCurrencyId)
			throws AppException, NoSuchElementException {
		List<ExchangeRate> ratesWithUsdBase = repository.getByCodeWithUsdBase(baseCurrencyId, targetCurrencyId);

		ExchangeRate usdToBaseExchange = getExchangeRateForCode(ratesWithUsdBase, baseCurrencyId);

		ExchangeRate usdToTargetExchange = getExchangeRateForCode(ratesWithUsdBase, targetCurrencyId);

		BigDecimal usdToBaseRate = usdToBaseExchange.getRate();
		BigDecimal usdToTargetRate = usdToTargetExchange.getRate();

		BigDecimal baseToTargetRate = usdToTargetRate.divide(usdToBaseRate, DECIMAL64);
		//@formatter:off
		ExchangeRate exchangeRate = new ExchangeRate(
				usdToBaseExchange.getTargetCurrency(),
				usdToTargetExchange.getTargetCurrency(),
				baseToTargetRate);
		//@formatter:on
		return Optional.of(exchangeRate);
	}

	private static ExchangeRate getExchangeRateForCode(List<ExchangeRate> rates, String code)
			throws NoSuchElementException {

		Optional<ExchangeRate> optExchangeRate = rates.stream()
				.filter(rate -> rate.getTargetCurrency().getCode().equals(code)).findFirst();

		if (optExchangeRate.isEmpty()) {
			//@formatter:off
			optExchangeRate = Optional.of(rates.stream()
					.filter(rate ->rate.getBaseCurrency().getCode().equals(code))
					.findFirst()
					.map(s-> new ExchangeRate(
								s.getTargetCurrency(),
								s.getBaseCurrency(),
								BigDecimal.ONE.divide(s.getRate(),DECIMAL64)))
					.orElseThrow());
			//@formatter:on
		}
		return optExchangeRate.get();
	}
}
