package me.zhengjie.modules.quartz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.constants.SysParamKey;
import me.zhengjie.modules.system.service.ParamsService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@EnableAsync
@Slf4j
@RequiredArgsConstructor
public class CzbTask {
    private final ParamsService paramsService;

    /**
     * 重置流水号索引
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetSerialNumber() {
        //入库流水号
        paramsService.save(SysParamKey.RK_Index, "0");
        //出库流水号
        paramsService.save(SysParamKey.CK_Index, "0");
        log.info("入库、出库流水号索引已重置！");
    }
}
