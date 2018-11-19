package com.shadt.ui.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shadt.util.MyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    /**
     * 根据key值从json获取对应的数据.
     *
     * @param jsonStr json Object数据
     * @param key     键值
     * @return
     */
    public static JsonObject getJsonObject(String jsonStr, String key) {
        return getJsonElement(jsonStr, key).getAsJsonObject();
    }

    /**
     * 根据key值从json获取对应的数据.
     *
     * @param jsonStr json Object数据
     * @param key     键值
     * @return
     */
    public static JsonArray getJsonArray(String jsonStr, String key) {
        return getJsonElement(jsonStr, key).getAsJsonArray();
    }

    /**
     * 根据key值从json获取对应的数据.
     *
     * @param jsonStr json Object数据
     * @param key     键值
     * @return
     */
    public static String getString(String jsonStr, String key) {
        return getJsonElement(jsonStr, key).getAsString();
    }

    /**
     * 根据key值从json获取对应的数据.
     *
     * @param jsonStr json Object数据
     * @param key     键值
     * @return
     */
    public static int getInt(String jsonStr, String key) {
        return getJsonElement(jsonStr, key).getAsInt();
    }

    /**
     * 根据key值从json获取对应的数据.
     *
     * @param jsonStr json Object数据
     * @param key     键值
     * @return
     */
    public static float getFloat(String jsonStr, String key) {
        return getJsonElement(jsonStr, key).getAsFloat();
    }

    /**
     * 根据key值从json获取对应的数据.
     *
     * @param jsonStr json Object数据
     * @param key     键值
     * @return
     */
    public static JsonElement getJsonElement(String jsonStr, String key) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();
        return jsonObject.get(key);
    }

    /**
     * 绑定json object数据到class,并且返回该class对象,不转换没有 @Expose 注解的字段.
     *
     * @param jsonStr json Object数据
     * @param cls     绑定class
     * @param <T>     对象
     * @return
     */
    public static <T> T getModel(String jsonStr, Class<T> cls) {
        Gson gson = new Gson();
        T t = gson.fromJson(jsonStr, cls);
        return t;
    }

    /**
     * 绑定json object数据到class,并且返回该class对象,不转换没有 @Expose 注解的字段.
     *
     * @param jsonStr json Object数据
     * @param cls     绑定class
     * @param <T>     对象
     * @return
     */
    public static <T> T getModel(JsonObject jsonStr, Class<T> cls) {
        Gson gson = new Gson();
        T t = gson.fromJson(jsonStr, cls);
        return t;
    }

    /**
     * 绑定json array数据到class,并且返回该class集合对象.
     *
     * @param jsonArrayStr json array数据
     * @param <T>          对象
     */
    public static <T> ArrayList<T> getListModel(String jsonArrayStr, Class<T> cls) {
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(jsonArrayStr).getAsJsonArray();
        Gson gson = new Gson();
        ArrayList<T> arrayList = new ArrayList<>();
        for (int index = 0; index < jsonArray.size(); index++) {
            JsonElement element = jsonArray.get(index);
            T t = gson.fromJson(element, cls);
            arrayList.add(t);
        }
        return arrayList;
    }

    /**
     * 绑定json array数据到class,并且返回该class集合对象.
     *
     * @param jsonArray json array数据
     * @param <T>       对象
     */
    public static <T> ArrayList<T> getListModel(JsonArray jsonArray, Class<T> cls) {
        Gson gson = new Gson();
        ArrayList<T> arrayList = new ArrayList<>();
        for (int index = 0; index < jsonArray.size(); index++) {
            JsonElement element = jsonArray.get(index);
            try {
                T t = gson.fromJson(element, cls);
                arrayList.add(t);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

        }
        return arrayList;
    }

    /**
     * 检测是否有某个key
     * @param json
     * @param json_key
     * @return
     */
    public static boolean hasJsonKey(String json, String json_key){
        try {
            JSONObject jsonObject=new JSONObject(json);
            return jsonObject.isNull(json_key)||jsonObject.has(json_key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //clientInfo  字段的数据
    public static String  fastJson(String mac,String imei,String ip,String appVersion ,String screenHeight,String screenWidth,String network) {
        JSONObject arr_0 = new JSONObject();
        try {


            JSONObject arr_00 = new JSONObject();

            JSONObject arr_22 = new JSONObject();
            arr_22.put("mac", mac);
            arr_22.put("imei", imei);
            arr_22.put("ip", ip);
            arr_22.put("appVersion", appVersion);


//            arr_22.put("3rd_ad_version", _3rd_ad_version);
            JSONObject arr_33 = new JSONObject();
            arr_33.put("screenHeight", screenHeight);
            arr_33.put("screenWidth", screenWidth);
             

            arr_33.put("network", network);

            arr_00.put("userInfo", arr_22);
            arr_00.put("deviceInfo", arr_33);
            arr_0.put("clientInfo", arr_00);
            MyLog.i("fastJson" + arr_0.toString());
        } catch (JSONException e) {
            Log.i("OTH", "JS获取用户信息错误");
        }
        return  ""+ arr_0;
    }
}
