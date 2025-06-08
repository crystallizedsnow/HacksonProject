package com.Hackason.BankAccountProject.service;

import com.Hackason.BankAccountProject.Exception.BusinessException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @date 2025/06/08
 */
@Service
public class DistributedLockService {
    private final RedissonClient redissonClient;
    private static final String LOCK_PREFIX = "account_lock:";
    private static final long DEFAULT_WAIT_TIME = 3; // 默认等待时间(秒)
    private static final long DEFAULT_LEASE_TIME = 10; // 默认锁持有时间(秒)

    public DistributedLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 尝试获取锁
     * @param lockKey 锁key
     * @param waitTime 等待时间(秒)
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + lockKey);
        try {
            return lock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_WAIT_TIME,TimeUnit.SECONDS);
    }

    /**
     * 释放锁
     * @param lockKey 锁key
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + lockKey);
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 安全执行带有锁的操作
     * @param lockKeys 需要加锁的keys
     * @param operation 要执行的操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    public <T> T executeWithLocks(List<String> lockKeys, Supplier<T> operation) {
        // 对锁key排序，避免死锁
        List<String> sortedKeys = lockKeys.stream()
                .sorted()
                .collect(Collectors.toList());

        List<RLock> acquiredLocks = new ArrayList<>();
        try {
            // 按顺序获取所有锁
            for (String key : sortedKeys) {
                RLock lock = redissonClient.getLock(LOCK_PREFIX + key);
                if (lock.tryLock(DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.SECONDS)) {
                    acquiredLocks.add(lock);
                } else {
                    // 获取失败，释放已获得的锁
                    releaseAllLocks(acquiredLocks);
                    throw new BusinessException("Failed to acquire lock for: " + key);
                }
            }

            // 执行操作
            return operation.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            releaseAllLocks(acquiredLocks);
            throw new BusinessException("Thread interrupted while acquiring locks");
        } finally {
            // 释放所有锁
            releaseAllLocks(acquiredLocks);
        }
    }

    private void releaseAllLocks(List<RLock> locks) {
        for (RLock lock : locks) {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
