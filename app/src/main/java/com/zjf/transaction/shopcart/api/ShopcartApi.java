package com.zjf.transaction.shopcart.api;

import com.zjf.transaction.base.DataResult;
import com.zjf.transaction.main.model.Commodity;
import com.zjf.transaction.shopcart.model.ShopcartItem;

import java.util.List;

import androidx.room.Delete;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ShopcartApi {

    @POST("/shopcart/add")
    Single<DataResult<String>> add(@Query("userId") String userId, @Query("commodityId") String commodityId);

    @GET("/shopcart/getAll")
    Single<DataResult<List<ShopcartItem>>> getShopcartItem(@Query("userId") String userId, @Query("pageNum")int pageNum);

    @DELETE("/shopcart/delete")
    Single<DataResult<String>> delete(@Query("userId") String userId, @Query("commodity") String commodityId);


    @HTTP(method = "DELETE", path = "/shopcart/deleteMore", hasBody = true)
    Single<DataResult<String>> deleteMore(@Query("userId") String userId, @Body List<String> list);

    @GET("/shopcart/isExist")
    Single<DataResult<Integer>> isShopcartExist(@Query("userId") String userId, @Query("commodityId") String commodityId);
}
