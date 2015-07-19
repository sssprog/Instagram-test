package com.sssprog.instagramtest.api;

import com.google.gson.internal.LinkedTreeMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class QueryParams {
    private LinkedTreeMap<String, Object> params = new LinkedTreeMap<>();

    public QueryParams add(String name, Object value) {
        params.put(name, value);
        return this;
    }

    public String asUrlParams() {
        if (params.isEmpty()) {
            return "";
        }
        String result = "?";
        boolean first = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String value;
            try {
                value = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                value = entry.getValue().toString();
            }
            if (!first) {
                result += "&";
            }
            result += entry.getKey() + "=" + value;
            first = false;
        }
        return result;
    }

    public String asBody() {
        String result = asUrlParams();
        return result.isEmpty() ? result : result.substring(1);
    }
}
