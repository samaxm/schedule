package online.decentworld.schedule;

import online.decentworld.rdb.config.DBConfig;
import online.decentworld.schedule.config.ScheduleConfig;
import online.decentworld.schedule.task.RestoreChatRecordsFromRedis2Hbase;
import online.decentworld.tools.DateFormater;
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

    public SchedulerTaskManager getSchedulerTaskManager(){
        return (SchedulerTaskManager) ctx.getBean("schedulerTaskManager");
    }

    public static void main(String[] args) throws Exception {
        logger.debug("[Start Schedule Jobs] time#"+ DateFormater.formatReadableString(new Date()));
        ScheduleStarter starter=new ScheduleStarter();
        starter.init();
        SchedulerTaskManager manager=starter.getSchedulerTaskManager();
        if(manager==null){
            throw new Exception("[START_FAILED]");
        }
        //TODO:get job list from table or properties file
        manager.addTask(RestoreChatRecordsFromRedis2Hbase.class,"0/60 * * * * ?");
//        manager.addTask(TestJob.class,"0/20 * * * * ?");
        manager.start();
    }
}
