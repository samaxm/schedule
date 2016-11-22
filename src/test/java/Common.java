import online.decentworld.rdb.hbase.HbaseClient;
import online.decentworld.tools.MD5;
import org.apache.hadoop.hbase.client.Result;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Sammax on 2016/11/22.
 */
public class Common{
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Class.forName("org.apache.hadoop.hbase.util.ByteStringer");
        Calendar calendar = Calendar.getInstance();
        String chat_table_prefix="chat";
        String records_cf="records";
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        byte[] rowkey = getRowKey("123", "456", month, year);
        HbaseClient client=HbaseClient.instance();
        String subfix = String.valueOf(year) + String.valueOf(month);

        Result result= client.get((chat_table_prefix + subfix).getBytes(), rowkey);
        System.out.println("123");
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
