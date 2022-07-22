package me.zhengjie.modules.system.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.constants.SysParamKey;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sys_params")
@Getter
@Setter
public class Params {

    @ApiModelProperty(value = "参数键")
    @Id
    @Column(name = "`key`")
    @NotNull
    @Enumerated(EnumType.STRING)
    private SysParamKey key;

    @ApiModelProperty(value = "参数值")
    private String value;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "标签")
    private String label;
}
