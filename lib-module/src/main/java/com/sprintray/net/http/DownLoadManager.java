package com.sprintray.net.http;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.sprintray.net.http.download.DownLoadSubscriber;
import com.sprintray.net.http.download.ProgressCallBack;
import com.sprintray.net.http.interceptor.ProgressInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public class DownLoadManager {
    private static final String TAG = "DownLoadManager";
    private static DownLoadManager instance;

    private static Retrofit retrofit;

    private DownLoadManager() {
        buildNetWork();
    }

    /**
     * 单例模式
     *
     * @return DownLoadManager
     */
    public static DownLoadManager getInstance() {
        if (instance == null) {
            instance = new DownLoadManager();
        }
        return instance;
    }

    //下载
    public void load(String downUrl, final ProgressCallBack callBack) {
        retrofit.create(ApiService.class)
                .download(downUrl)
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程

                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(ResponseBody responseBody)  {

                        callBack.saveFile(responseBody);
                        
                        return Observable.just(responseBody);
                    }
                })
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        
                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        callBack.onError(e);


                        Log.d(TAG, "onError: "+e.getMessage());
//                        e.printStackTrace();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void buildNetWork() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ProgressInterceptor())
                .connectTimeout(40, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(NetworkUtil.url)
                .build();
    }

    private interface ApiService {
        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String url);
    }











//    private void down(){
//        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://dl.hdslb.com/mobile/latest/iBiliPlayer-html5_app_bili.apk"));//添加下载文件的网络路径
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "iBiliPlayer-html5_app_bili.apk");//添加保存文件路径与名称
//        request.setTitle("测试下载");//添加在通知栏里显示的标题
//        request.setDescription("下载中");//添加在通知栏里显示的描述
//        request.addRequestHeader("token","11");//如果你的下载需要token，或者有秘钥要求，可以在此处添加header的 key： value
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);//设置下载的网络类型
//        request.setVisibleInDownloadsUi(false);//是否显示下载 从Android Q开始会被忽略
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//下载中与下载完成后都会在通知中显示| 另外可以选 DownloadManager.Request.VISIBILITY_VISIBLE 仅在下载中时显示在通知中,完成后会自动隐藏
//        long id = downloadManager.enqueue(request);//加入队列，会返回一个唯一下载id
//    }










}
