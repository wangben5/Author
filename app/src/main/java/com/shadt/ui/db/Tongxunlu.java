package com.shadt.ui.db;



import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

import indexlib.IndexBar.bean.BaseIndexPinyinBean;

public class Tongxunlu implements Serializable{


    /**
     * msg : success
     * result : [{"userId":1,"username":"admin","email":"root@qq.io","mobile":"13612345678","deptId":1,"deptName":"广电总局","name":"张三","portraituri":null,"ryToken":"FcNa9b4YRGlc8OS+60uHb1RAi/nSXuqXGZZHSdpB50Gm5VoiT77hgihouZGujf1fGFktOwTvRfU=","roles":[{"roleId":2,"roleName":"记者","remark":"11"}]},{"userId":2,"username":"jishubu","email":"1@qq.com","mobile":"13918541458","deptId":6,"deptName":"网络公司","name":"李四","portraituri":null,"ryToken":"4S8KLdWGqeizpTZ/l4mPriizoCNjg41oxSR2jp/e98jxKpYYS5Vx9kckXlnMOl6FoX76+P8Vg/ExP1439F6q1g==","roles":[{"roleId":2,"roleName":"记者","remark":"11"}]},{"userId":3,"username":"root","email":"1@qq.com","mobile":"13918541455","deptId":3,"deptName":"浏阳分公司","name":"王五","portraituri":null,"ryToken":"MEGUS2c0DMffGyBT0L3xUVRAi/nSXuqXGZZHSdpB50EXONBqQ5sjKh5lynVwIy1w3HgzJXrzS+Q=","roles":[{"roleId":1,"roleName":"超级管理员","remark":null}]},{"userId":4,"username":"wangben","email":"111@qq.com","mobile":"12345678910","deptId":1,"deptName":"广电总局","name":null,"portraituri":null,"ryToken":"A46m/cegvyPy9K9W1gH3USizoCNjg41oxSR2jp/e98jxKpYYS5Vx9gJk5DS+zNLeL3SiMRcO9XUxP1439F6q1g==","roles":[{"roleId":1,"roleName":"超级管理员","remark":null},{"roleId":2,"roleName":"记者","remark":"11"}]}]
     * code : 0
     */

    private String msg;
    private int code;
    private List<ResultBean> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }




    public static class ResultBean extends BaseIndexPinyinBean implements Serializable {
        private static final long serialVersionUID = -7060210544600464481L;
        /**
         * userId : 1
         * username : admin
         * email : root@qq.io
         * mobile : 13612345678
         * deptId : 1
         * deptName : 广电总局
         * name : 张三
         * portraituri : null
         * ryToken : FcNa9b4YRGlc8OS+60uHb1RAi/nSXuqXGZZHSdpB50Gm5VoiT77hgihouZGujf1fGFktOwTvRfU=
         * roles : [{"roleId":2,"roleName":"记者","remark":"11"}]
         */

        private boolean isTop;//是否是最上面的 不需要被转化成拼音的
        public boolean isTop() {
            return isTop;
        }

        public ResultBean setTop(boolean top) {
            isTop = top;
            return this;
        }
        public ResultBean(String name) {
            this.name = name;
        }
        public ResultBean(String name,String id,String token) {
            this.name = name;
            this.ryId=id;
            this.ryToken=token;
        }



        @Override
        public boolean isNeedToPinyin() {
            return !isTop;
        }


        @Override
        public boolean isShowSuspension() {
            return !isTop;
        }

        private int userId;
        private String username;
        private String email;
        private String mobile;
        private int deptId;
        private String deptName;
        private String name;
        private Object portraituri;
        private String ryToken;
        private List<RolesBean> roles;
        private String ryId;

        @Override
        public String getTarget() {
            return name;
        }

        public int getUserId() {
            return userId;
        }
        public String getRyId() {
            return ryId;
        }

        public void setRyId(String ryId) {
            this.ryId = ryId;
        }
        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getDeptId() {
            return deptId;
        }

        public void setDeptId(int deptId) {
            this.deptId = deptId;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getName() {

            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getPortraituri() {
            return portraituri;
        }

        public void setPortraituri(Object portraituri) {
            this.portraituri = portraituri;
        }

        public String getRyToken() {
            return ryToken;
        }

        public void setRyToken(String ryToken) {
            this.ryToken = ryToken;
        }

        public List<RolesBean> getRoles() {
            return roles;
        }

        public void setRoles(List<RolesBean> roles) {
            this.roles = roles;
        }

        public static class RolesBean implements Serializable{
            /**
             * roleId : 2
             * roleName : 记者
             * remark : 11
             */

            private int roleId;
            private String roleName;
            private String remark;

            public int getRoleId() {
                return roleId;
            }

            public void setRoleId(int roleId) {
                this.roleId = roleId;
            }

            public String getRoleName() {
                return roleName;
            }

            public void setRoleName(String roleName) {
                this.roleName = roleName;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }
        }
    }
}
