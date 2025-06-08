package com.Hackason.BankAccountProject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wtt
 * @date 2025/06/08
 */
@Data
@TableName(value = "transaction_status", autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatus {

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "username")
    private String username;

    @Schema(description = "transaction id")
    private String transactionId;

    @Schema(description = "from account")
    private String fromAccount;

    @Schema(description = "to account")
    private String toAccount;

    @Schema(description = "amount")
    private BigDecimal amount;

    @Schema(description = "currency type")
    private String currencyType;

    @Schema(description = "status (PENDING, SUCCESS, FAILED, CANCELLED)")
    private String status;

    @Schema(description = "created time")
    private String createdAt;

    @Schema(description = "updated time")
    private String updatedAt;
}
