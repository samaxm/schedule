package online.decentworld.schedule;

import online.decentworld.rdb.config.DBConfig;
import online.decentworld.schedule.config.ScheduleConfig;
import online.decentworld.tools.DateFormater;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Date;


/**
 * Created by Sammax on 2016/11/21.
 */
public class ScheduleStarter {

    private  static Logger logger= LoggerFactory.getLogger(ScheduleStarter.class);

    private AnnotationConfigApplicationContext ctx;


    public ScheduleStarter(){

    }

    /**
     * load spring context and start jobs
     */
    public void init(){
        ctx = new AnnotationConfigApplicationContext();
        ctx.register(DBConfig.class, ScheduleConfig.class);
        ctx.refresh();
    }

    public Job getJob(String jobName){
        return (Job) ctx.getBean(jobName);
    }


    public static void main(String[] args) throws Exception {
        logger.debug("[Start Schedule Jobs] time#"+ DateFormater.formatReadableString(new Date()));
        ScheduleStarter starter=new ScheduleStarter();
        starter.init();

    }
}
