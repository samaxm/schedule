package online.decentworld.schedule;

import com.alibaba.fastjson.JSON;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

/**
 * Created by Sammax on 2016/10/3.
 */
public class SchedulerTaskManager {

    private static Logger logger= LoggerFactory.getLogger(SchedulerTaskManager.class);

    private Hashtable<String,String> jobs=new Hashtable<>();
    private SchedulerFactory sf=new StdSchedulerFactory();

    public SchedulerTaskManager(){
        logger.debug("create bean");
    }

    public synchronized SchedulerTaskManager addTask(Class<? extends Job> job,String cronSchedule) throws SchedulerException {
        String name=job.getName();
        if(!jobs.containsKey(name)){
            logger.debug("[ADD_NEW_TASK] name#"+name);
            jobs.put(name,cronSchedule);
            JobDetail jd= JobBuilder.newJob(job)
                    .withIdentity(name, "group1")
                    .build();
            Trigger trigger=TriggerBuilder.newTrigger()
                    .withIdentity(name+"_trigger", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule))
                    .build();
            sf.getScheduler().scheduleJob(jd,trigger);
        }
        return this;
    }

    public void start() throws SchedulerException {
        sf.getScheduler().start();

    }


    @Override
    public String toString() {
        return JSON.toJSONString(jobs);
    }


}
