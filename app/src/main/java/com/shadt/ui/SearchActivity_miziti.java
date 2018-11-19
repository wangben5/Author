package com.shadt.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.shadt.caibian_news.R;
import com.shadt.ui.adapater.NewListFragmentAdapter2;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.MediaInfo;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.ui.utils.SpaceItemDecoration;
import com.shadt.ui.widget.entity.Leixing;
import com.shadt.ui.widget.picker.DatePicker;
import com.shadt.ui.widget.picker.SinglePicker;
import com.shadt.ui.widget.supertext.SuperTextView;
import com.shadt.ui.widget.util.ConvertUtils;
import com.shadt.util.MyLog;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class SearchActivity_miziti extends BaseActivity {
    SuperTextView text_begin_time;
    SuperTextView text_finish_time;
    SuperTextView text_fenlei;
    RecyclerView mRecyclerView;
    SuperTextView text_search;
    SuperTextView text_chongzhi;
    RelativeLayout rela_erro;
    TextView text_erro;
    private EditText mSearchEditText;
    ImageView ac_iv_press_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach_meizi);
        initPages();
    }

    @Override
    public void initPages() {
        ac_iv_press_back = findViewById(R.id.ac_iv_press_back);
        text_begin_time = findViewById(R.id.text_begin_time);
        text_finish_time = findViewById(R.id.text_finish_time);
        text_fenlei = findViewById(R.id.text_leixin);

        mRecyclerView = findViewById(R.id.mRecyclerView);
        text_search = findViewById(R.id.text_search);

        mSearchEditText = (EditText) findViewById(R.id.ac_et_search);
        text_chongzhi = findViewById(R.id.text_chongzhi);
        rela_erro = (RelativeLayout) findViewById(R.id.rela_erro);
        rela_erro.setVisibility(View.GONE);
        text_erro = (TextView) findViewById(R.id.text_erro);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10, 10));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        mRecyclerView.setNestedScrollingEnabled(false);
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        Contact.meizi_ip = preferences.getString("zimeiti_ip", "");
        Contact.meizi_apikey = preferences.getString("zimeiti_key", "");

        text_begin_time.setOnClickListener(this);
        text_finish_time.setOnClickListener(this);
        text_fenlei.setOnClickListener(this);
        text_search.setOnClickListener(this);
        text_chongzhi.setOnClickListener(this);
        ac_iv_press_back.setOnClickListener(this);


        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        mSearchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mSearchEditText.getRight() - 2 * mSearchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mSearchEditText.setText("");
                        mSearchEditText.clearFocus();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    public void timepiker_begin(int myear, int mmonth, int mday) {
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH) + 1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        MyLog.i("系统时间啊" + year + "-" + month + "-" + day + ">" + myear);
        final DatePicker picker = new DatePicker(SearchActivity_miziti.this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(SearchActivity_miziti.this, 10));
        picker.setRangeEnd(year, month, day);
        picker.setRangeStart(2018, 6, 1);
        if (myear != 0) {
            picker.setSelectedItem(myear, mmonth, mday);
        } else {
            picker.setSelectedItem(year, month, day);
        }

        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                MyLog.i(year + "-" + month + "-" + day);
                //开始时间
                if (is_begin_or_finish == true) {
                    begin = year + month + day;
                    text_begin_time.setRightString(year + "-" + month + "-" + day);
                    begin_year = Integer.parseInt(year);
                    begin_month = Integer.parseInt(month);
                    begin_day = Integer.parseInt(day);
                } else {
                    //结束时间
                    finish = year + month + day;
                    text_finish_time.setRightString(year + "-" + month + "-" + day);
                    finish_year = Integer.parseInt(year);
                    finish_month = Integer.parseInt(month);
                    finish_day = Integer.parseInt(day);
                }


            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    int selected = -1;

    public void onSinglePicker(int mselected) {

        List<Leixing> data = new ArrayList<>();
        data.add(new Leixing(1, "全部"));
        data.add(new Leixing(2, "图片"));
        data.add(new Leixing(3, "视频"));
        data.add(new Leixing(4, "音频"));
        data.add(new Leixing(5, "文稿"));
        SinglePicker<Leixing> picker = new SinglePicker<>(this, data);
        picker.setCanceledOnTouchOutside(false);
        if (mselected == -1) {
            picker.setSelectedIndex(0);
        } else {
            picker.setSelectedIndex(mselected);
        }

        picker.setCycleDisable(false);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<Leixing>() {
            @Override
            public void onItemPicked(int index, Leixing item) {
                text_fenlei.setRightString(item.getName());
                selected = index;
            }
        });
        picker.show();
    }

    String mbegin_time, mfinish_time;
    int begin_year = 0, begin_month = 0, begin_day = 0;
    int finish_year = 0, finish_month = 0, finish_day = 0;
    boolean is_begin_or_finish = true;
    String mtype;

    @Override
    public void onClickListener(View v) {

        switch (v.getId()) {
            case R.id.ac_iv_press_back:
                finish();
                break;
            case R.id.text_begin_time:
                is_begin_or_finish = true;
                timepiker_begin(begin_year, begin_month, begin_day);
                break;
            case R.id.text_finish_time:
                is_begin_or_finish = false;
                timepiker_begin(finish_year, finish_month, finish_day);

                break;
            case R.id.text_leixin:
                onSinglePicker(selected);
                break;
            case R.id.text_chongzhi:
                begin_year=0;
                finish_year=0;
                selected=-1;
                text_begin_time.setRightString("请选择");
                 text_finish_time.setRightString("请选择");
                text_fenlei.setRightString("请选择");
                mSearchEditText.setText("");
                break;
            case R.id.text_search:
                if (begin_year == 0) {
                    Toast.makeText(this, "您还没有选择开始时间", 0).show();
                    return;
                } else if (finish_year == 0) {
                    Toast.makeText(this, "您还没有选择结束时间", 0).show();
                    return;
                } else if (finish_year >= begin_year) {
                    if (finish_month >= begin_month) {

                    } else {
                        Toast.makeText(this, "查询时间段错误", 0).show();
                        return;
                    }
                } else {
                    Toast.makeText(this, "查询时间段错误", 0).show();
                    return;
                }
                if (selected == -1) {
                    Toast.makeText(this, "您还没有选择类型", 0).show();
                    return;
                } else if (selected == 0) {
                    mtype = "all";
                } else if (selected == 1) {
                    mtype = "img";
                } else if (selected == 2) {
                    mtype = "vid";
                } else if (selected == 3) {
                    mtype = "aud";
                }else if(selected == 4){
                    mtype = "doc";
                }
                handler.sendEmptyMessage(1);
                LoadSource(Contact.meizi_apikey, mtype);
                break;
        }
    }

    String begin;
    String finish;

    public void LoadSource(String apikey, String show) {


        RequestParams params = new RequestParams();
        params.addBodyParameter("method", "searchassets");
        params.addBodyParameter("api_key", apikey);
        params.addBodyParameter("maxrows", "0");
        params.addBodyParameter("sortby", "dateadd");
        if (!TextUtils.isEmpty(mSearchEditText.getText().toString())){
            params.addBodyParameter("searchfor", "keywords:("+toURLEncoded(mSearchEditText.getText().toString()) + ")");
        }else{
            params.addBodyParameter("searchfor", "*");
        }
        MyLog.i("keywords:("+toURLEncoded(mSearchEditText.getText().toString()) + ")");
        params.addBodyParameter("show", show);
        params.addBodyParameter("datecreaterange", begin + "%20TO%20" + finish);

        HttpUtils httpUtils = new HttpUtils();
        MyLog.i(apikey + "show" + show + "Contact.meizi_ip + Contact.meizi_serch" + Contact.meizi_ip + Contact.meizi_serch+"?method=");
        httpUtils.send(HttpRequest.HttpMethod.POST, Contact.meizi_ip + Contact.meizi_serch, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                MyLog.i("失败");
                mNewListFragmentAdapter=null;
                mRecyclerView.removeAllViews();
                rela_erro.setVisibility(View.VISIBLE);
                text_erro.setText("请求失败");
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                MyLog.i("成功" + arg0.result);
                String str = arg0.result;
                handler.sendEmptyMessage(2);
                setdata(str);
            }
        });
    }
    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {

            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            MyLog.i("str"+str);
            return str;
        }
        catch (Exception localException)
        {

        }

        return "";
    }
    NewListFragmentAdapter2 mNewListFragmentAdapter;
    MediaInfo mMediaInfo;

    public void setdata(String str) {
        mMediaInfo = JsonUtils.getModel(str, MediaInfo.class);//
        MyLog.i("mMediaInfo" + mMediaInfo.getDATA().size());
        try {
            if (mMediaInfo != null) {
                if (mMediaInfo.getDATA() != null) {
                    if (mMediaInfo.getDATA().size() != 0) {
                        if (selected == 0) {
                            for (int i =mMediaInfo.getDATA().size()-1; i >=0 ; i--) {
                                if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(i).get(7))) {
                                    if (mMediaInfo.getDATA().get(i).get(7).equals("doc")) {
                                        if (!mMediaInfo.getDATA().get(i).get(8).equals("txt")) {
                                            mMediaInfo.getDATA().remove(i);
                                        }
                                    }
                                } else {
                                    mMediaInfo.getDATA().remove(i);
                                }
                            }
                        }
                        rela_erro.setVisibility(View.GONE);
                        if (mNewListFragmentAdapter != null) {
                            mNewListFragmentAdapter = null;
                        }
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mNewListFragmentAdapter = new NewListFragmentAdapter2(this, mMediaInfo);
                        mRecyclerView.setAdapter(mNewListFragmentAdapter);
                    } else {
                        mNewListFragmentAdapter=null;
                        mRecyclerView.removeAllViews();
                        mRecyclerView.setVisibility(View.GONE);
                        rela_erro.setVisibility(View.VISIBLE);
                        text_erro.setText("数据为空!");
                    }
                } else {
                    mNewListFragmentAdapter=null;
                    mRecyclerView.removeAllViews();
                    rela_erro.setVisibility(View.VISIBLE);
                    text_erro.setText("数据为空!");

                }
            } else {
                mNewListFragmentAdapter=null;
                mRecyclerView.removeAllViews();
                rela_erro.setVisibility(View.VISIBLE);
                text_erro.setText("数据为空!");

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            mNewListFragmentAdapter=null;
            mRecyclerView.removeAllViews();
            rela_erro.setVisibility(View.VISIBLE);
            text_erro.setText("数据为空!");
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    showLoadingDialog( );
                    break;
                case 2:
                    dismissLoadingDialog();
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
}
