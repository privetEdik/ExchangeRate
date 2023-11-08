package kettlebell.dto;

import kettlebell.model.Currency;

public class ExchangeRateDTO {
	private Integer id;
	private Currency baseCurrency;	
	private Currency targetCurrency;
	private String rate;
	
	public ExchangeRateDTO(Integer id, Currency baseCurrency, Currency targetCurrency, String rate) {
		this.id = id;
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.rate = rate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public Currency getTargetCurrency() {
		return targetCurrency;
	}

	public void setTargetCurrency(Currency targetCurrency) {
		this.targetCurrency = targetCurrency;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}
	
	
	
	
}
