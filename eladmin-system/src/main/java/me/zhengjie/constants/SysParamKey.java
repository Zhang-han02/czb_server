package me.zhengjie.constants;

/**
 * 系统参数key值常量集合
 */
public enum SysParamKey {
    CK_Index("出库日流水号"),
    RK_Index("入库日流水号"),
    RegisterCode("系统注册码");


    private String label;

    SysParamKey(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
