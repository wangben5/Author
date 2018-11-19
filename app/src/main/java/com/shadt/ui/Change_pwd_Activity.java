package com.shadt.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import com.shadt.activity.LoginActivity;
import com.shadt.bean.UpdateInfo;
import com.shadt.caibian_news.R;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.MsgInfo;
import com.shadt.ui.db.UserInfo;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.util.MyLog;
import com.shadt.util.PasswordTextWatcher;
import com.shadt.util.RegularUtils;

public class Change_pwd_Activity extends BaseActivity implements
		OnClickListener {
	LinearLayout back;
	TextView title;
	Context mcontext = Change_pwd_Activity.this;
	EditText old_pwd, new_pwd, qr_pwd;
	RelativeLayout ok;
	SharedPreferences preferences;
	String password = null, phone = null, img = null, id = null;
	protected UpdateInfo update;
	int lerbie_type = 0;
	public final int INT_HIDE_DIALOG = 101;
	public final int INT_HIDE_DIALOG_CONTEXT = 1011; // 内容数据获取 赋值
	public final int INT_SHOW_DIALOG = 100;
	public final int INT_ERROR_TIME = 404;// 超时
	public final int INT_FIRST_SUBMIT = 1; // 第一次接口请求
	public final int INT_SECOND_SUBMIT = 2; // 第二个
	public final int INT_GET_INFO = 11; // 得到顶部数据 设置adpter
	public final int INT_CONTENT_NULL = 403;// 数据为空

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepwd);
		password = get_sharePreferences_rongpas();

		init();
	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case INT_SHOW_DIALOG:
				showLoadingDialog("正在修改密码");
				break;
			case INT_HIDE_DIALOG:
                dismissLoadingDialog();
				break;
			case INT_FIRST_SUBMIT:
                post_Password(password,qr_pwd.getText().toString());
				break;
			case INT_ERROR_TIME:
                dismissLoadingDialog();

				break;
			}
		};
	};

	public void init() {
		back = (LinearLayout) findViewById(R.id.line_back);
		title = (TextView) findViewById(R.id.title);
		old_pwd = (EditText) findViewById(R.id.old_pwd);
		new_pwd = (EditText) findViewById(R.id.new_pwd);
		qr_pwd = (EditText) findViewById(R.id.qr_pwd);
		ok = (RelativeLayout) findViewById(R.id.ok);
		ok.setOnClickListener(this);
		title.setText("修改密码");
		back.setOnClickListener(this);
		old_pwd.addTextChangedListener(new PasswordTextWatcher(old_pwd) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com. .yanbb.testandroid.
			 * PasswordTextWatcher#afterTextChanged(android.text.Editable)
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				super.afterTextChanged(s);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com. .yanbb.testandroid.
			 * PasswordTextWatcher#beforeTextChanged(java.lang.CharSequence,
			 * int, int, int)
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				super.beforeTextChanged(s, start, count, after);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com. .yanbb.testandroid.
			 * PasswordTextWatcher#onTextChanged(java.lang.CharSequence, int,
			 * int, int)
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				super.onTextChanged(s, start, before, count);
			}
		});
		new_pwd.addTextChangedListener(new PasswordTextWatcher(new_pwd) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com. .yanbb.testandroid.
			 * PasswordTextWatcher#afterTextChanged(android.text.Editable)
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				super.afterTextChanged(s);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com. .yanbb.testandroid.
			 * PasswordTextWatcher#beforeTextChanged(java.lang.CharSequence,
			 * int, int, int)
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				super.beforeTextChanged(s, start, count, after);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com. .yanbb.testandroid.
			 * PasswordTextWatcher#onTextChanged(java.lang.CharSequence, int,
			 * int, int)
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				super.onTextChanged(s, start, before, count);
			}
		});
		qr_pwd.addTextChangedListener(new PasswordTextWatcher(qr_pwd) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com. .yanbb.testandroid.
			 * PasswordTextWatcher#afterTextChanged(android.text.Editable)
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				super.afterTextChanged(s);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com. .yanbb.testandroid.
			 * PasswordTextWatcher#beforeTextChanged(java.lang.CharSequence,
			 * int, int, int)
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				super.beforeTextChanged(s, start, count, after);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com. .yanbb.testandroid.
			 * PasswordTextWatcher#onTextChanged(java.lang.CharSequence, int,
			 * int, int)
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				super.onTextChanged(s, start, before, count);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			finish();

			break;
		case R.id.ok:
			if (old_pwd.getText().toString().equals("")
					|| old_pwd.getText().toString().length() == 0) {
                showNotifyDialog("旧密码不能为空");

			} else if (new_pwd.getText().toString().equals("")
					|| new_pwd.getText().toString().length() == 0) {
                showNotifyDialog("新密码不能为空");

			} else if (new_pwd.getText().toString().length() < 8) {
				showNotifyDialog("密码长度至少8位");

			} else if (RegularUtils.isAlpha(new_pwd.getText().toString()) == true) {
				showNotifyDialog("密码不能纯字母");

			} else if (RegularUtils.isNumeric(new_pwd.getText().toString()) == true) {
				showNotifyDialog("密码不能纯数字");

			} else if (RegularUtils.havefuhao(new_pwd.getText().toString()) == false) {
				showNotifyDialog("密码不能纯符号");

			} else if (qr_pwd.getText().toString().equals("")
					|| qr_pwd.getText().toString().length() == 0) {
				showNotifyDialog("确认密码不能为空");

			} else if (!qr_pwd.getText().toString()
					.equals(new_pwd.getText().toString())) {
				showNotifyDialog("两次填写的密码不一样");

			} else if (!password.equals(old_pwd.getText().toString())) {
                showNotifyDialog("旧密码错误,请重新填写");
				old_pwd.setText(null);

			} else if (password.equals(qr_pwd.getText().toString())) {

                showNotifyDialog("修改密码不能为旧密码");

			} else {
				handler.sendEmptyMessage(INT_SHOW_DIALOG);
				handler.sendEmptyMessageDelayed(INT_FIRST_SUBMIT, 1000);
			}
			break;

		default:
			break;
		}
	}

	String result_string = "";
    public void post_Password(String password,String newPassword) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("password",password);
        params.addBodyParameter("newPassword",newPassword);
         String token=get_sharePreferences_UserToken();
        params.addBodyParameter("token",token);
        HttpUtils httpUtils = new HttpUtils();
        String Posturl=Contact.rong_ip+Contact.rong_change_pwd;
        MyLog.i("Posturl"+Posturl);
        httpUtils.send(HttpRequest.HttpMethod.POST, Posturl, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                result_string=arg1.toString();
                handler.sendEmptyMessage(INT_ERROR_TIME);
                showNotifyDialog("密码修改失败!");

            }
            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                String str = arg0.result;
                MyLog.i("上传成功：" + str);
                MsgInfo mUserInfo= JsonUtils.getModel(str, MsgInfo.class);
                if (mUserInfo.getCode()==0){
                    Dialog dialog = new AlertDialog.Builder(mcontext).setMessage("密码修改成功!")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }

                            }).create();
                    dialog.show();
                }else{
                    Dialog dialog = new AlertDialog.Builder(mcontext).setMessage(""+mUserInfo.getMsg())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }

                            }).create();
                    dialog.show();
                }
                handler.sendEmptyMessage(INT_HIDE_DIALOG);

//                loginAgain();
            }
        });
    }


	@Override
	public void initPages() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub

	}

}
