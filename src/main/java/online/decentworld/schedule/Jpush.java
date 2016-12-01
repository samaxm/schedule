package online.decentworld.schedule;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import online.decentworld.rpc.dto.message.types.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class Jpush {

//  private String masterSecret="42233e4f5db1de69eaecd10e";
    private String masterSecret="67c5fc209619981c10962abb";
//  private String appKey="18b736277743d26c481172f8";
    private String appKey="6a8f279e406382c2a8f9f684";
    private JPushClient jpushClient;
    private static Logger logger= LoggerFactory.getLogger(Jpush.class);
    private static String DEFAULT_SOUND="happy.caf";
    private static String JSON="JSON";
    private static String NOTICE_TAG="ONLINE_NOTICE";

    public Jpush() {
        jpushClient= new JPushClient(masterSecret, appKey);
    }

    public void pushNotice(String notice, String receiver) {
        logger.debug("[PUSH_NOTICE] notice#"+notice+" receiver#"+receiver);
        try {
            PushPayload load=createNotice(receiver, notice);
            jpushClient.sendPush(load);
        } catch (APIConnectionException | APIRequestException e) {
            logger.warn("[PUSH_MESSAGE_ERROR] receiver#"+receiver+" notice#"+notice,e);
        }
    }

    public void pushMessage(String receiver,MessageType title,String notice) {
        logger.debug("[PUSH_NOTICE] notice#"+notice+" receiver#"+receiver);
        try {
            PushPayload load=createMessage(receiver, notice,title);
            jpushClient.sendPush(load);
        } catch (APIConnectionException | APIRequestException e) {
            logger.warn("[PUSH_MESSAGE_ERROR] receiver#"+receiver+" notice#"+notice,e);
        }
    }

    public void pushOnlineNotice(){
        try {
            logger.debug("[pushing online notice]");
            PushPayload load= createAndroidNoticeByTag(NOTICE_TAG, "有用户上线来，快去碰碰运气吧！");
            jpushClient.sendPush(load);
            PushPayload load2= createIOSNoticeByTag(NOTICE_TAG,"有用户上线来，快去碰碰运气吧！");
            jpushClient.sendPush(load2);
        } catch (Exception e) {
            logger.warn("",e);
        }
    }

    private PushPayload createNotice(String token,String message){
        PushPayload payload=PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(token))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(message)
                                .setBadge(1)
                                .setSound(DEFAULT_SOUND)
                                .build())
                        .build())
                .build();
        return payload;
    }

    private PushPayload createAndroidNoticeByTag(String tag,String message){
        PushPayload payload=PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.tag(tag))
                .setNotification(Notification.android(message, "上线提醒", null))
                .build();
        return payload;
    }

    private PushPayload createIOSNoticeByTag(String tag,String message){
        PushPayload payload=PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.tag(tag))
                .setNotification(Notification.newBuilder().addPlatformNotification(IosNotification.newBuilder()
                        .setAlert(message)
                        .setBadge(1).addExtra("from", "decentworld")
                        .setSound("happy").build())
                        .build())
                .build();
        return payload;
    }


    private PushPayload createMessage(String token,String message,MessageType title){
        PushPayload payload=PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(token))
                .setMessage(Message.newBuilder().setMsgContent(message).setContentType(JSON).setTitle(title.name()).build())
                .build();
        return payload;
    }



    public static void main(String[] args) {
        Jpush jpush=new Jpush();
        jpush.pushOnlineNotice();
//        LikeMessageBody body=n
// ew LikeMessageBody("http://dev.service.dawan.online/group1/M00/00/13/cEodc1gqbVeASc0TAADKuGWfeGA824.jpg","吓跑鱼","1845581884","1851236884","男");
//        MessageWrapper messageWrapper=MessageWrapper.createMessageWrapper("1845581884","1851236884",body,0);
//        System.out.println(com.alibaba.fastjson.JSON.toJSONString(messageWrapper));
//        jpush.pushMessage("3469464206",MessageType.NOTICE_LIKE, com.alibaba.fastjson.JSON.toJSONString(messageWrapper));
////        jpush.pushNotice(com.alibaba.fastjson.JSON.toJSONString(body),"1845581884");
    }
}
