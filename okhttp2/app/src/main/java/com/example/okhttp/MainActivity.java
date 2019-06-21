package com.example.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity" ;
    private static final int GET_RES = 1001;
    private static final int GET_IMG = 1002;
    private TextView txt_response;
    private Handler handler;
    private ImageView imageView;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imgv);

        okHttpClient = new OkHttpClient();

        handler =  new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if(message.what == GET_RES){
                    String content = (String) message.obj;
                    txt_response.setText(content);
                    return true;
                }else if( message.what == GET_IMG){
                    Bitmap bitmap = (Bitmap) message.obj;
                    imageView.setImageBitmap(bitmap);
                }
                return false;
            }
        });

        txt_response = findViewById(R.id.t1);

        findViewById(R.id.b1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsynGetImg();
            }
        });

        findViewById(R.id.b2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncGet();
            }
        });

        findViewById(R.id.b3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetInfo("13242005659");
            }
        });

        findViewById(R.id.b4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostInfo("13242005659");
            }
        });
    }

    private void PostInfo(String phone) {
        String url = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";
        RequestBody requestBody = new FormBody.Builder()
                .add("mobileCode", phone)
                .add("userID", "")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                String content = response.body().string();
                Log.e(TAG, content);
                Message message = Message.obtain();
                message.what = GET_RES;
                message.obj = content;
                handler.sendMessage(message);

            }
        });

    }

    private void GetInfo(String phone) {
        String url = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";
        url = url.concat("?mobileCode=").concat(phone).concat("&userID=");
        Request request = new Request.Builder().url(url)
                .get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                String content = response.body().string();
                Log.e(TAG, content);
                Message message = Message.obtain();
                message.what = GET_RES;
                message.obj = content;
                handler.sendMessage(message);

            }
        });
    }

    private void SyncGet() {
        String url = "http://www.baidu.com";
        Request request = new Request.Builder().url(url)
                .get().build();
        final Call call = okHttpClient.newCall(request);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    String content = response.body().string();
                    Log.e(TAG, content);
                    Message message = Message.obtain();
                    message.what = GET_RES;
                    message.obj = content;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    // 异步GEt
    private void AsynGet() {
        String url = "http://www.baidu.com";
        Request request = new Request.Builder().url(url)
                .get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                String content = response.body().string();
                Log.e(TAG, content);
//                txt_response.setText(content);
                Message message = Message.obtain();
                message.what = GET_RES;
                message.obj = content;
                handler.sendMessage(message);

            }
        });
    }

    // 异步GEt
    private void AsynGetImg() {
        String imgUrl ="http://jsxy.gdcp.cn/UploadFile/2/2019/3/19/2019319124832881.jpg";

        Request request = new Request.Builder().url(imgUrl)
                .get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Message message = Message.obtain();
                message.what = GET_IMG;
                message.obj = bitmap;
                handler.sendMessage(message);

            }
        });
    }
}
