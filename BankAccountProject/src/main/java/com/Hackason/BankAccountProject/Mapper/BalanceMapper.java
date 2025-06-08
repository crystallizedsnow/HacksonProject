package com.Hackason.BankAccountProject.Mapper;

import com.Hackason.BankAccountProject.entity.Balance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


import java.math.BigDecimal;
@Mapper
public interface BalanceMapper extends BaseMapper<Balance> {
    @Update("UPDATE balance SET amount = amount - #{amount} WHERE accountId = #{accountId} AND currency = #{currency} AND amount >= #{amount}")
    int decreaseBalance(@Param("account_id") String accountId,
                        @Param("currency") String currency,
                        @Param("amount") BigDecimal amount);

    @Update("UPDATE balance SET amount = amount + #{amount} WHERE accountId = #{accountId} AND currency = #{currency}")
    int increaseBalance(@Param("account_id") String accountId,
                        @Param("currency") String currency,
                        @Param("amount") BigDecimal amount);

    @Select("SELECT * FROM balance WHERE accountId = #{accountId} AND currency = #{currency} FOR UPDATE")
    Balance selectByAccountAndCurrency(@Param("account_id") String accountId,
                                       @Param("currency") String currency);
}
