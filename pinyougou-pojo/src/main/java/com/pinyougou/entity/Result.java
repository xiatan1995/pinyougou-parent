package com.pinyougou.entity;

import java.io.Serializable;

public class Result implements Serializable {

    private boolean success;

    private String jieguo;

    public Result(boolean success, String jieguo) {
        this.success = success;
        this.jieguo = jieguo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getJieguo() {
        return jieguo;
    }

    public void setJieguo(String jieguo) {
        this.jieguo = jieguo;
    }
}
