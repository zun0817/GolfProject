package com.golf.project.http.http

import com.golf.project.http.response.BaseResponse
import com.golf.project.mvp.model.RegisterModel
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.http.*

/**
 * 作者： Zun.
 * 创建： 2018/11/6.
 * 说明： API接口入参服务管理类
 */
interface GolfAPI {

    /**
     * 用户注册
     */
    @POST("user/register")
    fun userRegister(
        @Query("username") userName: String,
        @Query("password") password: String,
        @Query("repassword") rePassword: String
    ): Observable<BaseResponse<RegisterModel>>
}