package com.shadt.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shadt.application.MyApp;
import com.shadt.caibian_news.R;
import com.shadt.ui.db.Tongxunlu;
import com.shadt.ui.widget.CircleImageView;
import com.shadt.util.MyLog;

import java.util.Locale;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class DeatailFriendActivity extends BaseActivity {


    private TextView mTitle;
    private LinearLayout img_back, line_chat_msg;
    private StringBuffer mCallNumberText = new StringBuffer("");
    private String channelName = "channelid";
    private final int REQUEST_CODE = 0x01;
    Context mContext;
    CircleImageView img_head;
    TextView text_username, text_qiyename, text_loginname, text_bumen, text_real_name, text_phone, text_email;
    Tongxunlu.ResultBean mResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatail_friend);
        mContext = DeatailFriendActivity.this;
        mResult = (Tongxunlu.ResultBean) getIntent().getSerializableExtra("content");
        init();
    }

    @Override
    public void initPages() {

    }

    @Override
    public void onClickListener(View v) {

    }

    LinearLayout line_chat_call;

    public void init() {
        img_head = (CircleImageView) findViewById(R.id.img_head);
        img_back = (LinearLayout) findViewById(R.id.line_back);
        ImageLoader.getInstance().displayImage(get_sharePreferences_rongip() + mResult.getPortraituri(), img_head, MyApp.getOptions());

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = (TextView) findViewById(R.id.title);
        title.setText("用户详情");
        str_call_phone = "" + mResult.getUserId();
        mMyAccount = get_sharePreferences_ronguserid();
        line_chat_call = (LinearLayout) findViewById(R.id.line_chat_call);
        line_chat_msg = (LinearLayout) findViewById(R.id.line_chat_msg);
        text_username = (TextView) findViewById(R.id.text_username);

        text_loginname = (TextView) findViewById(R.id.text_loginname);
        text_bumen = (TextView) findViewById(R.id.text_bumen);
        text_real_name = (TextView) findViewById(R.id.text_real_name);
        text_phone = (TextView) findViewById(R.id.text_phone);
        text_email = (TextView) findViewById(R.id.text_email);
        if(TextUtils.isEmpty(mResult.getName())){
            text_email.setText("" );
        }else{
            text_username.setText("" + mResult.getName());
            text_loginname.setText("" + mResult.getName());//登录名
            text_bumen.setText("" + mResult.getDeptName());
            text_real_name.setText("" + mResult.getName());
        }


        if(TextUtils.isEmpty(mResult.getMobile())){
            text_phone.setText("" );
        }else{
            text_phone.setText("" + mResult.getMobile());
        }
        if(TextUtils.isEmpty(mResult.getEmail())){
            text_email.setText("" );
        }else{
            text_email.setText("" + mResult.getEmail());
        }


//        line_chat_call.setVisibility(View.GONE);
        line_chat_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webutils.isNetworkConnected(getApplication()) == true) {


                    if (str_call_phone.equals(mMyAccount)) {
                        Toast.makeText(DeatailFriendActivity.this, "不能拨打自己", Toast.LENGTH_SHORT).show();
                    } else {
                        RongCallSession profile = RongCallClient.getInstance().getCallSession();
                        if (profile != null && profile.getActiveTime() > 0) {
                            Toast.makeText(mContext,
                                    profile.getMediaType() == RongCallCommon.CallMediaType.AUDIO ?
                                            getString(io.rong.callkit.R.string.rc_voip_call_audio_start_fail) :
                                            getString(io.rong.callkit.R.string.rc_voip_call_video_start_fail),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                            Toast.makeText(mContext, getString(io.rong.callkit.R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
                        intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase(Locale.US));
                        intent.putExtra("targetId", mResult.getRyId());
                        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage(getPackageName());
                        getApplicationContext().startActivity(intent);
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo("" + mResult.getRyId(), "" + mResult.getName(), Uri.parse(get_sharePreferences_rongip() + "" + mResult.getPortraituri())));

                    }
                } else {
                    Toast.makeText(mContext, "世上最遥远的地方就是没有网络...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        line_chat_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (str_call_phone.equals(mMyAccount)) {
                    Toast.makeText(DeatailFriendActivity.this, "不能给自己发消息", Toast.LENGTH_SHORT).show();
                } else {
                    MyLog.i("" + mResult.getRyId()+ "name:" + mResult.getName());
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo("" + mResult.getRyId(), "" + mResult.getName(), Uri.parse(get_sharePreferences_rongip() + "" + mResult.getPortraituri())));
                    RongIM.getInstance().startPrivateChat(DeatailFriendActivity.this, "" + mResult.getRyId(), "" + mResult.getName());
                }
            }
        });
    }
}
