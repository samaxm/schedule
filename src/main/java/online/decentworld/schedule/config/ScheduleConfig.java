package online.decentworld.schedule.config;

import online.decentworld.schedule.SchedulerTaskManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Sammax on 2016/11/21.
 */
@Configuration
@ComponentScan(basePackages={"online.decentworld.schedule.*"})
public class ScheduleConfig {



    @Bean(name = "schedulerTaskManager")
    public SchedulerTaskManager getSchedulerTaskManager(){
        return new SchedulerTaskManager();
    }
}
