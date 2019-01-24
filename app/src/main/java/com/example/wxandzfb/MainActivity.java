package com.example.wxandzfb;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private MyHandler mHandler = new MyHandler(this);
    private String from = "服务器返回的相关数据";
    private IWXAPI iwxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.btn_wx:
                startWx();
                break;
            case R.id.btn_zfb:
                startZfb();
                break;
        }
    }

    private void startWx() {
        iwxapi = WXAPIFactory.createWXAPI(this, null);
        iwxapi.registerApp("你的appId");
        new Thread(new Runnable() {
            @Override
            public void run() {
                PayReq request = new PayReq(); //调起微信APP的对象
                //下面是设置必要的参数，也就是前面说的参数,这几个参数从何而来请看上面说明
                request.appId = "你的appId";
                request.partnerId = "1234";
                request.prepayId = "1234";
                request.packageValue = "Sign=WXPay";
                request.nonceStr = "12324";
                request.timeStamp = "1234";
                request.sign = "1234567890987654";
                iwxapi.sendReq(request);//发送调起微信的请求
            }
        }).start();
    }


    //调用支付宝支付接口
    private void startZfb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //调用支付宝
                PayTask payTask = new PayTask(MainActivity.this);
                //form 是从服务器获取来的一段字符串，由阿里的大佬们封装好了，完全不需要我们进行任何操作。
                String result = payTask.pay(from, false);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();
    }


    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        private MyHandler(MainActivity mActivity) {
            this.mActivity = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        String result = ((String) msg.obj).replace("{", "")
                                .replace("}", "").replace("resultStatus=", "")
                                .replace("memo=", "").replace("result=", "");
                        Log.d("MainActivity:", result);
                        String num = result.split(";")[0];
                        showPayDialog(num);
                        break;
                }
            }
        }
    }

    //展示吐司
    private static void showPayDialog(String num) {
        String result;
        switch (num) {
            case "9000":
                result = "订单支付成功";
                break;
            case "8000":
                result = "支付结果未知，请联系客服";
                break;
            case "4000":
                result = "订单支付失败";
                break;
            case "5000":
                result = "重复请求";
                break;
            case "6001":
                result = "订单取消成功";
                break;
            case "6002":
                result = "网络连接出错";
                break;
            case "6004":
                result = "支付结果未知，请联系客服";
                break;
            default:
                result = "支付失败，请联系客服";
        }

        Toast.makeText(MyApplication.getContext(), result, Toast.LENGTH_LONG).show();
    }


}
