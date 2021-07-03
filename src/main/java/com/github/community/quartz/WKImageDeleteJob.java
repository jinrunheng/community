package com.github.community.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

@Slf4j
public class WKImageDeleteJob implements Job {

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        File[] files = new File(wkImageStorage).listFiles();
        if (files == null || files.length == 0) {
            log.info("没有 WK 图片，任务取消");
            return;
        }

        for (File file : files) {
            // 删除一分钟之前创建的图片
            if (System.currentTimeMillis() - file.lastModified() > 60 * 1000) {
                log.info("删除 WK 图片" + file.getName());
                file.delete();
            }
        }
    }
}
