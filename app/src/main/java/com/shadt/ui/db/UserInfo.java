package com.shadt.ui.db;

import java.util.List;

public class UserInfo {
    /**
     * msg : 处理成功
     * result : {"userId":8,"ryId":"8","username":"wb","email":"1239@qnq.comjs","mobile":"13912345788","deptId":1,"deptName":"官渡镇","name":"王奔ns","portraituri":"/resources/portrait/8/5a43c4c0-24f0-4673-8859-b4a8c50c4b1320180927150830.png","token":"00fb5aed1b150c7aa7ea5806714f4dae","ryToken":"H9ijImphsn69qk6mELwHtSizoCNjg41oxSR2jp/e98jEHtUH0KrQ5OWBxdiKYyF+dxcMbkS+YSYxP1439F6q1g==","online":null,"roles":[{"roleId":1,"roleName":"超级管理员","remark":"超级管理员"}]}
     * code : 0
     */

    private String msg;
    private ResultBean result;
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultBean {
        /**
         * userId : 8
         * ryId : 8
         * username : wb
         * email : 1239@qnq.comjs
         * mobile : 13912345788
         * deptId : 1
         * deptName : 官渡镇
         * name : 王奔ns
         * portraituri : /resources/portrait/8/5a43c4c0-24f0-4673-8859-b4a8c50c4b1320180927150830.png
         * token : 00fb5aed1b150c7aa7ea5806714f4dae
         * ryToken : H9ijImphsn69qk6mELwHtSizoCNjg41oxSR2jp/e98jEHtUH0KrQ5OWBxdiKYyF+dxcMbkS+YSYxP1439F6q1g==
         * online : null
         * roles : [{"roleId":1,"roleName":"超级管理员","remark":"超级管理员"}]
         */

        private int userId;
        private String ryId;
        private String username;
        private String email;
        private String mobile;
        private int deptId;
        private String deptName;
        private String name;
        private String portraituri;
        private String token;
        private String ryToken;
        private Object online;
        private List<RolesBean> roles;
        private  String mediaApikey;
        private  String mediaUserid;

        public String getMediaApikey() {
            return mediaApikey;
        }

        public void setMediaApikey(String mediaApikey) {
            this.mediaApikey = mediaApikey;
        }

        public String getMediaUserid() {
            return mediaUserid;
        }

        public void setMediaUserid(String mediaUserid) {
            this.mediaUserid = mediaUserid;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getRyId() {
            return ryId;
        }

        public void setRyId(String ryId) {
            this.ryId = ryId;
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

        public String getPortraituri() {
            return portraituri;
        }

        public void setPortraituri(String portraituri) {
            this.portraituri = portraituri;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRyToken() {
            return ryToken;
        }

        public void setRyToken(String ryToken) {
            this.ryToken = ryToken;
        }

        public Object getOnline() {
            return online;
        }

        public void setOnline(Object online) {
            this.online = online;
        }

        public List<RolesBean> getRoles() {
            return roles;
        }

        public void setRoles(List<RolesBean> roles) {
            this.roles = roles;
        }

        public static class RolesBean {
            /**
             * roleId : 1
             * roleName : 超级管理员
             * remark : 超级管理员
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
