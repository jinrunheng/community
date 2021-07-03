package com.github.community.config;

import com.github.community.quartz.PostScoreRefreshJob;
import com.github.community.quartz.WKImageDeleteJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {
    // 刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        // 任务是否持久保存下去
        factoryBean.setDurability(true);
        // 任务是否是可恢复的
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 配置 Trigger（SimpleTriggerFactoryBean,CronTriggerFactoryBean）
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        // 执行任务的频率 5min 测试，实际项目不需要这么短的时间 几个小时几即可
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        // 指定 JobDataMap 存储 Job 的状态
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    // 删除 WK 图片任务
    @Bean
    public JobDetailFactoryBean wkImageDeleteJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(WKImageDeleteJob.class);
        factoryBean.setName("wkImageDeleteJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 删除 WK 图片触发器
    @Bean
    public SimpleTriggerFactoryBean wkImageDeleteTrigger(JobDetail wkImageDeleteJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(wkImageDeleteJobDetail);
        factoryBean.setName("wkImageDeleteTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        // 四分钟执行一次删除图片的任务
        factoryBean.setRepeatInterval(1000 * 60 * 4);
        // 指定 JobDataMap 存储 Job 的状态
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
