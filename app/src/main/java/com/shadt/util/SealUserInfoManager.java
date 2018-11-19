package com.shadt.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;


import com.shadt.db.UserInfoBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;


import io.rong.common.RLog;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

/**
 * Created by wangmingqiang on 16/9/10.
 * Company RongCloud
 * 数据库访问接口,目前接口有同步和异步之分
 * 第一次login时从app server获取数据,之后数据库读,数据的更新使用IMKit里的通知类消息
 * 因为存在app server获取数据失败的情况,代码里有很多这种异常情况的判断处理并重新从app server获取数据
 * 1.add...类接口为插入或更新数据库
 * 2.get...类接口为读取数据库
 * 3.delete...类接口为删除数据库数据
 * 4.sync...为同步接口,因为存在去掉sync相同名称的异步接口,有些同步类接口不带sync
 * 5.fetch...,pull...类接口都是从app server获取数据并存数据库,不同的只是返回值不一样,此类接口全部为private
 */
public class SealUserInfoManager  {

    private final static String TAG = "SealUserInfoManager";
    private static final int GET_TOKEN = 800;

    /**
     * 用户信息全部未同步
     */
    private static final int NONE = 0;//00000
    /**
     * 好友信息同步成功
     */
    private static final int FRIEND = 1;//00001
    /**
     * 群组信息同步成功
     */
    private static final int GROUPS = 2;//00010
    /**
     * 群成员信息部分同步成功,n个群组n次访问网络,存在部分同步的情况
     */
    private static final int PARTGROUPMEMBERS = 4;//00100
    /**
     * 群成员信息同步成功
     */
    private static final int GROUPMEMBERS = 8;//01000
    /**
     * 黑名单信息同步成功
     */
    private static final int BLACKLIST = 16;//10000
    /**
     * 用户信息全部同步成功
     */
    private static final int ALL = 27;//11011

    private static SealUserInfoManager sInstance;
    private final Context mContext;

    private Handler mWorkHandler;
    private HandlerThread mWorkThread;
    static Handler mHandler;
    private SharedPreferences sp;
     private int mGetAllUserInfoState;
    private boolean doingGetAllUserInfo = false;

    private LinkedHashMap<String, UserInfo> mUserInfoCache;

    public static SealUserInfoManager getInstance() {
        return sInstance;
    }

    public SealUserInfoManager(Context context) {
        mContext = context;

        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);


    }

    public static void init(Context context) {
        RLog.d(TAG, "SealUserInfoManager init");
        sInstance = new SealUserInfoManager(context);
    }

    /**
     * 修改数据库的存贮路径为.../appkey/userID/,
     * 必须确保userID存在后才能初始化DBManager
     */



    /**
     * app中获取用户头像的接口,此前这部分调用分散在app显示头像的每处代码中,整理写一个方法使app代码更整洁
     * 这个方法不涉及读数据库,头像空时直接生成默认头像
     */
    public String getPortraitUri(UserInfo userInfo) {
        if (userInfo != null) {
            if (userInfo.getPortraitUri() != null) {
                if (TextUtils.isEmpty(userInfo.getPortraitUri().toString())) {
                    if (userInfo.getName() != null) {
                        return RongGenerate.generateDefaultAvatar(userInfo);
                    } else {
                        return null;
                    }
                } else {
                    return userInfo.getPortraitUri().toString();
                }
            } else {
                if (userInfo.getName() != null) {
                    return RongGenerate.generateDefaultAvatar(userInfo);
                } else {
                    return null;
                }
            }

        }
        return null;
    }

    public String getPortraitUri(UserInfoBean bean) {
        if (bean != null) {
            if (bean.getPortraitUri() != null) {
                if (TextUtils.isEmpty(bean.getPortraitUri().toString())) {
                    if (bean.getName() != null) {
                        return RongGenerate.generateDefaultAvatar(bean.getName(), bean.getUserId());
                    } else {
                        return null;
                    }
                } else {
                    return bean.getPortraitUri().toString();
                }
            } else {
                if (bean.getName() != null) {
                    return RongGenerate.generateDefaultAvatar(bean.getName(), bean.getUserId());
                } else {
                    return null;
                }
            }

        }
        return null;
    }
//
//    public String getPortraitUri(GetGroupInfoResponse groupInfoResponse) {
//        if (groupInfoResponse.getResult() != null) {
//            Groups groups = new Groups(groupInfoResponse.getResult().getId(),
//                    groupInfoResponse.getResult().getName(),
//                    groupInfoResponse.getResult().getPortraitUri());
//            return getPortraitUri(groups);
//        }
//        return null;
//    }
//
//    /**
//     * 获取用户头像,头像为空时会生成默认的头像,此默认头像可能已经存在数据库中,不重新生成
//     * 先从缓存读,再从数据库读
//     */
//    private String getPortrait(Friend friend) {
//        if (friend != null) {
//            if (friend.getPortraitUri() == null || TextUtils.isEmpty(friend.getPortraitUri().toString())) {
//                if (TextUtils.isEmpty(friend.getUserId())) {
//                    return null;
//                } else {
//                    UserInfo userInfo = mUserInfoCache.get(friend.getUserId());
//                    if (userInfo != null) {
//                        if (userInfo.getPortraitUri() != null && !TextUtils.isEmpty(userInfo.getPortraitUri().toString())) {
//                            return userInfo.getPortraitUri().toString();
//                        } else {
//                            mUserInfoCache.remove(friend.getUserId());
//                        }
//                    }
//                    List<GroupMember> groupMemberList = getGroupMembersWithUserId(friend.getUserId());
//                    if (groupMemberList != null && groupMemberList.size() > 0) {
//                        GroupMember groupMember = groupMemberList.get(0);
//                        if (groupMember.getPortraitUri() != null && !TextUtils.isEmpty(groupMember.getPortraitUri().toString()))
//                            return groupMember.getPortraitUri().toString();
//                    }
//                    String portrait = RongGenerate.generateDefaultAvatar(friend.getName(), friend.getUserId());
//                    //缓存信息kit会使用,备注名存在时需要缓存displayName
//                    String name = friend.getName();
//                    if (friend.isExitsDisplayName()) {
//                        name = friend.getDisplayName();
//                    }
//                    userInfo = new UserInfo(friend.getUserId(), name, Uri.parse(portrait));
//                    mUserInfoCache.put(friend.getUserId(), userInfo);
//                    return portrait;
//                }
//            } else {
//                return friend.getPortraitUri().toString();
//            }
//        }
//        return null;
//    }
//
//    private Uri getPortrait(GroupMember groupMember) {
//        if (groupMember != null) {
//            if (groupMember.getPortraitUri() == null || TextUtils.isEmpty(groupMember.getPortraitUri().toString())) {
//                if (TextUtils.isEmpty(groupMember.getUserId())) {
//                    return null;
//                } else {
//                    UserInfo userInfo = mUserInfoCache.get(groupMember.getUserId());
//                    if (userInfo != null) {
//                        if (userInfo.getPortraitUri() != null && !TextUtils.isEmpty(userInfo.getPortraitUri().toString())) {
//                            return userInfo.getPortraitUri();
//                        } else {
//                            mUserInfoCache.remove(groupMember.getUserId());
//                        }
//                    }
//                    Friend friend = getFriendByID(groupMember.getUserId());
//                    if (friend != null) {
//                        if (friend.getPortraitUri() != null && !TextUtils.isEmpty(friend.getPortraitUri().toString())) {
//                            return friend.getPortraitUri();
//                        }
//                    }
//                    List<GroupMember> groupMemberList = getGroupMembersWithUserId(groupMember.getUserId());
//                    if (groupMemberList != null && groupMemberList.size() > 0) {
//                        GroupMember member = groupMemberList.get(0);
//                        if (member.getPortraitUri() != null && !TextUtils.isEmpty(member.getPortraitUri().toString())) {
//                            return member.getPortraitUri();
//                        }
//                    }
//                    String portrait = RongGenerate.generateDefaultAvatar(groupMember.getName(), groupMember.getUserId());
//                    if (!TextUtils.isEmpty(portrait)) {
//                        userInfo = new UserInfo(groupMember.getUserId(), groupMember.getName(), Uri.parse(portrait));
//                        mUserInfoCache.put(groupMember.getUserId(), userInfo);
//                        return Uri.parse(portrait);
//                    } else {
//                        return null;
//                    }
//                }
//            } else {
//                return groupMember.getPortraitUri();
//            }
//        }
//        return null;
//    }
}
