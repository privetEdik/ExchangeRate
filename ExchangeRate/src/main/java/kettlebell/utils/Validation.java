package kettlebell.utils;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class Validation {
	private static Set<String> currencyCodes;

	public static boolean isValidCurrencyCode(String code) {
		if (currencyCodes == null) {
			Set<Currency> currencies = Currency.getAvailableCurrencies();
			//@formatter:off
			currencyCodes = currencies.stream()
					.map(Currency::getCurrencyCode)
					.collect(Collectors.toSet());
			//@formatter:on
		}
		return currencyCodes.contains(code);
	}
}
