/*
 * PDMS wliduo https://github.com/dolyw
 * Created By dolyw.com
 * Date By (2019-11-20 18:03:33)
 */
package com.example.service;

import com.example.common.IBaseService;
import com.example.dto.custom.StockDto;
import java.util.List;

/**
 * IStockService
 */
public interface IStockService extends IBaseService<StockDto> {

    /**
     * 列表
     */
    public List<StockDto> findPageInfo(StockDto stockDto);
}