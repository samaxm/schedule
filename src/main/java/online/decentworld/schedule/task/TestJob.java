package online.decentworld.schedule.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Sammax on 2016/11/21.
 */
public class TestJob implements Job {

    private static Logger logger= LoggerFactory.getLogger(TestJob.class);


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("invoke");
    }
}
