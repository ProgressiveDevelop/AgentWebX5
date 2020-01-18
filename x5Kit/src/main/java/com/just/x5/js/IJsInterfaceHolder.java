package com.just.x5.js;

import androidx.collection.ArrayMap;

public interface IJsInterfaceHolder {

    boolean checkObject(Object v);

    IJsInterfaceHolder addJavaObject(String k, Object v);

    IJsInterfaceHolder addJavaObjects(ArrayMap<String, Object> maps);

}
