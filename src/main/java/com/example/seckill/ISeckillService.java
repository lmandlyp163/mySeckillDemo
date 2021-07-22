/*
 * PDMS wliduo https://github.com/dolyw
 * Created By dolyw.com
 * Date By (2019-11-20 18:03:33)
 */
package com.example.seckill;

import com.example.dto.custom.StockDto;

/**
 * 统一接口
 */
public interface ISeckillService {

    /**
     * 检查库存
     */
    StockDto checkStock(Integer id) throws Exception;

    /**
     * 扣库存
     */
    Integer saleStock(StockDto stockDto) throws Exception;

    /**
     * 下订单
     */
    Integer createOrder(StockDto stockDto);

}