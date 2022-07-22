/*
 * Apache License
 * Version 2.0, January 2004
 *
 *    Copyright 2018 北有风雪 (yongjie.teng@qq.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.zhengjie.base;

/**
 * 业务异常编码
 *
 * @author yongjie.teng
 * @date 2018/8/27
 * @package com.soraka.admin.exception
 */
public enum BuzCode {
    /**
     * 0 成功
     */
    SUCCEED(200, "操作成功"),

    /**
     * 9999 失败
     */
    FAILED(500, "操作失败"),

    PARAM_ERROR(400, "参数异常"),

    /**
     * 1*** 用户相关业务异常
     */
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PWD_ERROR(1002, "密码不正确"),
    USER_IS_BLOCKED(1003, "用户被锁定");

    /**
     * 业务异常编码
     */
    private int code;

    /**
     * 异常信息
     */
    private String message;

    BuzCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
