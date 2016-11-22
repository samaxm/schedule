package online.decentworld.schedule.task;

import online.decentworld.cache.redis.CacheKey;
import online.decentworld.cache.redis.RedisTemplate;
import online.decentworld.cache.redis.ReturnResult;
import online.decentworld.rdb.hbase.HTableHelper;
import online.decentworld.rdb.hbase.HbaseClient;
import online.decentworld.rdb.hbase.PutRequest;
import online.decentworld.rpc.dto.message.protos.MessageProtos;
import online.decentworld.tools.HbaseRowKeyHelper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class RestoreChatRecordsFromRedis2Hbase implements Job{

    private static Logger logger= LoggerFactory.getLogger(RestoreChatRecordsFromRedis2Hbase.class);
;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("[BEGIN_CHAT_RECORD_PERSIST_TASK]");
        RedisTemplate template=new RedisTemplate();
        ReturnResult result=template.cache((Jedis jedis)->{
            List<byte[]> list=null;
            Set<String> ids=jedis.smembers(CacheKey.MESSSAGE_STORE_SET);
            list=template.getFromHSETBytes(CacheKey.MESSAGE,ids,jedis);
            return ReturnResult.result(list);
        });
        if(result.isSuccess()&&result.getResult()!=null){
            List<byte[]> msgs= (List<byte[]>) result.getResult();
            List<Long> stored=new LinkedList<>();
            List<Long> failed=new LinkedList<>();
            Calendar calendar=Calendar.getInstance();
            int month=calendar.get(Calendar.MONTH);
            int year=calendar.get(Calendar.YEAR);
            String subfix=String.valueOf(year)+String.valueOf(month);
            PutRequest request=new PutRequest((HTableHelper.CHAT_PREFIX+subfix).getBytes(),HTableHelper.CHAT_COLUMN_RECORD.getBytes());
            msgs.forEach((byte[] data)->{
                long mid=-1;
                try {
                    MessageProtos.Message msg=MessageProtos.Message.parseFrom(data);
                    String from=msg.getFrom();
                    String to=msg.getTo();
                    byte[] rowkey= HbaseRowKeyHelper.getChatHistoryRowkey(from,to,year+""+month);
                    mid=msg.getMid();
                    request.add(rowkey,String.valueOf(mid).getBytes(),data);
//                    if (mw.getType() == MessageType.CHAT_TEXT||
//                            mw.getType() == MessageType.CHAT_IMAGE||
//                            mw.getType() == MessageType.CHAT_AUDIO) {
//                        String receiver = mw.getReceiverID();
//                        String sender = mw.getSenderID();
//                        ChatIndex index = new ChatIndex(sender, receiver, true, mid);
//                        ChatIndex index2 = new ChatIndex(receiver, sender, false, mid);
//                        ChatRecord cr = new ChatRecord();
//                        cr.setMid(mid);
//                        cr.setData(codec.encode(mw));
//                        indexMapper.insert(index);
//                        indexMapper.insert(index2);
//                        recordMapper.insert(cr);
//                    }
                    stored.add(mid);
                }catch (Exception e){
                    logger.warn("[ERROR_PERSIST] ",e);
                    if(mid!=-1){
                        failed.add(mid);
                    }
                }
            });
            try {
                HbaseClient.instance().put(request);
                //clean cache
//                ReturnResult result1=template.cache((Jedis jedis)->{
//                    if(failed.size()!=0){
//                        jedis.rpush(CacheKey.FAIL_STROE_MSG,failed.toArray(new String[failed.size()]));
//                    }
//                    stored.addAll(failed);
//                    if(stored.size()!=0)
//                        jedis.hdel(CacheKey.MESSAGE,stored.toArray(new String[stored.size()]));
//                    return ReturnResult.SUCCESS;
//                });
//                if(!result1.isSuccess()){
//                    logger.warn("[CLEAN_CHAT_CACHE_FAIL] stored#"+ LogUtil.toLogString(stored));
//                }
            } catch (IOException e) {
                logger.warn("",e);
            }

        }
    }


}
