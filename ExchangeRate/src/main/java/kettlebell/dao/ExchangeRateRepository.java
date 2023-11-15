package kettlebell.dao;

import java.util.List;
import java.util.Optional;

import kettlebell.exceptions.AppException;
import kettlebell.model.ExchangeRate;

public interface ExchangeRateRepository extends CRUDRepository<ExchangeRate>{
	Optional<ExchangeRate> getByCode (String baseCurrencyCode, String targetCurrencyCode) throws AppException;
	List<ExchangeRate> getByCodeWithUsdBase(String baseCurrencyCode, String targetCurrencyCode) throws AppException;
}
