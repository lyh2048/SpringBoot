package com.example.demo.config;

import com.example.demo.quartz.MedalDataJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfig {
    public static class MedalDataJobConfig {
        @Bean
        public JobDetail medalDataJob() {
            return JobBuilder.newJob(MedalDataJob.class)
                    .withIdentity("medalDataJob")
                    .storeDurably()
                    .build();
        }

        @Bean
        public Trigger medalDataJobTrigger() {
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(10).repeatForever();
            return TriggerBuilder.newTrigger()
                    .forJob(medalDataJob())
                    .withIdentity("medalDataJobTrigger")
                    .withSchedule(simpleScheduleBuilder)
                    .build();
        }
    }
}
