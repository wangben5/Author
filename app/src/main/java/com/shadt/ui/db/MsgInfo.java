package com.shadt.ui.db;

public class MsgInfo {

    /**
     * msg : 此邮箱已被使用
     * result : false
     * code : 500
     */

    private String msg;
    private boolean result;
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
