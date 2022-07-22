package me.zhengjie.modules.security.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExpirationInfo {
    @ApiModelProperty("到期日期")
    private String expirationTime;

    @ApiModelProperty("剩余天数")
    private long residueDays;

    @ApiModelProperty("是否需要提醒")
    private boolean needWarn;

    @ApiModelProperty("注册码")
    private String registerCode;
}
