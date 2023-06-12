package com.sprintray.net.utils;

import androidx.lifecycle.ViewModelStore;

import java.util.HashMap;

public class ViewModelStoreStore {


    private final HashMap<String, ViewModelStore> mMap = new HashMap<>();

    final void put(String key, ViewModelStore viewModel) {
        mMap.put(key, viewModel);
    }

    final ViewModelStore get(String key) {
        return mMap.get(key);
    }

    final void remove(String key) {
        mMap.remove(key);
    }

    final int getSize() {
        return mMap.size();
    }

    public final void clear() {
        for (ViewModelStore vm : mMap.values()) {
            vm.clear();
        }
        mMap.clear();
    }

}
