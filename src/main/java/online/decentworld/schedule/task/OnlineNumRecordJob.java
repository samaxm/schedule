package online.decentworld.schedule.task;

import online.decentworld.cache.redis.ApplicationInfoCache;
import online.decentworld.rdb.hbase.HbaseClient;
import online.decentworld.schedule.Jpush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Sammax on 2016/11/26.
 */
@Component
public class OnlineNumRecordJob{
    @Autowired
    private ApplicationInfoCache applicationInfoCache;
    @Autowired
    private Jpush jpush;
    private boolean isZero=false;
    private static SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHH");
    private static Logger logger= LoggerFactory.getLogger(OnlineNumRecordJob.class);
    private static String ONLINE_NUM_TABLE="ONLINE_NUM_TABLE_2016";
    private static String COLUMN_FAMILY="ONLINENUM";

    @Scheduled(cron = "0 * * * * *")
    public void execute(){
        try {
            long currentOnline=applicationInfoCache.checkOnline();
            if(currentOnline==0){
                isZero=true;
            }else{
                if(isZero==true){
                    //new user coming
                    jpush.pushOnlineNotice();
                    isZero=false;
                }
            }

            logger.debug("recording onlineNum#"+currentOnline);
            //save to hbase
            /*
                rowkey yyyymmddhh
                column min offset
                value num

             */
            Calendar calendar=Calendar.getInstance();
            String rowkey=format.format(calendar.getTime());
            String colume=String.valueOf(calendar.get(Calendar.MINUTE));
            HbaseClient.instance().put(ONLINE_NUM_TABLE.getBytes(),rowkey.getBytes(), COLUMN_FAMILY.getBytes(),colume.getBytes(),String.valueOf(currentOnline).getBytes());
        }catch (Exception e){
            logger.debug("",e);
        }
    }


}
