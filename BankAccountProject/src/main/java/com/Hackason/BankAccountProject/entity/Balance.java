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
@TableName(value = "balance", autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "账户id")
    private Long accountId;

    @Schema(description = "余额")
    private BigDecimal amount;

    @Schema(description = "币种")
    private String currency;
}
