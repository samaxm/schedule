import online.decentworld.tools.MD5;

import java.io.IOException;

/**
 * Created by Sammax on 2016/11/22.
 */
public class Common{
    public static void main(String[] args) throws IOException, ClassNotFoundException{

        System.out.println(System.currentTimeMillis()-10*60*1000);
//        TextChatMessage textChatMessage=new TextChatMessage(ChatStatus.NORMAL,"100",
//                "wlker", ChatRelation.STRANGER,"777","666","helloworld");
//        MessageWrapper messageWrapper=MessageWrapper.createMessageWrapper("777","666",textChatMessage,1);
//        SimpleProtosCodec codec= new SimpleProtosCodec();
//        codec.setConverterFactory(new ReflectConverterFactory());
//        byte[] data=codec.encode(messageWrapper);
//        HbaseClient.instance().put((HTableHelper.CHAT_PREFIX+"201610").getBytes(),getRowKey("777","666",10,2016),HTableHelper.CHAT_COLUMN_RECORD.getBytes(),"123".getBytes(),data);
//        HbaseClient.instance().close();

    }

    private static byte[] getRowKey(String from,String to,int month,int year){
        String rowKey=String.valueOf(year)+String.valueOf(month);
        if(Long.parseLong(from)>Long.parseLong(to)){
            rowKey=to+from+rowKey;
        }else{
            rowKey=from+to+rowKey;
        }
        return MD5.getMD5(rowKey);
    }


}
