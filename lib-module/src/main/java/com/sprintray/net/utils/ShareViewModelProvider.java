package com.sprintray.net.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

public class ShareViewModelProvider {

    private static ViewModelStoreStore mViewModelStoreStore;//key:以XXXViewModel.getClass().getCanonicalName();
    private static LifecycleStore mLifecycleStore;//key:以LifecycleStore.toString()
    private static RefCountStore mRefCountStore;//key:以XXXViewModel.getClass().getCanonicalName(); 当引用计数为0时表示ViewModelStore可以从mViewModelStoreStore中清除并调用ViewModelStore.clear()

    /**
     * 只要cls完整类名相同，则能得到同一个实例
     *
     * @param viewModelCls ViewModel的Class
     * @param factory      viewModelCls的构建工厂，可为null,为null时cls必须拥有空构造函数，否则抛出异常
     * @param <T>          T extends ViewModel
     * @return viewModelCls实例
     */
    public static synchronized <T extends ViewModel> T get(@NonNull LifecycleOwner lifecycleOwner, @NonNull Class<T> viewModelCls, @Nullable ViewModelProvider.Factory factory) {
        if (mViewModelStoreStore == null) {
            mViewModelStoreStore = new ViewModelStoreStore();
        }
        if (mRefCountStore == null) {
            mRefCountStore = new RefCountStore();
        }
        if (mLifecycleStore == null) {
            mLifecycleStore = new LifecycleStore();
        }

        final String canonicalName = viewModelCls.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
        }

        final String lifecycleKey = lifecycleOwner.toString();//发现fragment重写了toString,因此名字变得好长
        if (mLifecycleStore.get(lifecycleKey) == null) {
            mLifecycleStore.put(lifecycleKey, lifecycleOwner);
            mRefCountStore.addOne(canonicalName);
            lifecycleOwner.getLifecycle().addObserver(new MyLifecycleEventObserver(canonicalName));
        }
        ViewModelStore viewModelStore = mViewModelStoreStore.get(canonicalName);
        if (viewModelStore == null) {
            viewModelStore = new ViewModelStore();
            mViewModelStoreStore.put(canonicalName, viewModelStore);
        }
        ViewModel viewModel = new ViewModelProvider(viewModelStore, factory).get(viewModelCls);
        return (T) viewModel;
    }

    public static synchronized <T extends ViewModel> T get(@NonNull LifecycleOwner lifecycleOwner, @NonNull Class<T> viewModelCls) {
        return get(lifecycleOwner, viewModelCls, NewInstanceFactory.getInstance());
    }

    /**
     * 是否正在销毁重建
     *
     * @param owner
     * @return true--表示正在销毁重建
     */
    public static boolean isChangingConfigurations(LifecycleOwner owner) {
        if (owner instanceof Fragment) {
            FragmentActivity activity = ((Fragment) owner).getActivity();
            if (activity == null) {
                return false;
            }
            return activity.isChangingConfigurations();
        }
        if (owner instanceof FragmentActivity) {
            return ((FragmentActivity) owner).isChangingConfigurations();
        }
        return false;
    }

    public static class MyLifecycleEventObserver implements LifecycleEventObserver {

        private String mViewModelStoreKey;

        public MyLifecycleEventObserver(String viewModelStoreKey) {
            mViewModelStoreKey = viewModelStoreKey;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event != Lifecycle.Event.ON_DESTROY) {
                return;
            }

            String key = source.toString();
            mLifecycleStore.remove(key);
            Log.i("123456", key + "已从LifecycleStore移除，现在剩余个数：" + mLifecycleStore.getSize());
            int count = mRefCountStore.subtractOne(mViewModelStoreKey);
            if (count == 0) {
                if (isChangingConfigurations(source)) {//为true表示正在销毁重建，此时的ViewModelStore不应该移除掉
                    Log.i("123456", "正发生销毁重建");
                    return;
                }

                ViewModelStore viewModelStore = mViewModelStoreStore.get(mViewModelStoreKey);
                mViewModelStoreStore.remove(mViewModelStoreKey);
                Log.i("123456", mViewModelStoreKey + "已从ViewModelStoreStore移除,现在剩余个数：" + mViewModelStoreStore.getSize());
                if (viewModelStore != null) {
                    viewModelStore.clear();
                }
            }
        }
    }

    public static class NewInstanceFactory implements ViewModelProvider.Factory {

        private static ViewModelProvider.NewInstanceFactory sInstance;

        /**
         * Retrieve a singleton instance of NewInstanceFactory.
         *
         * @return A valid {@link ViewModelProvider.NewInstanceFactory}
         */
        @NonNull
        static ViewModelProvider.NewInstanceFactory getInstance() {
            if (sInstance == null) {
                sInstance = new ViewModelProvider.NewInstanceFactory();
            }
            return sInstance;
        }

        @SuppressWarnings("ClassNewInstance")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection TryWithIdenticalCatches
            try {
                return modelClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }

}
