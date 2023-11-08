package kettlebell.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Currency {
	private Integer id;
	@JsonProperty("name")
	private String fullName;
	private String code;
	private String sign;
	
	
	
	public Currency(Integer id, String code, String fullName, String sign) {
		this.id = id;
		this.code = code;
		this.fullName = fullName;
		this.sign = sign;
	}
	public Currency(String code, String fullName, String sign) {
		this.code = code;
		this.fullName = fullName;
		this.sign = sign;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}
