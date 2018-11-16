package com.golf.project.http.converter;

import com.golf.project.http.response.BaseResponse;
import com.google.gson.*;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * 当出现data为null的时候会出现空指针，在这个地方处理
 */
public class ResponseTypeAdapter implements JsonDeserializer<BaseResponse> {
    @Override
    public BaseResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (json.isJsonObject()) {
            final JsonObject obj = json.getAsJsonObject();
            if (obj.get("data").isJsonNull()) {
                return new BaseResponse(new JSONObject(), obj.get("errorMsg").getAsString(), obj.get("errorCode").getAsInt());
            } else {
                return new Gson().fromJson(json, typeOfT);
            }
        }
        return null;
    }


}
