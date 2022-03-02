/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.dto.region;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 省
 *
 * @author Mark sunlightcs@gmail.com
 */
@ApiModel(value = "省")
@Data
@EqualsAndHashCode(callSuper = true)
public class RegionProvince extends Region {
    @ApiModelProperty(value = "市列表")
    private List<Region> cities = new ArrayList<>();
}
