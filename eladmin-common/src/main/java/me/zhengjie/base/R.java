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

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 统一返回值
 *
 * @author yongjie.teng
 * @date 2018/8/17
 * @package com.soraka.admin.model.dto
 */
public class R<T> implements Serializable {

	@ApiModelProperty(value = "返回码 200：成功")
	private int status = BuzCode.SUCCEED.getCode();

	@ApiModelProperty(value = "返回信息")
	private String message = BuzCode.SUCCEED.getMessage();

	@ApiModelProperty(value = "返回数据")
	private T data;

	public static <T> R<T> success() {
		return new R<T>();
	}

	public static <T> R<T> success(T value) {
		return new R<T>(BuzCode.SUCCEED, value);
	}

	public static <T> R<T> fail() {
		return new R<T>(BuzCode.FAILED.getCode(), BuzCode.FAILED.getMessage());
	}

	public static R<Object> operate(boolean isSucceed) {
		return isSucceed ? R.<Object>success() : R.<Object>fail();
	}

	public R() {
	}

	public R(BuzCode buzCode) {
		this.status = buzCode.getCode();
		this.message = buzCode.getMessage();
	}

	public R(BuzCode buzCode, T data) {
		this.status = buzCode.getCode();
		this.message = buzCode.getMessage();
		this.data = data;
	}

	public R(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public R(int status, String message, T data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
