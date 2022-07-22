package me.zhengjie.modules.system.service;

import me.zhengjie.constants.SysParamKey;
import me.zhengjie.modules.system.domain.Params;
import org.springframework.stereotype.Service;


public interface ParamsService {
    /**
     * 保存参数
     *
     * @param key
     * @param value
     */
    void save(SysParamKey key,String value);

    /**
     * 获取指定参数
     *
     * @param key
     * @return
     */
    Params findByKey(SysParamKey key);

}
