package com.sssprog.instagramtest.api;

import com.google.gson.internal.LinkedTreeMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class QueryParams {
    private LinkedTreeMap<String, ParamValue> params = new LinkedTreeMap<>();

    public QueryParams add(String name, Object value) {
        return add(name, value, true);
    }

    public QueryParams add(String name, Object value, boolean encode) {
        params.put(name, new ParamValue(value, encode));
        return this;
    }

    private String toString(boolean urlParams) {
        if (params.isEmpty()) {
            return "";
        }
        String result = urlParams ? "?" : "";
        boolean first = true;
        for (Map.Entry<String, ParamValue> entry : params.entrySet()) {
            if (!first) {
                result += "&";
            }
            result += entry.getKey() + "=" + entry.getValue().asString(urlParams);
            first = false;
        }
        return result;
    }

    public String asUrlParams() {
        return toString(true);
    }

    public String asBody() {
        return toString(false);
    }

    private static class ParamValue {
        final Object value;
        final boolean encode;

        public ParamValue(Object value, boolean encode) {
            this.value = value;
            this.encode = encode;
        }

        public String asString(boolean encodeIfNeeded) {
            if (encode && encodeIfNeeded) {
                try {
                    return URLEncoder.encode(value.toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }
            }
            return value.toString();
        }
    }
}
