/*
 * PDMS wliduo https://github.com/dolyw
 * Created By dolyw.com
 * Date By (2019-11-20 18:03:33)
 */
package com.example.service.impl;


import com.example.constant.Constant;
import com.example.dao.StockDao;
import com.example.dao.StockOrderDao;
import com.example.dto.custom.StockDto;
import com.example.dto.custom.StockOrderDto;
import com.example.exception.CustomException;
import com.example.service.ISeckillEvolutionService;
import com.example.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * StockServiceImpl
 */
@Service("seckillEvolutionService")
public class SeckillEvolutionServiceImpl implements ISeckillEvolutionService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillEvolutionServiceImpl.class);

    @Autowired
    private StockDao stockDao;

    @Autowired
    private StockOrderDao stockOrderDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createWrongOrder(Integer id) throws Exception {
        // 检查库存
        StockDto stockDto = stockDao.selectByPrimaryKey(id);
        if (stockDto.getCount() <= 0) {
            throw new CustomException("库存不足");
        }
        // 扣库存
        stockDto.setCount(stockDto.getCount() - 1);
        stockDto.setSale(stockDto.getSale() + 1);
        Integer saleCount = stockDao.updateByPrimaryKey(stockDto);
        if (saleCount <= 0) {
            throw new CustomException("扣库存失败");
        }
        // 下订单
        StockOrderDto stockOrderDto = new StockOrderDto();
        stockOrderDto.setStockId(stockDto.getId());
        Integer orderCount = stockOrderDao.insertSelective(stockOrderDto);
        if (saleCount <= 0) {
            throw new CustomException("下订单失败");
        }
        return orderCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createOptimisticLockOrder(Integer id) throws Exception {
        // 检查库存
        StockDto stockDto = stockDao.selectByPrimaryKey(id);
        if (stockDto.getCount() <= 0) {
            throw new CustomException("库存不足");
        }
        // 扣库存
        Integer saleCount = stockDao.updateByOptimisticLock(stockDto);
        if (saleCount <= 0) {
            throw new CustomException("扣库存失败");
        }
        // 下订单
        StockOrderDto stockOrderDto = new StockOrderDto();
        stockOrderDto.setStockId(stockDto.getId());
        Integer orderCount = stockOrderDao.insertSelective(stockOrderDto);
        if (saleCount <= 0) {
            throw new CustomException("下订单失败");
        }
        return orderCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createOptimisticLockOrderWithRedisWrong(Integer id) throws Exception {
        // 检查库存
        // 使用缓存读取库存，减轻DB压力，这里会出现数据不一致
        Integer count = Integer.parseInt(JedisUtil.get(Constant.PREFIX_COUNT + id));
        Thread.sleep(100);
        Integer sale = Integer.parseInt(JedisUtil.get(Constant.PREFIX_SALE + id));
        Thread.sleep(100);
        // 第一个线程和第二个线程同时读取缓存count时，都读取到10，然后第二个线程暂停了，第一个线程继续执行，
        // 读取的version版本号为0，继续执行到已经秒杀完成，更新缓存(version版本号加一，变成1)，
        // 现在第二个线程才恢复继续执行，结果读取缓存version版本号为1(本来应该也是0)
        Integer version = Integer.parseInt(JedisUtil.get(Constant.PREFIX_VERSION + id));
        if (count <= 0) {
            throw new CustomException("库存不足");
        }
        // 还有库存
        StockDto stockDto = new StockDto();
        stockDto.setId(id);
        stockDto.setCount(count);
        stockDto.setSale(sale);
        stockDto.setVersion(version);
        // 扣库存
        Integer saleCount = stockDao.updateByOptimisticLock(stockDto);
        // 操作数据大于0，说明扣库存成功
        if (saleCount > 0) {
            logger.info("版本号:{} {} {}", stockDto.getCount(), stockDto.getSale(), stockDto.getVersion());
            // 更新缓存，这里更新需要保证三个数据(库存，已售，乐观锁版本号)的一致性，使用mset原子操作
            updateCache(stockDto);
        }
        if (saleCount <= 0) {
            throw new CustomException("扣库存失败");
        }
        // 下订单
        StockOrderDto stockOrderDto = new StockOrderDto();
        stockOrderDto.setStockId(stockDto.getId());
        Integer orderCount = stockOrderDao.insertSelective(stockOrderDto);
        if (saleCount <= 0) {
            throw new CustomException("下订单失败");
        }
        Thread.sleep(10);
        return orderCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createOptimisticLockOrderWithRedisSafe(Integer id) throws Exception {
        // 检查库存
        // 使用缓存读取库存，减轻DB压力，Redis批量操作(具有原子性)解决线程安全问题
        List<String> dataList = JedisUtil.mget(Constant.PREFIX_COUNT + id,
                Constant.PREFIX_SALE + id, Constant.PREFIX_VERSION + id);
        Integer count = Integer.parseInt(dataList.get(0));
        Integer sale = Integer.parseInt(dataList.get(1));
        Integer version = Integer.parseInt(dataList.get(2));
        if (count <= 0) {
            throw new CustomException("库存不足");
        }
        // 还有库存
        StockDto stockDto = new StockDto();
        stockDto.setId(id);
        stockDto.setCount(count);
        stockDto.setSale(sale);
        stockDto.setVersion(version);
        // 扣库存
        Integer saleCount = stockDao.updateByOptimisticLock(stockDto);
        // 操作数据大于0，说明扣库存成功
        if (saleCount > 0) {
            logger.info("版本号:{} {} {}", stockDto.getCount(), stockDto.getSale(), stockDto.getVersion());
            // 更新缓存，这里更新需要保证三个数据(库存，已售，乐观锁版本号)的一致性，使用mset原子操作
            updateCache(stockDto);
        }
        if (saleCount <= 0) {
            throw new CustomException("扣库存失败");
        }
        // 下订单
        StockOrderDto stockOrderDto = new StockOrderDto();
        stockOrderDto.setStockId(stockDto.getId());
        Integer orderCount = stockOrderDao.insertSelective(stockOrderDto);
        if (saleCount <= 0) {
            throw new CustomException("下订单失败");
        }
        Thread.sleep(10);
        return orderCount;
    }


    /**
     * 这里遵循先更新数据库，再更新缓存，详细的数据库与缓存一致性解析可以查看
     * https://note.dolyw.com/cache/00-DataBaseConsistency.html
     */
    public void updateCache(StockDto stockDto) {
        Integer count = stockDto.getCount() - 1;
        Integer sale = stockDto.getSale() + 1;
        Integer version = stockDto.getVersion() + 1;
        JedisUtil.mset(Constant.PREFIX_COUNT + stockDto.getId(), count.toString(),
                Constant.PREFIX_SALE + stockDto.getId(), sale.toString(),
                Constant.PREFIX_VERSION + stockDto.getId(), version.toString());
    }
}