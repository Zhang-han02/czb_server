package me.zhengjie.modules.system.service.impl;

import lombok.AllArgsConstructor;
import me.zhengjie.constants.SysParamKey;
import me.zhengjie.modules.system.domain.Params;
import me.zhengjie.modules.system.repository.ParamsRepository;
import me.zhengjie.modules.system.service.ParamsService;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ParamsServiceImpl implements ParamsService {
    private final ParamsRepository paramsRepository;

    @Override
    public void save(SysParamKey key, String value) {
        Params params = new Params();
        params.setKey(key);
        params.setLabel(key.getLabel());
        params.setValue(value);
        paramsRepository.save(params);
    }

    @Override
    public Params findByKey(SysParamKey key) {
        return paramsRepository.findById(key).orElseGet(Params::new);
    }
}
