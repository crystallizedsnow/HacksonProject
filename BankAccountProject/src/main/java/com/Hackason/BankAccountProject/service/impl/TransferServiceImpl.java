package com.Hackason.BankAccountProject.service.impl;


import com.Hackason.BankAccountProject.Exception.BusinessException;
import com.Hackason.BankAccountProject.Mapper.BalanceMapper;
import com.Hackason.BankAccountProject.Mapper.TransactionStatusMapper;
import com.Hackason.BankAccountProject.config.SnowflakeIdGenerator;
import com.Hackason.BankAccountProject.entity.Balance;
import com.Hackason.BankAccountProject.entity.TransactionStatus;
import com.Hackason.BankAccountProject.param.TransferRequest;
import com.Hackason.BankAccountProject.service.DistributedLockService;
import com.Hackason.BankAccountProject.service.TransferService;
import com.Hackason.BankAccountProject.vo.TransferResult;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author wtt
 * @date 2025/06/08
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService{
    private final BalanceMapper balanceMapper;
    private final TransactionStatusMapper transactionStatusMapper;
    private final DistributedLockService lockService;
    private final SnowflakeIdGenerator idGenerator;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferResult transfer(TransferRequest request) {
        // 1. 参数校验
        validateRequest(request);

        // 2. 生成唯一交易ID
        String transactionId = String.valueOf(idGenerator.generateId());

        // 3. 记录交易初始状态
        TransactionStatus transaction = createInitialTransaction(request, transactionId);
        transactionStatusMapper.insert(transaction);

        try {
            // 4. 加分布式锁(防止并发操作同一账户)
            lockAccounts(request.getFromAccount(), request.getToAccount());

            // 5. 检查转出账户余额
            checkBalance(request);

            // 6. 执行转账操作
            executeTransfer(request);

            // 7. 更新交易状态为成功
            updateTransactionStatus(transactionId, "SUCCESS");

            return buildSuccessResult(request, transactionId);
        } catch (Exception e) {
            // 8. 失败处理
            updateTransactionStatus(transactionId, "FAILED");
            throw new BusinessException("Transfer failed: " + e.getMessage());
        } finally {
            // 9. 释放锁
            releaseLocks(request.getFromAccount(), request.getToAccount());
        }
    }

    private void validateRequest(TransferRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be positive");
        }
        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new BusinessException("From and to accounts cannot be the same");
        }
    }

    private TransactionStatus createInitialTransaction(TransferRequest request, String transactionId) {
        TransactionStatus transaction = new TransactionStatus();
        transaction.setTransactionId(transactionId);
        transaction.setFromAccount(request.getFromAccount());
        transaction.setToAccount(request.getToAccount());
        transaction.setAmount(request.getAmount());
        transaction.setCurrencyType(request.getCurrency());
        transaction.setStatus("PENDING");
        transaction.setCreatedAt(LocalDateTime.now().toString());
        transaction.setUpdatedAt(LocalDateTime.now().toString());
        return transaction;
    }

    private void lockAccounts(String fromAccount, String toAccount) {
        // 按固定顺序加锁避免死锁
        String firstLock = fromAccount.compareTo(toAccount) < 0 ? fromAccount : toAccount;
        String secondLock = fromAccount.compareTo(toAccount) < 0 ? toAccount : fromAccount;

        if (!lockService.tryLock(firstLock, 3, TimeUnit.SECONDS)) {
            throw new BusinessException("Failed to acquire lock for account: " + firstLock);
        }
        if (!lockService.tryLock(secondLock, 3, TimeUnit.SECONDS)) {
            lockService.unlock(firstLock);
            throw new BusinessException("Failed to acquire lock for account: " + secondLock);
        }
    }

    private void checkBalance(TransferRequest request) {
        Balance fromBalance = balanceMapper.selectByAccountAndCurrency(request.getFromAccount(), request.getCurrency());
        if (fromBalance == null || fromBalance.getAmount().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance");
        }
    }

    private void executeTransfer(TransferRequest request) {
        // 扣减转出账户余额
        balanceMapper.decreaseBalance(request.getFromAccount(), request.getCurrency(), request.getAmount());

        // 增加转入账户余额
        Balance toBalance = balanceMapper.selectByAccountAndCurrency(request.getToAccount(), request.getCurrency());
        if (toBalance == null) {
            // 如果目标账户没有该币种余额记录，创建一条
            Balance newBalance = new Balance();
            newBalance.setAccountId(Long.parseLong(request.getToAccount()));
            newBalance.setAmount(request.getAmount());
            newBalance.setCurrency(request.getCurrency());
            balanceMapper.insert(newBalance);
        } else {
            balanceMapper.increaseBalance(request.getToAccount(), request.getCurrency(), request.getAmount());
        }
    }

    private void updateTransactionStatus(String transactionId, String status) {
        TransactionStatus update = new TransactionStatus();
        update.setStatus(status);
        update.setUpdatedAt(LocalDateTime.now().toString());

        LambdaUpdateWrapper<TransactionStatus> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TransactionStatus::getTransactionId, transactionId);

        transactionStatusMapper.update(update, wrapper);
    }

    private TransferResult buildSuccessResult(TransferRequest request, String transactionId) {
        TransferResult result = new TransferResult();
        result.setSuccess(true);
        result.setTransactionId(transactionId);
        result.setAmount(request.getAmount());
        result.setCurrency(request.getCurrency());
        result.setFromAccount(request.getFromAccount());
        result.setToAccount(request.getToAccount());
        result.setMessage("Transfer successful");
        return result;
    }

    private void releaseLocks(String fromAccount, String toAccount) {
        lockService.unlock(fromAccount);
        lockService.unlock(toAccount);
    }
}
