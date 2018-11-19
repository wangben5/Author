package com.shadt.ui.db;

import java.util.List;

public class TaskInfo {

    /**
     * msg : 处理成功
     * result : {"totalCount":1,"pageSize":100,"totalPage":1,"currPage":1,"list":[{"taskId":305,"title":"测试选题公开与否","ctime":"2018-10-30 14:21:00","address":"fd","assignUser":"王青青","executeUser":[{"userId":19,"username":"zzf","name":"张振飞","portraituri":"/resources/portrait/19/e78e6549-9b03-40d9-9f15-642942d7bb2020181008113714.jpg"},{"userId":40,"username":"qixiaoyang","name":"qxy","portraituri":"/resources/portrait/40/53641b38-737f-4f89-836f-787864d92ddf20181019170253.jpg"}],"status":1,"statusName":"进行中","resources":[]}]}
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
         * totalCount : 1
         * pageSize : 100
         * totalPage : 1
         * currPage : 1
         * list : [{"taskId":305,"title":"测试选题公开与否","ctime":"2018-10-30 14:21:00","address":"fd","assignUser":"王青青","executeUser":[{"userId":19,"username":"zzf","name":"张振飞","portraituri":"/resources/portrait/19/e78e6549-9b03-40d9-9f15-642942d7bb2020181008113714.jpg"},{"userId":40,"username":"qixiaoyang","name":"qxy","portraituri":"/resources/portrait/40/53641b38-737f-4f89-836f-787864d92ddf20181019170253.jpg"}],"status":1,"statusName":"进行中","resources":[]}]
         */

        private int totalCount;
        private int pageSize;
        private int totalPage;
        private int currPage;
        private List<ListBean> list;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getCurrPage() {
            return currPage;
        }

        public void setCurrPage(int currPage) {
            this.currPage = currPage;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * taskId : 305
             * title : 测试选题公开与否
             * ctime : 2018-10-30 14:21:00
             * address : fd
             * assignUser : 王青青
             * executeUser : [{"userId":19,"username":"zzf","name":"张振飞","portraituri":"/resources/portrait/19/e78e6549-9b03-40d9-9f15-642942d7bb2020181008113714.jpg"},{"userId":40,"username":"qixiaoyang","name":"qxy","portraituri":"/resources/portrait/40/53641b38-737f-4f89-836f-787864d92ddf20181019170253.jpg"}]
             * status : 1
             * statusName : 进行中
             * resources : []
             */

            private int taskId;
            private String title;
            private String ctime;
            private String address;
            private String assignUser;
            private int status;
            private String statusName;
            private List<ExecuteUserBean> executeUser;
            private List<?> resources;

            public int getTaskId() {
                return taskId;
            }

            public void setTaskId(int taskId) {
                this.taskId = taskId;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getCtime() {
                return ctime;
            }

            public void setCtime(String ctime) {
                this.ctime = ctime;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getAssignUser() {
                return assignUser;
            }

            public void setAssignUser(String assignUser) {
                this.assignUser = assignUser;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getStatusName() {
                return statusName;
            }

            public void setStatusName(String statusName) {
                this.statusName = statusName;
            }

            public List<ExecuteUserBean> getExecuteUser() {
                return executeUser;
            }

            public void setExecuteUser(List<ExecuteUserBean> executeUser) {
                this.executeUser = executeUser;
            }

            public List<?> getResources() {
                return resources;
            }

            public void setResources(List<?> resources) {
                this.resources = resources;
            }

            public static class ExecuteUserBean {
                /**
                 * userId : 19
                 * username : zzf
                 * name : 张振飞
                 * portraituri : /resources/portrait/19/e78e6549-9b03-40d9-9f15-642942d7bb2020181008113714.jpg
                 */

                private int userId;
                private String username;
                private String name;
                private String portraituri;

                public int getUserId() {
                    return userId;
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
            }
        }
    }
}
