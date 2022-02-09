package com.example.demo.quartz;

import com.example.demo.entity.Medal;
import com.example.demo.service.MedalService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

@DisallowConcurrentExecution
@Slf4j
public class MedalDataJob extends QuartzJobBean {
    @Autowired
    private MedalService medalService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("任务开始执行");
        // 获取数据
        List<Medal> medalList = medalService.getMedalData();
        // 将数据保存到数据库
        for (Medal medal : medalList) {
            // 判断数据是否存在
            Medal medalByName = medalService.findMedalByName(medal.getName());
            if (medalByName == null) {
                medal.setId(null);
                int i = medalService.saveMedal(medal);
                if (i > 0) {
                    log.info("数据[" + medal + "]插入成功");
                }
            } else {
                medal.setId(medalByName.getId());
                int i = medalService.updateMedal(medal);
                if (i > 0) {
                    log.info("数据[" + medal + "]更新成功");
                }
            }
        }
        log.info("任务执行结束");
    }
}
