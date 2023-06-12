package com.sprintray.net.utils;

import android.util.Log;

import java.util.HashMap;

public class RefCountStore {

    private final HashMap<String, Integer> mMap = new HashMap<>();

    /**
     * 引用加一
     *
     * @param key
     * @return 当前引用个数
     */
    final synchronized int addOne(String key) {
        Integer count = mMap.get(key);
        if (count == null) {
            count = 0;
        }
        count++;
        mMap.put(key, count);
        Log.i("123456", key + "当前引用个数为" + count);
        return count;
    }

    /**
     * 引用减一
     *
     * @param key
     * @return 当前引用个数
     */
    final synchronized int subtractOne(String key) {
        Integer count = mMap.get(key);
        if (count == null) {
            Log.i("123456", key + "当前引用个数为" + 0);
            return 0;
        }
        count--;
        if (count == 0) {
            mMap.remove(key);
        } else {
            mMap.put(key, count);
        }
        Log.i("123456", key + "当前引用个数为" + count);
        return count;
    }

    public final void clear() {
        mMap.clear();
    }

}
