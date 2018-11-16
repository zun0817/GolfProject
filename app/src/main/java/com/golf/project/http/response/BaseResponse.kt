package com.golf.project.http.response

/**
 * 作者： Zun.
 * 创建： 2018/11/6.
 * 说明： 返回数据基础类
 */
class BaseResponse<T>(var data: T?, var errorMsg: String, var errorCode: Int) {

    companion object {
        val SUCCESS = 0
        val FAIL = 1
    }

}
