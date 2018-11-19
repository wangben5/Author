/*
package com.shadt.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.shadt.caibian_news.R;
import com.shadt.ui.adapater.MyAdapter;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.WenjianjiaInfo;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.util.Contacts;
import com.shadt.util.MyLog;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class UploadActivity extends BaseActivity {
    RelativeLayout rela_upload, rela_choose;
    TextView text_mywenjian;
    TextView text_size;
    TextView text_leixin;
    ImageView img;
    EditText ed_miaosu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initPages();
    }

    String url;
    String choose_wenjianjia = "";
    String type;
    String wenjian_bianhao;

    @Override
    public void initPages() {
        url = getIntent().getStringExtra("url");
        type = getIntent().getStringExtra("type");
        MyLog.i(url);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        line_back = (LinearLayout) findViewById(R.id.line_back);
        line_back.setOnClickListener(this);
        rela_choose = (RelativeLayout) findViewById(R.id.rela_choose);
        rela_upload = (RelativeLayout) findViewById(R.id.rela_upload);
        rela_upload.setOnClickListener(this);
        rela_choose.setOnClickListener(this);
        text_mywenjian = (TextView) findViewById(R.id.text_mywenjian);
        text_leixin = (TextView) findViewById(R.id.text_leixin);
        text_size = (TextView) findViewById(R.id.text_size);
        text_size.setText("文件大小:" + getFileSize(url));
        text_leixin.setText("文件类型:" + type);
        img = (ImageView) findViewById(R.id.img);
        ed_miaosu = findViewById(R.id.ed_miaosu);
        Glide.with(this)
                .load(url)
                .apply(options)
                .into(img);
    }

    private String getFileSize(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return "0 MB";
        } else {
            long size = f.length();


            return new DecimalFormat("0.00").format((double)(size / 1024f) / 1024f) + "MB";
        }
    }

    private int themeId;

    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.line_back:
                finish();

                break;
            case R.id.rela_choose:
                PhoneToLogin(Contact.meizi_apikey);
                break;
            case R.id.rela_upload:
                new Thread(networkTask_tijiao).start();

                break;
            case R.id.img:
//                themeId = R.style.picture_default_style;
//                PictureSelector.create(this).themeStyle(themeId).openExternalPreview(0, url);
                break;
            default:
                break;
        }
    }

    */
/**
     * 获取指定文件大小
     *
     * @param f
     * @return
     * @throws Exception
     *//*

    public static String getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            MyLog.e("获取文件大小");
        }
        return FormetFileSize(size);
    }

    */
/**
     * 转换文件大小
     *
     * @param fileS
     * @return
     *//*

    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    ArrayList<String> str;

    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popwindowlayout, null);
        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.WindowStyle);
        // 在底部显示
        window.showAtLocation(this.findViewById(R.id.rela_upload), Gravity.BOTTOM, 0, 0);
        //添加控件绑定并配置适配器


        final ListView goodsCart = (ListView) view.findViewById(R.id.goods_cart_lv);

        str = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            str.add(i, "" + i);
        }
        goodsCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choose_wenjianjia = userInfo.getDATA().get(i).get(1);
                text_mywenjian.setText(choose_wenjianjia);
                wenjian_bianhao=userInfo.getDATA().get(i).get(0);
                window.dismiss();
            }
        });
        MyAdapter myAdapter = new MyAdapter(getApplicationContext(), userInfo);
        goodsCart.setAdapter(myAdapter);
        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

            }
        });
    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:


                    break;
                case 0:
                    hidedialogs();
                    break;
                case 2:

                    break;
                case 3:
                    showdialog();
                    break;
                case 4:

                    break;
                case 5:

                    break;

            }
        }

        ;
    };
    WenjianjiaInfo userInfo;

    public void PhoneToLogin(String api_key) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("method", "getfolders");
        params.addBodyParameter("api_key", api_key);
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("vsOutData6", "");

        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.POST, Contact.meizi_getwenjianjia, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                MyLog.i("失败：" + arg1.toString());

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(0);
                String str = arg0.result;
                MyLog.i("融媒体账号登录返回数据：" + str);
                userInfo = JsonUtils.getModel(str, WenjianjiaInfo.class);//这里已经解析出来了
                showPopwindow();
            }
        });
    }
    Runnable networkTask_tijiao = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();
            try {
                Submit_tuwen_img();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Looper.loop();
        }
    };
    public void Submit_tuwen_img() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost(Contact.meizi_upload+"?fa=c.apiupload&api_key="+Contact.meizi_apikey+"&destfolderid="+wenjian_bianhao);
            MultipartEntity reqEntity = new MultipartEntity();
            FileBody file = new FileBody(new File(url));
            reqEntity.addPart("filedata", file);

            // file2为请求后台的File
            // upload;属性
            httppost.setEntity(reqEntity);

            HttpResponse httpResponse;
            try {
                httpResponse = httpclient.execute(httppost);
                final int statusCode = httpResponse.getStatusLine()
                        .getStatusCode();
                //取消dialog
                handler.sendEmptyMessage(3);
                if (statusCode == HttpStatus.SC_OK) {
                    // Toast.makeText(AnjianBianjiActivity.this, "上传成功",
                    // 0).show();
                    String result = EntityUtils.toString(
                            httpResponse.getEntity(), "UTF-8");
                    Log.v("ceshi2", "result" + result);
 //                    str = XMLParserUtil.parse_img(result);

                    int a = 0;


//                    handler.sendEmptyMessage(1);
                } else {
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.v("ceshi2", "失败");
            } finally {
                handler.sendEmptyMessage(3);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
*/
