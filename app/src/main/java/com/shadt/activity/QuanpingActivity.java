package com.shadt.activity;

import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.shadt.bean.txtandpicBean;
import com.shadt.caibian_news.R;
import com.shadt.util.xBase64;

public class QuanpingActivity extends BaseActivity {
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
	int length = 0;
	private AlertDialog myDialog;
	int type = 101;
	boolean enable = true;
	ScrollView scor;
	boolean tuwen = false;
	boolean addnext = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quanping);

		Intent it = getIntent();
		context = it.getStringExtra("context");
		my_position = it.getIntExtra("position", 0);
		my_title = it.getStringExtra("title");
		length = it.getIntExtra("length", 0);
		type = it.getIntExtra("type", 101);
		enable = it.getBooleanExtra("enable", false); // 表示 能否编辑 。
		tuwen = it.getBooleanExtra("tuwen", false);// 表示 编辑的 图文
		addnext = it.getBooleanExtra("addnext", false); // 表示 编辑的 是 下一个栏目的 。
		initPages();
	}

	@Override
	public void initPages() {
		// TODO Auto-generated method stub
		initTop();
		initView();
	}

	public void initTop() {
		cancel = (TextView) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setText("" + my_title);
		ok = (TextView) findViewById(R.id.edit_ok);
		ok.setOnClickListener(this);
	}

	public void initView() {
		edit = (EditText) findViewById(R.id.edit_context);
		txt_context = (TextView) findViewById(R.id.txt_context);
		scor = (ScrollView) findViewById(R.id.scor);
		if (type == 0) {
			edit.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		if (enable == false) {
			Log.v("ceshi", "不能编辑");
			edit.setEnabled(false);
			ok.setVisibility(View.GONE);
			cancel.setVisibility(View.GONE);
			edit.setVisibility(View.GONE);
			txt_context.setText(context);
		} else {
			Log.v("ceshi", "能编辑");
			scor.setVisibility(View.GONE);
			if (addnext==true) {
				
			}else{
				edit.setText(context);
			}
			try {
				edit.setHint("不超过" + length + "字");
				edit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						length) });
				// edit.setSelection(context.length());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void initpop() {
		// TODO Auto-generated method stub

		myDialog = new AlertDialog.Builder(QuanpingActivity.this).create();
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
						try {
							if (tuwen == true) {
								if (addnext != true) {
									AddActivity2.tuwenList.get(my_position).setTxt(
											edit.getText().toString());
								} else {
									txtandpicBean bean = new txtandpicBean();
									bean.setTxt(edit.getText().toString());
									AddActivity2.tuwenList.add(my_position + 1, bean);
								}

							} else {
								AddNewsActivity.channel_list.get(my_position)
										.setFieldcontext(
												edit.getText().toString());
							}
						} catch (Exception e) {
							// TODO: handle exception
							MyNewsDetailActivity.channel_list.get(my_position)
									.setFieldcontext(edit.getText().toString());
						}
						finish();
					}
				});
	}

	public void cancel() {
		if (context != null) {
			if (context.equals(edit.getText().toString())) {
				finish();
			} else {
				if (edit.getText().toString().length() == 0) {
					Toast.makeText(QuanpingActivity.this, "内容不能为空", 0).show();
				} else {
					initpop();
				}
			}
		} else {
			if (edit.getText().toString().length() != 0) {
				initpop();
			} else {
				finish();
			}
		}
	}

	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel:

			if (enable == false) {
				Log.v("ceshi", "能返回");
				finish();
			} else {
				Log.v("ceshi", "不能返回");
				cancel();
			}
			break;
		case R.id.edit_ok:
			try {
				if (tuwen == true) {
					if (addnext != true) {
						AddActivity2.add=true;
						AddActivity2.tuwenList.get(my_position).setTxt(
								edit.getText().toString());
					} else {
						txtandpicBean bean = new txtandpicBean();
						bean.setTxt(edit.getText().toString());
						AddActivity2.tuwenList.add(my_position + 1, bean);
					}

				} else {
					if (AddNewsActivity.channel_list.get(my_position).getFieldtype().equals("5")) {
						AddNewsActivity.channel_list.get(my_position)
						.setFieldcontext(xBase64.getBase64(edit.getText().toString()));
					}else{
						AddNewsActivity.channel_list.get(my_position)
						.setFieldcontext(edit.getText().toString());
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				if (MyNewsDetailActivity.channel_list.get(my_position).getFieldtype().equals("5")) {
					MyNewsDetailActivity.channel_list.get(my_position)
					.setFieldcontext(xBase64.getBase64(edit.getText().toString()));
				}else{
					MyNewsDetailActivity.channel_list.get(my_position)
					.setFieldcontext(edit.getText().toString());
				}
				
			}
			finish();
			break;
		case R.id.txt_save_no:
			break;
		case R.id.txt_save:

			break;
		default:
			break;
		}
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

}
