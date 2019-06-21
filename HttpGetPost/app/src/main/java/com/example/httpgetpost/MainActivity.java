package com.example.httpgetpost;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int GET_RESULT = 1001;
    private Button b1;
    private Button b2;
    private EditText ed_phoneNum;
    private TextView t1;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        ed_phoneNum = findViewById(R.id.edit_phoneNum);
        t1 = findViewById(R.id.t1);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == GET_RESULT){
                    String html = (String) msg.obj;
                    t1.setText(html);
                    return true;
                }
                return false;
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String phone = ed_phoneNum.getText().toString();
                        useHttpGet(phone);
                    }
                }).start();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String phone = ed_phoneNum.getText().toString();
                        useHttpGet(phone);
                    }
                }).start();

            }
        });
    }

    private void useHttpPost(String phone) {
        String url = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";
        HttpPost httpPost = new HttpPost(url);
        HttpClient httpClient = new DefaultHttpClient();
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("mobileCode", phone));
        nameValuePairList.add(new BasicNameValuePair("userID", ""));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String htmlContent = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            Log.e(TAG, htmlContent);

            Message message = Message.obtain();
            message.what = GET_RESULT;
            message.obj = htmlContent;
            handler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void useHttpGet(String phone) {
        String url = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo?mobileCode=";
        url = url.concat(phone).concat("&userID=");
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int code = httpResponse.getStatusLine().getStatusCode();
            Log.e(TAG, "code = " + code);
//            String htmlContent = getHtmlContent(httpResponse);
//            Log.e(TAG, htmlContent);
            String htmlContent = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            Log.e(TAG, htmlContent);

            Message message = Message.obtain();
            message.what = GET_RESULT;
            message.obj = htmlContent;
            handler.sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
