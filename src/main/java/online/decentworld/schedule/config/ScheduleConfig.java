package online.decentworld.schedule.config;

import online.decentworld.rdb.config.DBConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Sammax on 2016/11/21.
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackages={"online.decentworld.schedule.*"})
@Import(DBConfig.class)
public class ScheduleConfig {


}
