package com.isacmms.exchangetransactions.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(value = "exchange_transactions")
public class ExchangeTransactionEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * User ID
	 */
	@NotNull
	@Column(value = "user_id")
	private Long userId;

	/**
	 * Origin currency
	 */
	@NotNull
	@Column(value = "base_currency")
	private String baseCurrency;

	/**
	 * Origin value
	 */
	@NotNull
	@Column(value = "base_value")
	private BigDecimal baseValue;

	/**
	 * Transaction rate currency
	 */
	@NotNull()
	@Column(value = "rate_currency")
	private String rateCurrency;

	/**
	 * Transaction rate value. Not persisted and resolved on entity load.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Transient
	private BigDecimal rateValue;

	/**
	 * Transaction rate used
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(value = "used_rate")
	private BigDecimal usedRate;
	
	public ExchangeTransactionEntity(Long userId, String baseCurrency, BigDecimal baseValue, String rateCurrency) {
		this.userId = userId;
		this.baseCurrency = baseCurrency;
		this.baseValue = baseValue;
		this.rateCurrency = rateCurrency;
	}

	public enum CurrencyEnum {
		BRL, 
		USD, 
		EUR, 
		JPY
	}

}
