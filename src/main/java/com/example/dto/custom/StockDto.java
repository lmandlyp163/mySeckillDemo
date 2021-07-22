/*
 * PDMS wliduo https://github.com/dolyw
 * Created By dolyw.com
 * Date By (2019-11-20 18:03:33)
 */
package com.example.dto.custom;

import com.example.dto.domain.StockDtoBase;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * StockDto
 */
@Table(name = "t_seckill_stock")
public class StockDto extends StockDtoBase implements Serializable {

    private static final long serialVersionUID = StockDto.class.getName().hashCode();

}