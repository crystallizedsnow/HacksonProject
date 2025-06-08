package com.Hackason.BankAccountProject.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wtt
 * @date 2025/06/08
 */
@Data
public class TransferResult {
    private boolean success;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String fromAccount;
    private String toAccount;
    private String message;
}
