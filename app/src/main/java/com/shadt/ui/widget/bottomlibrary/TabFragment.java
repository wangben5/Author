package com.shadt.ui.widget.bottomlibrary;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shadt.ui.fragment.BaseFragment;

/**
 * @author ChayChan
 * @date 2017/6/23  11:22
 */
public class TabFragment extends BaseFragment {

    public static final String CONTENT = "content";
    private TextView mTextView;


    @Override
    protected void lazyLoad() {
        Log.i("shadt","11111");
    }
    String content;

    @Override
    public View initView(LayoutInflater inflater) {
        mTextView = new TextView(getActivity());
        mTextView.setGravity(Gravity.CENTER);
        content  = getArguments().getString(CONTENT);
        mTextView.setText(content);
        return mTextView;

    }

    @Override
    protected void initFindViewById(View view) {

    }

    @Override
    public void initData() {

    }


}
