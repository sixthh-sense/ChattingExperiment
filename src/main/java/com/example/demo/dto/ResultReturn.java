package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResultReturn {
    public static Long member;
    private Boolean ok;
    private Object results;
    private String msg;

    public ResultReturn(Boolean ok, Object results, String msg) {
        this.ok = ok;
        this.results = results;
        this.msg = msg;
    }

    public ResultReturn(Boolean ok, String msg) {
        this.ok = ok;
        this.results = null;
        this.msg = msg;
    }

    public ResultReturn() {
        this.ok = null;
        this.results = null;
        this.msg = null;
    }

    public ResultReturn(boolean ok, String msg, UserDto userDto) {
        this.ok=ok;
        this.results= userDto.getToken();
        this.msg=msg;
    }
}