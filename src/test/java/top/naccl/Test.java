package top.naccl;

import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;

public class Test {
    private static final String QQ_NICKNAME_URL = "https://users.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg?uins={1}";

    public static void main(String[] args) {
        String id = "738555306";
        /*String res = new RestTemplate().getForObject(QQ_NICKNAME_URL, String.class, id);
        System.out.println(res);
        try {
            System.out.println(new String(res.getBytes("GBK"), StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        SortedMap<String,Charset> charsetSortedMap = Charset.availableCharsets();
        System.out.println(charsetSortedMap);
        for(String s : charsetSortedMap.keySet())
        new Thread(() -> {
            System.out.println(sendGet("https://users.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg", "uins="+id, s));
        }).start();
    }

    public static String sendGet(String url, String param, String charset) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
// 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
// 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
// 建立实际的连接
            connection.connect();
// 定义 BufferedReader输入流来读取URL的响应
            InputStreamReader reader = new InputStreamReader(
                    connection.getInputStream(), charset);
            in = new BufferedReader(reader);
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
// 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
