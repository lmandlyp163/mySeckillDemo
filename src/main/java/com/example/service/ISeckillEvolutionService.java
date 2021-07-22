/*
 * PDMS wliduo https://github.com/dolyw
 * Created By dolyw.com
 * Date By (2019-11-20 18:03:33)
 */
package com.example.service;


/**
 * ISeckillEvolutionService
 */
public interface ISeckillEvolutionService {

    /**
     * 传统方式的创建订单(并发会出现错误)
     */
    Integer createWrongOrder(Integer id) throws Exception;

    /**
     * 使用乐观锁创建订单(解决卖超问题)
     */
    Integer createOptimisticLockOrder(Integer id) throws Exception;

    /**
     * 使用乐观锁创建订单(解决卖超问题)，加缓存读(线程不安全)，提升性能，
     */
    Integer createOptimisticLockOrderWithRedisWrong(Integer id) throws Exception;

    /**
     * 使用乐观锁创建订单(解决卖超问题)，加缓存读(线程安全)，提升性能，
     */
    Integer createOptimisticLockOrderWithRedisSafe(Integer id) throws Exception;

}