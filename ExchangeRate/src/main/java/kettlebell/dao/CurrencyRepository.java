package kettlebell.dao;

import java.util.Optional;

import kettlebell.exceptions.AppException;
import kettlebell.model.Currency;

public interface CurrencyRepository extends CRUDRepository<Currency>{
	Optional<Currency> findByCode(String code) throws AppException;
}
