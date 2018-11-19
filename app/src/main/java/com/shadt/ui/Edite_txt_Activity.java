package com.shadt.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.shadt.caibian_news.R;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.MsgInfo;
import com.shadt.ui.db.UploadResultInfo;
import com.shadt.ui.db.WenjianjiaInfo;
import com.shadt.ui.utils.FileUtils;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.ui.widget.pupwindow.PopItemAction;
import com.shadt.ui.widget.pupwindow.PopWindow;
import com.shadt.util.MyLog;
import com.shadt.util.XMLParserUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shadt.ui.PictureActivity.RESULT_VIOCELINEVIEW;

public class Edite_txt_Activity extends BaseActivity {
    LinearLayout line_back, line_exit, line_version;
    TextView title, ok, cancel, txt_context;
    Button txt_save, txt_save_no;
    EditText edit;
    int my_position = 0;
    String context = "";
    private LayoutInflater mInflater;
    private View pop_view;
    private PopupWindow pop;
    String my_title;
    int length = 30000;
    private AlertDialog myDialog;
    int type = 101;
    boolean enable = true;
    ScrollView scor;
    boolean tuwen = false;
    boolean addnext = false;
    EditText ed_title;
    RelativeLayout rela_choose;
    TextView text_mywenjian;
    public boolean is_save = false;
    ImageView img_voiceline;
    private static final String TAG = "Edite_txt_Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_txt);
        initPages();

    }

    @Override
    public void initPages() {
        initTop();
        initView();
    }

    public void initTop() {
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText("文稿");
        ok = (TextView) findViewById(R.id.edit_ok);
        ok.setOnClickListener(this);
        img_voiceline=(ImageView)findViewById(R.id.img_voiceline);
        img_voiceline.setOnClickListener(this);
    }

    //新建还是编辑    false 是编辑.true是新建
    public boolean is_new = true;
    RelativeLayout rela_task;

    TextView text_task;
    String token = "";
    EditText ed_key;

    public void initView() {
        String name = getIntent().getStringExtra("name");
        context = getIntent().getStringExtra("context");
        token = get_sharePreferences_UserToken();
        is_new = getIntent().getBooleanExtra("new", true);
        MyLog.i(">>new" + is_new);
        edit = (EditText) findViewById(R.id.edit_context);
        ed_title = (EditText) findViewById(R.id.ed_title);
        txt_context = (TextView) findViewById(R.id.txt_context);
        scor = (ScrollView) findViewById(R.id.scor);
        rela_choose = (RelativeLayout) findViewById(R.id.rela_choose);
        rela_choose.setOnClickListener(this);
        text_mywenjian = (TextView) findViewById(R.id.text_mywenjian);

        ed_key = findViewById(R.id.ed_key);

        rela_task = findViewById(R.id.rela_task);
        text_task = findViewById(R.id.text_task);
        rela_task.setOnClickListener(this);

        if (type == 0) {
            edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if (enable == false) {
            MyLog.v("不能编辑");
            edit.setEnabled(false);
            ok.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            txt_context.setText(context);
        } else {
            MyLog.v("能编辑");
            scor.setVisibility(View.GONE);
            if (addnext == true) {

            } else {
                edit.setText(context);
            }
            try {
                edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        length)});
                // edit.setSelection(context.length());
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        if (!TextUtils.isEmpty(name)) {
            if (name.length() > 23) {
                MyLog.i(">>>named" + name.substring(0, name.length() - 23));
                ed_title.setText(name.substring(0, name.length() - 23));

                if (TextUtils.isEmpty(context)) {
                    context = getString(name);
                }
                edit.setText(context);
            }
        }
    }

    private void initpop() {
        // TODO Auto-generated method stub

        myDialog = new AlertDialog.Builder(Edite_txt_Activity.this).create();
        myDialog.show();
        myDialog.getWindow().setContentView(R.layout.popu_eorr);
        myDialog.getWindow().findViewById(R.id.txt_save_no)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        finish();
                    }
                });
        myDialog.getWindow().findViewById(R.id.txt_save)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        save_txt(false);

                        finish();
                    }
                });
    }

    AlertDialog myDialog_save;

    //true表示保存本地并且上传
    public void save_txt(boolean is_upload) {
        long timeStamp = System.currentTimeMillis();
        MyLog.i("timeStamp" + (getDateToString(timeStamp) + ".txt").length());
        String s = ed_title.getText().toString() + getDateToString(timeStamp);
        s = stringFilter(s);
        fileName = s + ".txt";
        writeTxtToFile(edit.getText().toString(), is_upload);
    }

    private void popsave() {
        // TODO Auto-generated method stub

        myDialog_save = new AlertDialog.Builder(Edite_txt_Activity.this).create();
        myDialog_save.show();
        myDialog_save.getWindow().setContentView(R.layout.popu_save);
        myDialog_save.getWindow().findViewById(R.id.txt_save_no)

                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog_save.dismiss();

                        save_txt(true);

                    }
                });
        myDialog_save.getWindow().findViewById(R.id.txt_save)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog_save.dismiss();
                        save_txt(false);
                        finish();

                    }
                });
    }


    public void cancel() {
        MyLog.i("11111111111111");
        if (context != null) {
            if (context.equals(edit.getText().toString())) {
                finish();
            } else {
                if (edit.getText().toString().length() == 0) {

                    edit.requestFocus();
                    edit.setError("内容不能为空");
                } else {
                    //如果是新建，返回提示保存，不是直接退出
                    if (is_new == true) {
                        if (is_save == true) {
                            finish();
                        } else {
                            if (ed_title.getText().length() == 0) {
                                ed_title.requestFocus();
                                ed_title.setError("标题不能为空");
                            } else {
                                if (edit.getText().length() == 0) {
                                    edit.requestFocus();
                                    edit.setError("内容不能为空");
                                } else {
                                    initpop();
                                }
                            }

                        }
                    } else {
                        finish();
                    }

                }
            }
        } else {

            if (edit.getText().toString().length() != 0) {
                if (is_save == false) {
                    initpop();
                }else{
                    finish();
                }

            } else {
                finish();
            }
        }
    }

    String fileName;

    @Override
    public void onClickListener(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.cancel:

                if (enable == false) {
                    finish();
                } else {
                    cancel();
                }
                break;
            case R.id.edit_ok:
                if (ed_title.getText().length() == 0) {
                    ed_title.requestFocus();
                    ed_title.setError("标题不能为空");
                } else {
                    //                    if (ed_title.getText().toString().contains(s[i])
                    if (edit.getText().length() == 0) {
                        edit.requestFocus();

                        edit.setError("内容不能为空");
                    } else {
                        if (!TextUtils.isEmpty(wenjian_bianhao)) {
                            if (TextUtils.isEmpty(task_id)) {
                                Toast.makeText(this, "您还没有选择任务绑定", 0).show();
                            } else {
                                popsave();
                            }

                        } else {
                            Toast.makeText(this, "请选择上传的文件夹", 0).show();
                        }

                    }

                }
                break;
            case R.id.rela_choose:
                if (userInfo == null) {
                    rela_choose.setEnabled(false);
                    Upload_wenjianjia(Contact.meizi_apikey);
                } else {
                    showpop();
                }
                break;
            case R.id.rela_task:

                Intent its = new Intent(this, MyTaskActivity.class);
                startActivityForResult(its, 10086);
                break;
            case R.id.img_voiceline:
                   Intent it=new Intent(this,VioceLineViewActivity.class);
                it.putExtra("activity",TAG);
                startActivityForResult(it,RESULT_VIOCELINEVIEW);
                break;
            default:
                break;
        }
    }

    /**
     * 过滤一些特殊字符
     */
    private String stringFilter(String str) {
        String result = "";
        try {
            str = str.replaceAll("\\\\", "");
            String regEx = "[`~!@#$%^&*()+=|{}'.:;'\\[\\]<>/?？~@#￥%……&*]";//+号表示空格
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            result = m.replaceAll("").trim();
        } catch (Exception e) {
            MyLog.i("过滤报错");
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 1235645488 - 0 > 3000
            if (enable == false) {
                finish();
            } else {
                cancel();
            }
        }
        return false;
    }

    private final String reg = "^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";

    private Pattern pattern = Pattern.compile(reg);
    // 输入表情前的光标位置
    private int cursorPos;
    // 输入表情前EditText中的文本
    private String tmp;
    // 是否重置了EditText的内容
    private boolean resetText;


    public String getDateToString(long milSecond) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return format.format(date);
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, boolean is_upload) {


        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(FileUtils.path, fileName);

        String strFilePath = FileUtils.path + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {

                MyLog.i("Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes("GBK"));
            raf.close();
            if (is_upload == true) {
                new Thread(networkTask_tijiao).start();
            }
            is_save = true;
            Intent it = new Intent();
            it.setAction("refresh");
            sendBroadcast(it);

        } catch (Exception e) {
            showNotifyDialog("文稿生成失败");
        }
    }


    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            MyLog.i(e + "");
        }
    }

    //读取文本
    public String getString(String name) {


        StringBuffer sb = new StringBuffer("");
        try {
            String pathname = FileUtils.path + name; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "GBK"));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        MyLog.i("sb:" + sb.toString());
        return sb.toString();
    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    showLoadingDialog("文稿上传中...");
                    break;
                case 0:
                    dismissLoadingDialog();
                    break;
                case 2:
                    showLoadingDialog("视频压缩中......");
                    break;
                case 3:

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

    //文件夹
    public void Upload_wenjianjia(String api_key) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("method", "getfolders");
        params.addBodyParameter("api_key", api_key);
        MyLog.i("api_key" + api_key);

        HttpUtils httpUtils = new HttpUtils();
        String postUrl = Contact.meizi_ip + Contact.meizi_getwenjianjia;
        MyLog.i(postUrl);
        httpUtils.send(HttpRequest.HttpMethod.POST, postUrl, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                rela_choose.setEnabled(true);
                MyLog.i("失败" + arg1);
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(0);
                String str = arg0.result;
                MyLog.i("成功" + str);
                rela_choose.setEnabled(true);
                userInfo = JsonUtils.getModel(str, WenjianjiaInfo.class);//这里已经解析出来了
//                showPopwindow();
                showpop();
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
                handler.sendEmptyMessage(1);
                Submit_tuwen_img(FileUtils.path + fileName);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Looper.loop();
        }
    };
    List<UploadResultInfo> list_upload;

    public void Submit_tuwen_img(String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String meta_description = "&meta_file_desc=";
        String meta_keywords = "&meta_file_keywords=";
        try {
            String Posturl = Contact.meizi_upload + "?fa=c.apiupload&api_key=" + Contact.meizi_apikey + "&metadata=1" + "&meta_lang_id_r=1" + "&zip_extract=0" + "&meta_location=仙霞路350" + "&destfolderid=" + wenjian_bianhao + meta_keywords + URLEncoder.encode(ed_key.getText().toString(), "UTF-8")+meta_description;
           MyLog.i(Posturl);
            HttpPost httppost = new HttpPost(Posturl);
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

                if (statusCode == HttpStatus.SC_OK) {
                    // 通过HttpEntity获得响应流
                    InputStream is = httpResponse.getEntity().getContent();
                    // 将流转换成string
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    String string = out.toString("UTF-8");
                    MyLog.i("result" + string);

                    is = new ByteArrayInputStream(string.getBytes("UTF-8"));

                    handler.sendEmptyMessage(0);

                    try {

                        list_upload = new ArrayList<UploadResultInfo>();
                        list_upload = XMLParserUtil.parse_upload_result(is);
                        if (list_upload != null) {
                            if (list_upload.get(0).getResponsecode().equals("0")) {
//                                showNotifyDialogSuccese("上传成功");
                                LoadSource(task_id, list_upload.get(0).getAssetid(), token);
                            } else {

                                showNotifyDialog("上传失败");
                            }
                        }
                    } catch (Exception e1) {
                        MyLog.i("失败1111");
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                        showNotifyDialog("上传失败");
                    }
                } else {
                    failed();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                MyLog.i("失败222");
                failed();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            failed();
            e.printStackTrace();
        }
    }


    public void LoadSource(String taskid, String mediaid, String token) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("taskid", taskid);
        MyLog.i("mediaid" + mediaid);
        params.addBodyParameter("mediaids", mediaid);
        params.addBodyParameter("token", token);

        HttpUtils httpUtils = new HttpUtils();
        String postUlr = Contact.rong_ip + Contact.add_task;
        MyLog.i("postUlr" + postUlr);
        httpUtils.send(HttpRequest.HttpMethod.POST, postUlr, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                showNotifyDialog("绑定任务失败");

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(5);

                MyLog.i("chengong：" + arg0.result);

                MsgInfo mUserInfo = JsonUtils.getModel(arg0.result, MsgInfo.class);

                showNotifyDialog("" + mUserInfo.getMsg());

            }
        });
    }

    public void failed() {

        handler.sendEmptyMessage(0);
        showNotifyDialog("上传失败");

    }

    String choose_wenjianjia = "";
    String wenjian_bianhao;

    public void showpop() {
        PopWindow popWindow = new PopWindow.Builder(this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setTitle("文件夹选择")

                .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel))
                .create();
        if (userInfo.getDATA().size() == 1) {

            if (userInfo.getDATA().get(0).get(1).contains("登录无效")) {
                popWindow.addItemAction(new PopItemAction("" + userInfo.getDATA().get(0).get(1), PopItemAction.PopItemStyle.Warning, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {

                    }
                }));

            } else {
                final String choose_wenjianjiaa = userInfo.getDATA().get(0).get(1);
                final String wenjian_bianhaos = userInfo.getDATA().get(0).get(0);
                popWindow.addItemAction(new PopItemAction("" + userInfo.getDATA().get(0).get(1), PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        choose_wenjianjia = choose_wenjianjiaa;
                        wenjian_bianhao = wenjian_bianhaos;
                        text_mywenjian.setText(choose_wenjianjia);
                    }
                }));
            }
        } else {
            for (int i = 0; i < userInfo.getDATA().size(); i++) {
                final String choose_wenjianjiaa = userInfo.getDATA().get(i).get(1);
                final String wenjian_bianhaos = userInfo.getDATA().get(i).get(0);
                popWindow.addItemAction(new PopItemAction("" + userInfo.getDATA().get(i).get(1), PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        choose_wenjianjia = choose_wenjianjiaa;
                        wenjian_bianhao = wenjian_bianhaos;
                        text_mywenjian.setText(choose_wenjianjia);
                    }
                }));
            }
        }

        popWindow.show();
    }

    public void showNotifyDialogSuccese(String msg) {
        Dialog dialog = new AlertDialog.Builder(this).setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        finish();
                    }

                }).create();
        dialog.show();
    }

    String task_id = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10086) {
            MyLog.i(">>>>>>>>>>>>.111111");
            if (requestCode == 10086) {
                MyLog.i(">>>>>>>>>>>>.22222");
                if (data != null) {
                    String title = data.getStringExtra("title");
                    MyLog.i(">>>>>>>>>>>>.title" + title);
                    String taskid = data.getStringExtra("id");
                    if (!TextUtils.isEmpty(title)) {
                        text_task.setText(title);
                    }
                    if (!TextUtils.isEmpty(taskid)) {
                        task_id = taskid;
                    }
                }
            }
        }else if(requestCode==RESULT_VIOCELINEVIEW){
            if (data!=null) {
                edit.setText(edit.getText().toString()+ data.getStringExtra("result"));
                if (edit.getText().length()>0){
                    edit.setSelection(edit.getText().length());
                }
                MyLog.i("返回的语音信息" + data.getStringExtra("result"));
            }
        }

    }

}
