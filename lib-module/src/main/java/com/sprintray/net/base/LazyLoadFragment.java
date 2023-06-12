package com.sprintray.net.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trello.rxlifecycle4.components.support.RxFragment;

import java.util.List;

public abstract class LazyLoadFragment  extends RxFragment {


    //不可见
    private boolean isLastVisible = false;
    //是否是第一次可见
    private boolean isFirst = true;
    //是否已经执行onResume
    private boolean isResuming = false;
    //view是否已经创建
    private boolean isViewCreate = false;
    //是否被隐藏
    private boolean hidden = false;



    /**
     * fragment可见
     *
     * @param isFirst
     * @param isViewCreate
     */
    public abstract void onVisible(boolean isFirst, boolean isViewCreate);

    /**
     * fragment不可见
     */
    public abstract void onInvisible();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        isViewCreate = true;
        isLastVisible = false;
        isFirst = true;
        hidden = false;

        return super.onCreateView(inflater, container, savedInstanceState);
    }





    @Override
    public void onResume() {
        super.onResume();
        isResuming = true;
        //  尝试设置可见
        trySetVisibility(true);
    }


    @Override
    public void onPause() {
        super.onPause();
        isResuming = false;
        //  尝试设置不可见
        trySetVisibility(false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreate = false;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //设置子Fragment当前可见状态改变了
        setChildFragmentUserVisibleHint(isVisibleToUser);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        setChildFragmentHiddenChanged(hidden);
    }


    /**
     * 尝试修改可见状态
     * 根据当前是否显示判断
     *
     * @param tryToShow
     */
    private void trySetVisibility(boolean tryToShow) {

        if (isLastVisible) {
            //当前可见
            if (tryToShow) {
                //尝试显示  当前已经是显示
                return;
            }

            //尝试隐藏
            if (!isFragmentVisible()) {
                //当前已经是不可见,回调隐藏方法
                onInvisible();
                isLastVisible = false;
            }

        } else {
            //当前不可见
            if (!tryToShow) {
                //尝试隐藏 当前已经是隐藏
                return;
            }

            if (isFragmentVisible()) {
                onVisible(isFirst, isViewCreate);
                isLastVisible = true;
                isFirst = false;
            }
        }

    }


    /**
     * Fragment是否可见
     *
     * @return
     */
    public boolean isFragmentVisible() {

        if (isResuming && getUserVisibleHint() && !hidden) {
            return true;
        }
        return false;
    }


    private void setChildFragmentUserVisibleHint(boolean isVisibleToUser) {
        // 尝试设置可见状态
        trySetVisibility(isVisibleToUser);
        if (isAdded()) {
            //已经被添加
            // 当Fragment状态改变，其子Fragment也状态改变。
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof LazyLoadFragment) {
                        ((LazyLoadFragment) fragment).setChildFragmentUserVisibleHint(isVisibleToUser);
                    }
                }
            }
        }
    }


    private void setChildFragmentHiddenChanged(boolean hidden) {
        this.hidden = hidden;
        trySetVisibility(!hidden);
        if (isAdded()) {
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof LazyLoadFragment) {
                        ((LazyLoadFragment) fragment).setChildFragmentHiddenChanged(hidden);
                    }
                }
            }
        }

    }


}
