/*
 * PDMS wliduo https://github.com/dolyw
 * Created By dolyw.com
 * Date By (2019-11-20 18:03:33)
 */
package com.example.service;

import com.example.common.IBaseService;
import com.example.dto.custom.StockOrderDto;
import java.util.List;

/**
 * IStockOrderService
 */
public interface IStockOrderService extends IBaseService<StockOrderDto> {

    /**
     * 列表
     */
    public List<StockOrderDto> findPageInfo(StockOrderDto stockOrderDto);
}