package com.shadt.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shadt.ui.widget.SelectableRoundedImageView;
import com.shadt.db.DBManager;
import com.shadt.db.Friend;
import com.shadt.db.FriendDao;
import com.shadt.db.GroupMember;
import com.shadt.db.GroupMemberDao;
import com.shadt.model.SealSearchConversationResult;
import com.shadt.model.SearchResult;
import com.shadt.caibian_news.R;
import com.shadt.util.CharacterParser;
import com.shadt.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import de.greenrobot.dao.query.QueryBuilder;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.SearchConversationResult;

/**
 * Created by tiankui on 16/8/31.
 */
public class SealSearchActivity extends Activity {
    private static final int SEARCH_TYPE_FLAG = 1;

    private EditText mSearchEditText;
    private LinearLayout mFriendListLinearLayout;
    private ListView mFriendListView;
    private LinearLayout mMoreFriendLinearLayout;
    private LinearLayout mGroupListLinearLayout;
    private ListView mGroupsListView;
    private LinearLayout mMoreGroupsLinearLayout;
    private TextView mSearchNoResultsTextView;
    private ImageView mPressBackImageView;
    private LinearLayout mChattingRecordsLinearLayout;
    private LinearLayout mMoreChattingRecordsLinearLayout;
    private ListView mChattingRecordsListView;

    private CharacterParser mCharacterParser;
    private ArrayList<Friend> mFilterFriendList;
    private String mFilterString;
    private AsyncTask mAsyncTask;
    private ThreadPoolExecutor mExecutor;

    private static final String SQL_DISTINCT_GROUP_ID = "SELECT DISTINCT " + GroupMemberDao.Properties.GroupId.columnName + " FROM " + GroupMemberDao.TABLENAME;

    private ArrayList<String> mFilterGroupId;
    private List<SearchConversationResult> mSearchConversationResultsList;
    private ArrayList<SearchConversationResult> mSearchConversationResultsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_search);

        initView();
        initData();
    }

    private void initView() {
        mSearchEditText = (EditText) findViewById(R.id.ac_et_search);
        mMoreChattingRecordsLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_more_chatting_records);
        mChattingRecordsLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_filtered_chatting_records_list);
        mChattingRecordsListView = (ListView) findViewById(R.id.ac_lv_filtered_chatting_records_list);
        mSearchNoResultsTextView = (TextView) findViewById(R.id.ac_tv_search_no_results);
        mPressBackImageView = (ImageView) findViewById(R.id.ac_iv_press_back);
        mChattingRecordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object selectObj = parent.getItemAtPosition(position);
                if (selectObj instanceof SealSearchConversationResult) {
                    SealSearchConversationResult result = (SealSearchConversationResult) selectObj;
                    int count = result.getMatchCount();
                    Conversation conversation = result.getConversation();
                    if (count == 1) {
                        RongIM.getInstance().startConversation(SealSearchActivity.this, conversation.getConversationType(), conversation.getTargetId(), result.getTitle(), result.getConversation().getSentTime());
                    } else {
                        Intent intent = new Intent(SealSearchActivity.this, SealSearchChattingDetailActivity.class);
                        intent.putExtra("filterString", mFilterString);
                        intent.putExtra("searchConversationResult", result);
                        intent.putExtra("flag", SEARCH_TYPE_FLAG);
                        startActivity(intent);
                    }
                }
            }
        });
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFilterFriendList = new ArrayList<>();
                mFilterString = s.toString();
                mFilterGroupId = new ArrayList<>();
                mSearchConversationResultsList = new ArrayList<>();
                RongIMClient.getInstance().searchConversations(mFilterString,
                        new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP},
                        new String[]{"RC:TxtMsg", "RC:ImgTextMsg", "RC:FileMsg"}, new RongIMClient.ResultCallback<List<SearchConversationResult>>() {
                            @Override
                            public void onSuccess(List<SearchConversationResult> searchConversationResults) {
                                Log.v("wangben", "searchConversationResults" + searchConversationResults.size());
                                mSearchConversationResultsList = searchConversationResults;
                                mSearchConversationResultsArrayList = new ArrayList<>();
                                for (SearchConversationResult result : searchConversationResults) {
                                    mSearchConversationResultsArrayList.add(result);
                                }
                                if (searchConversationResults.size() > 0) {
                                    mChattingRecordsLinearLayout.setVisibility(View.VISIBLE);
                                    if (searchConversationResults.size() > 3) {
                                        mMoreChattingRecordsLinearLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        mMoreChattingRecordsLinearLayout.setVisibility(View.GONE);
                                    }
                                } else {
                                    mChattingRecordsLinearLayout.setVisibility(View.GONE);
                                }
                                if (mFilterString.equals("")) {
                                    mChattingRecordsLinearLayout.setVisibility(View.GONE);
                                    mMoreChattingRecordsLinearLayout.setVisibility(View.GONE);
                                }
                                if (mFilterFriendList.size() == 0 && mFilterGroupId.size() == 0 && mSearchConversationResultsList.size() == 0) {
                                    if (mFilterString.equals("")) {
                                        mSearchNoResultsTextView.setVisibility(View.GONE);
                                    } else {
                                        mSearchNoResultsTextView.setVisibility(View.VISIBLE);
                                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                        spannableStringBuilder.append(getResources().getString(R.string.ac_search_no_result_pre));
                                        SpannableStringBuilder colorFilterStr = new SpannableStringBuilder(mFilterString);
                                        colorFilterStr.setSpan(new ForegroundColorSpan(Color.parseColor("#0099ff")), 0, mFilterString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                        spannableStringBuilder.append(colorFilterStr);
                                        spannableStringBuilder.append(getResources().getString(R.string.ac_search_no_result_suffix));
                                        mSearchNoResultsTextView.setText(spannableStringBuilder);
                                    }
                                } else {
                                    mSearchNoResultsTextView.setVisibility(View.GONE);
                                }
                                ChattingRecordsAdapter chattingRecordsAdapter = new ChattingRecordsAdapter(mSearchConversationResultsList);
                                mChattingRecordsListView.setAdapter(chattingRecordsAdapter);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {
                                Log.v("wangben", "s失败");
                                if (mFilterString.equals("")) {
                                    mChattingRecordsLinearLayout.setVisibility(View.GONE);
                                    mMoreChattingRecordsLinearLayout.setVisibility(View.GONE);
                                }
                                if (mFilterFriendList.size() == 0 && mFilterGroupId.size() == 0 && mSearchConversationResultsList.size() == 0) {
                                    Log.v("wangben", "heheheh");
                                    if (mFilterString.equals("")) {
                                        Log.v("wangben", "heheheh1111111111111111111");

                                        mSearchNoResultsTextView.setVisibility(View.GONE);
                                    } else {
                                        Log.v("wangben", "hehehehe22222222222222222222");

                                        mSearchNoResultsTextView.setVisibility(View.VISIBLE);
                                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                        spannableStringBuilder.append(getResources().getString(R.string.ac_search_no_result_pre));
                                        SpannableStringBuilder colorFilterStr = new SpannableStringBuilder(mFilterString);
                                        colorFilterStr.setSpan(new ForegroundColorSpan(Color.parseColor("#0099ff")), 0, mFilterString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                        spannableStringBuilder.append(colorFilterStr);
                                        spannableStringBuilder.append(getResources().getString(R.string.ac_search_no_result_suffix));
                                        mSearchNoResultsTextView.setText(spannableStringBuilder);
                                    }
                                } else {
                                    Log.v("wangben", "hehehehe22223333333333333333333333");
                                    mSearchNoResultsTextView.setVisibility(View.GONE);
                                }

                            }


                        });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                    filterInfo(String.valueOf(mSearchEditText.getText()));
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
                        filterInfo("");
                        mSearchEditText.setText("");
                        mSearchEditText.clearFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        mPressBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SealSearchActivity.this.finish();
            }
        });
    }


    private synchronized SearchResult filterInfo(String filterStr) {

        List<Friend> filterFriendList = new ArrayList<>();
        List<String> filterGroupId = new ArrayList<>();
        Map<String, List<GroupMember>> filterGroupNameListMap = new HashMap<>();
        Map<String, List<GroupMember>> filterGroupMemberNameListMap = new HashMap<>();
        SearchResult searchResult = new SearchResult();

        if (filterStr.equals("")) {
            SearchResult result = new SearchResult();
            result.setFilterStr("");
            result.setFilterFriendList(filterFriendList);
            result.setFilterGroupId(filterGroupId);
            result.setFilterGroupNameListMap(filterGroupNameListMap);
            result.setFilterGroupNameListMap(filterGroupMemberNameListMap);
            return result;
        }
        if (filterStr.contains("'")) {
            SearchResult result = new SearchResult();
            result.setFilterStr(filterStr);
            result.setFilterFriendList(filterFriendList);
            result.setFilterGroupId(filterGroupId);
            result.setFilterGroupNameListMap(filterGroupNameListMap);
            result.setFilterGroupNameListMap(filterGroupMemberNameListMap);
            return result;
        }
        /**
         * 从数据库里边查询符合条件的数据
         */

        QueryBuilder queryBuilder = DBManager.getInstance().getDaoSession().getFriendDao().queryBuilder();
        filterFriendList = queryBuilder.where(queryBuilder.or(FriendDao.Properties.Name.like("%" + filterStr + "%"),
                FriendDao.Properties.DisplayName.like("%" + filterStr + "%"),
                FriendDao.Properties.NameSpelling.like(filterStr + "%"),
                FriendDao.Properties.DisplayNameSpelling.like(filterStr + "%"))).orderAsc(FriendDao.Properties.DisplayNameSpelling, FriendDao.Properties.NameSpelling).build().list();

        Cursor cursor = DBManager.getInstance().getDaoSession().getDatabase().rawQuery(SQL_DISTINCT_GROUP_ID
                + " WHERE " + GroupMemberDao.Properties.GroupName.columnName + " LIKE " + "'" + "%" + filterStr + "%" + "'" + " or "
                + GroupMemberDao.Properties.GroupNameSpelling.columnName + " like " + "'" + filterStr + "%" + "'" + " or "
                + GroupMemberDao.Properties.Name.columnName + " like " + "'" + "%" + filterStr + "%" + "'" + " or "
                + GroupMemberDao.Properties.NameSpelling.columnName + " like " + "'" + filterStr + "%" + "'" + " or "
                + GroupMemberDao.Properties.DisplayName.columnName + " like " + "'" + "%" + filterStr + "%" + "'" + " or "
                + GroupMemberDao.Properties.DisplayNameSpelling.columnName + " like " + "'" + filterStr + "%" + "'", null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    filterGroupId.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        for (String groupId : filterGroupId) {
            QueryBuilder groupNameQueryBuilder = DBManager.getInstance().getDaoSession().getGroupMemberDao().queryBuilder();
            List<GroupMember> filterGroupNameList = groupNameQueryBuilder.where(GroupMemberDao.Properties.GroupId.eq(groupId),
                    groupNameQueryBuilder.or(GroupMemberDao.Properties.GroupName.like("%" + filterStr + "%"),
                            GroupMemberDao.Properties.GroupNameSpelling.like(filterStr + "%"))).orderAsc(GroupMemberDao.Properties.GroupNameSpelling).build().list();
            QueryBuilder groupMemberNameQueryBuilder = DBManager.getInstance().getDaoSession().getGroupMemberDao().queryBuilder();
            List<GroupMember> filterGroupMemberNameList = groupMemberNameQueryBuilder.where(GroupMemberDao.Properties.GroupId.eq(groupId),
                    groupMemberNameQueryBuilder.or(GroupMemberDao.Properties.Name.like("%" + filterStr + "%"),
                            GroupMemberDao.Properties.NameSpelling.like(filterStr + "%"),
                            GroupMemberDao.Properties.DisplayName.like("%" + filterStr + "%"),
                            GroupMemberDao.Properties.DisplayNameSpelling.like(filterStr + "%"))
            ).orderAsc(GroupMemberDao.Properties.NameSpelling, GroupMemberDao.Properties.DisplayNameSpelling).build().list();
            if (filterGroupNameList.size() != 0) {
                filterGroupNameListMap.put(groupId, filterGroupNameList);
            } else {
                filterGroupNameListMap.put(groupId, null);
            }
            if (filterGroupMemberNameList.size() != 0) {
                filterGroupMemberNameListMap.put(groupId, filterGroupMemberNameList);
            } else {
                filterGroupMemberNameListMap.put(groupId, null);
            }
        }
        searchResult.setFilterStr(filterStr);
        searchResult.setFilterFriendList(filterFriendList);
        searchResult.setFilterGroupId(filterGroupId);
        searchResult.setFilterGroupNameListMap(filterGroupNameListMap);
        searchResult.setFilterGroupMemberNameListMap(filterGroupMemberNameListMap);
        return searchResult;
    }





//        mPressBackImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SealSearchActivity.this.finish();
//            }
//        });

    private class ChattingRecordsAdapter extends BaseAdapter {

        private List<SealSearchConversationResult> searchConversationResults;

        public ChattingRecordsAdapter(List<SearchConversationResult> searchConversationResults) {
            this.searchConversationResults = CommonUtils.convertSearchResult(searchConversationResults);
        }

        @Override
        public int getCount() {
            if (searchConversationResults != null) {
                return searchConversationResults.size() > 3 ? 3 : searchConversationResults.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (searchConversationResults == null)
                return null;

            if (position >= searchConversationResults.size())
                return null;

            return searchConversationResults.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ChattingRecordsViewHolder viewHolder;
            final SealSearchConversationResult searchResult = (SealSearchConversationResult) getItem(position);
            final Conversation conversation = searchResult.getConversation();
            int searchResultCount = searchResult.getMatchCount();
            if (convertView == null) {
                viewHolder = new ChattingRecordsViewHolder();
                convertView = View.inflate(SealSearchActivity.this, R.layout.item_filter_chatting_records_list, null);
                viewHolder.portraitImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.item_iv_record_image);
                viewHolder.chatDetailLinearLayout = (LinearLayout) convertView.findViewById(R.id.item_ll_chatting_records_detail);
                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.item_tv_chat_name);
                viewHolder.chatRecordsDetailTextView = (TextView) convertView.findViewById(R.id.item_tv_chatting_records_detail);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ChattingRecordsViewHolder) convertView.getTag();
            }
            if (conversation.getConversationType() == Conversation.ConversationType.PRIVATE) {
                try {
                    String currentUserName = conversation.getSenderUserName();

                    Log.v("wangben","currentUserName"+currentUserName);
//                    String portraitUri = conversation.getPortraitUrl();
//                    searchResult.setPortraitUri(portraitUri);
//                    ImageLoader.getInstance().displayImage(portraitUri, viewHolder.portraitImageView, MyApp.getOptions());
                    String currentUserId = conversation.getTargetId();
                    Log.v("wangben","currentUserId"+currentUserId);
                    searchResult.setId(currentUserId);
                    if (!TextUtils.isEmpty(currentUserName)) {
                        searchResult.setTitle(currentUserName);
                        viewHolder.nameTextView.setText(currentUserName);
                    } else {
                        searchResult.setTitle(currentUserId);
                        viewHolder.nameTextView.setText(currentUserId);
                    }
                }catch (Exception e){
                    Log.v("wangben","cuowu");
                }

//                    UserInfo currentUserInfo = new UserInfo("10010", "联通", Uri.parse("http://www.51zxw.net/bbs/UploadFile/2013-4/201341122335711220.jpg"));
//                    String portraitUri = "http://www.51zxw.net/bbs/UploadFile/2013-4/201341122335711220.jpg";
//                    searchResult.setPortraitUri(portraitUri);
//                    ImageLoader.getInstance().displayImage(portraitUri, viewHolder.portraitImageView, MyApp.getOptions());
            }
         /*   if (conversation.getConversationType() == Conversation.ConversationType.PRIVATE) {
                Log.v("wangben","1111111111111111111111"+conversation.getConversationTitle());
                Friend friend = DBManager.getInstance().getDaoSession().getFriendDao().queryBuilder().where(FriendDao.Properties.UserId.eq(conversation.getTargetId())).unique();
                SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                String currentUserId = sp.getString(SealConst.SEALTALK_LOGIN_ID, "");
                String currentUserName = sp.getString(SealConst.SEALTALK_LOGIN_NAME, "");
                String currentUserPortrait = sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, "");
                if (friend != null) {
                    Log.v("wangben","1111111111111111111111");
                    searchResult.setId(friend.getUserId());
                    String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(friend);
                    searchResult.setPortraitUri(portraitUri);
                    ImageLoader.getInstance().displayImage(portraitUri, viewHolder.portraitImageView, MyApp.getOptions());
                    if (!TextUtils.isEmpty(friend.getDisplayName())) {
                        Log.v("wangben","22222222222222222222222");
                        searchResult.setTitle(friend.getDisplayName());
                        viewHolder.nameTextView.setText(friend.getDisplayName());
                    } else {
                        searchResult.setTitle(friend.getName());
                        viewHolder.nameTextView.setText(friend.getName());
                    }
                } else if (conversation.getTargetId().equals(currentUserId)) {
                    Log.v("wangben","3333333333333333333333333");
                    searchResult.setId(currentUserId);
                    UserInfo currentUserInfo = new UserInfo(currentUserId, currentUserName, Uri.parse(currentUserPortrait));
                    String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(currentUserInfo);
                    searchResult.setPortraitUri(portraitUri);
                    ImageLoader.getInstance().displayImage(portraitUri, viewHolder.portraitImageView, MyApp.getOptions());
                    if (!TextUtils.isEmpty(currentUserName)) {
                        Log.v("wangben","444444444444444444444");
                        searchResult.setTitle(currentUserName);
                        viewHolder.nameTextView.setText(currentUserName);
                    } else {
                        searchResult.setTitle(currentUserId);
                        viewHolder.nameTextView.setText(currentUserId);
                    }
                } else {
                    Log.v("wangben","5555555555555555555555");
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(conversation.getTargetId());
                    String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(userInfo);
                    searchResult.setPortraitUri(portraitUri);
                    ImageLoader.getInstance().displayImage(portraitUri, viewHolder.portraitImageView, MyApp.getOptions());
                    searchResult.setId(conversation.getTargetId());
                    if (userInfo != null) {

                        if (!TextUtils.isEmpty(userInfo.getName())) {

                            searchResult.setTitle(userInfo.getName());
                            viewHolder.nameTextView.setText(userInfo.getName());
                        } else {
                            searchResult.setTitle(userInfo.getUserId());
                            viewHolder.nameTextView.setText(userInfo.getUserId());
                        }
                    } else {
                        searchResult.setId(conversation.getTargetId());
                        searchResult.setTitle(conversation.getTargetId());
                        viewHolder.nameTextView.setText(conversation.getTargetId());
                    }

                }

            }*/
            if (searchResultCount == 1) {
                viewHolder.chatRecordsDetailTextView.setText(mCharacterParser.getColoredChattingRecord(mFilterString, searchResult.getConversation().getLatestMessage()));
            } else {
                viewHolder.chatRecordsDetailTextView.setText(getResources().getString(R.string.search_item_chat_records, searchResultCount));
            }
            return convertView;
        }
    }

    class ChattingRecordsViewHolder {
        SelectableRoundedImageView portraitImageView;
        LinearLayout chatDetailLinearLayout;
        TextView nameTextView;
        TextView chatRecordsDetailTextView;
    }
    private void initData() {
        mExecutor = new ThreadPoolExecutor(3, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mCharacterParser = CharacterParser.getInstance();
    }



    @Override
    protected void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
        super.onDestroy();
    }
}
