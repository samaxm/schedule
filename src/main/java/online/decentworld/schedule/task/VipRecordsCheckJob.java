package online.decentworld.schedule.task;

import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.VipRecords;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.VipRecordsMapper;
import online.decentworld.schedule.common.UserType;
import online.decentworld.tools.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sammax on 2016/12/1.
 */
//@Component
public class VipRecordsCheckJob {

    private static Logger logger= LoggerFactory.getLogger(VipRecordsCheckJob.class);
    private static int pageAmount=10000;
    @Autowired
    private VipRecordsMapper vipRecordsMapper;
    @Autowired
    private UserMapper userMapper;

    @Scheduled(cron = "0 0 * * * *")
    public void execute(){
        logger.info("[BEGIN VIP RECORDS CHECK]");
        List<VipRecords> list=new LinkedList<>();
        List<String> expireList=new LinkedList<>();
        Date current=new Date();
        int page=0;
        do{
            list.clear();
            try {
                list=vipRecordsMapper.scanVipRecords(page * pageAmount, (page + 1) * pageAmount);
            }catch (Exception e){
                logger.warn("[FAILED CHECK] page#"+page,e);
            }
            list.forEach(record -> {
                Date date = record.getExpire();
                if (current.getTime() > date.getTime()) {
                    expireList.add(record.getDwid());
                }
            });
            page++;
        }while (list.size()==pageAmount);
        try {
            if(expireList.size()>1){
                userMapper.batchChangeUserType(expireList, UserType.UNCERTAIN.getName());
            }else if(expireList.size()==1){
                User user=new User();
                user.setId(expireList.get(0));
                user.setType(UserType.UNCERTAIN.getName());
                userMapper.updateByPrimaryKeySelective(user);
            }
        }catch (Exception e){
            logger.debug("[RESET_VIP_FAIL] list#"+ LogUtil.toLogString(expireList),e);
        }
    }
}
