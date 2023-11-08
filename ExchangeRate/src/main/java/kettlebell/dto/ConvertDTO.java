package kettlebell.dto;

import kettlebell.model.Currency;

public class ConvertDTO {
	private Currency baseCurrency;	
	private Currency targetCurrency;
	private String rate;
	private String amount;
	private String convertAmount;
	public ConvertDTO(Currency baseCurrency, Currency targetCurrency, String rate, String amount, String convertAmount) {
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.rate = rate;
		this.amount = amount;
		this.convertAmount = convertAmount;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getConvertAmount() {
		return convertAmount;
	}
	public void setConvertAmount(String convertAmount) {
		this.convertAmount = convertAmount;
	}
	
	
}
