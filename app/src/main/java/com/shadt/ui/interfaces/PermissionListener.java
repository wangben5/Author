package com.shadt.ui.interfaces;

import java.util.List;

/**
 * Created by sunyafei on 2018/5/28.
 */

public interface PermissionListener {
    //授权成功
    void onGranted();

    //授权部分
    void onGranted(List<String> grantedPermission);

    //拒绝授权
    void onDenied(List<String> deniedPermission);
}
