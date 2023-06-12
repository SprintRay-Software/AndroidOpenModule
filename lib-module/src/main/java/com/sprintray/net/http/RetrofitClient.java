package com.sprintray.net.http;

import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.StringUtils;
import com.sprintray.net.http.cookie.CookieJarImpl;
import com.sprintray.net.http.cookie.store.PersistentCookieStore;
import com.sprintray.net.http.interceptor.BaseInterceptor;
import com.sprintray.net.http.interceptor.logging.Level;
import com.sprintray.net.http.interceptor.logging.LoggingInterceptor;
import com.sprintray.net.http.model.RefreshTokenResponse;
import com.sprintray.net.http.source.SpecificTimeout;
import com.sprintray.net.http.source.TokenApiService;
import com.sprintray.net.utils.HttpSpKeys;
import com.sprintray.net.utils.KLog;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.platform.Platform;
import retrofit2.Call;
import retrofit2.Invocation;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient封装单例类, 实现网络请求
 */
public class RetrofitClient {
    //超时时间
    private static final int DEFAULT_TIMEOUT = 20;
    //缓存时间
    private static final int CACHE_TIMEOUT = 10 * 1024 * 1024;
    //服务端根路径
    public static String baseUrl = "https://www.oschina.net/";
    public static String devUrl = "https://dev-iot.sprintray.com/";
    public static String proUrl = "https://iot.sprintray.com/";
    public static String testUrl = "https://staging-iot.sprintray.com/";


    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;

    private Cache cache = null;
    private File httpCacheDirectory;

    private HashMap<String, String> urlMap = new HashMap<>(8);

    private static class SingletonHolder {
        private static RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private RetrofitClient() {
        this(baseUrl, null);
        urlMap.put("register_dev", "https://dev.api.SprintRay.com");
    }

    private RetrofitClient(String url, Map<String, String> headers) {
        String successMsg = ShellUtils.execCmd("getprop com.capricornus.tempenvironment", true).successMsg;
        String apiKeyHeader = "";
        if ("QA".equals(successMsg)) {
            url = testUrl;
            apiKeyHeader = "l4iBc0xwMFYll3edvOlgV8zQOqhgTYaIm9gAwuuu_capricorn";
        } else if ("PROD".equals(successMsg)) {
            url = proUrl;
            apiKeyHeader = "l4iBc0xwMFYll3edvOlgV8zQOqhgTYaIm9gAwuuu_capricorn";
        } else {
            url = devUrl;
            apiKeyHeader = "l4iBc0xwMFYll3edvOlgV8zQOqhgTYaIm9gAwuuu_capricorn";
        }
        if (headers==null){
            headers = new TreeMap<>();
        }
        headers.put("x-api-key",apiKeyHeader);
        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(BaseApplication.getInstance().getCacheDir(), "SprintRay_cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, CACHE_TIMEOUT);
            }
        } catch (Exception e) {
            KLog.e("Could not create http cache", e);
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(BaseApplication.getInstance())))
                .authenticator(authenticator)
                .addInterceptor(new BaseInterceptor(headers))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addInterceptor(urlInterceptor)
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(true)
                        .setLevel(Level.BODY) //打印的等级
                        .log(Platform.INFO) // 打印类型
                        .request("Request") // request的Tag
                        .response("Response")// Response的Tag
                        .build()
                )
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(timeOutInterceptor)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(url)
                .build();

    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    /**
     * /**
     * execute your customer API
     * For example:
     * MyApiService service =
     * RetrofitClient.getInstance(MainActivity.this).create(MyApiService.class);
     * <p>
     * RetrofitClient.getInstance(MainActivity.this)
     * .execute(service.lgon("name", "password"), subscriber)
     * * @param subscriber
     */

    public static <T> T execute(Observable<T> observable, Observer<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return null;
    }

    /**
     * use @SpecificTimeout to set custom readTime to any request
     */
    private Interceptor timeOutInterceptor = chain -> {
        Request request = chain.request();
        final Invocation tag = request.tag(Invocation.class);
        final Method method = tag != null ? tag.method() : null;
        final SpecificTimeout timeout = method != null ? method.getAnnotation(SpecificTimeout.class) : null;
        if (timeout != null) {
            return chain.withReadTimeout(timeout.duration(), timeout.unit())
                    .withConnectTimeout(timeout.duration(), timeout.unit())
                    .withWriteTimeout(timeout.duration(), timeout.unit())
                    .proceed(request);
        }

        return chain.proceed(request);
    };

    /**
     * auto change baseURL
     */
    private Interceptor urlInterceptor = chain -> {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader("Authorization", "Bearer " + SPUtils.getInstance(HttpSpKeys.SPKey.SP_NAME_ACCOUNT).getString(HttpSpKeys.SPKey.SP_ASSESS_TOKEN));
        List<String> list = request.headers("baseUrl");
        if (list.size() > 0) {
            //移除掉header 因为服务器不需要这个header,这个header只是在拦截器里用到
            builder.removeHeader("baseUrl");
            String key = list.get(0);
            //如果配置的header信息在HashMap里有声明
            if (!TextUtils.isEmpty(key) && urlMap.containsKey(key)) {
                HttpUrl newBaseUrl = HttpUrl.get(urlMap.get(key));
                HttpUrl oldBaseUrl = request.url();
                //将旧的请求地址里的协议、域名、端口号替换成配置的请求地址
                HttpUrl newFullUrl = oldBaseUrl.newBuilder().
                        scheme(newBaseUrl.scheme()).
                        host(newBaseUrl.host()).
                        port(newBaseUrl.port()).build();

                //创建带有新地址的新请求
                Request newRequest = builder.url(newFullUrl).build();
                return chain.proceed(newRequest);
            }
        }
        return chain.proceed(request);
    };

    /**
     * refresh token
     * when token failure, code==401  request refreshToken api
     */
    private final Authenticator authenticator = (route, response) -> {
        int code = response.code();
        if (code == 401) {
            String accessToken = SPUtils.getInstance(HttpSpKeys.SPKey.SP_NAME_ACCOUNT).getString(HttpSpKeys.SPKey.SP_ASSESS_TOKEN);
            String refreshToken = SPUtils.getInstance(HttpSpKeys.SPKey.SP_NAME_ACCOUNT).getString(HttpSpKeys.SPKey.SP_REFRESH_TOKEN);
            if (!StringUtils.isEmpty(accessToken) && !StringUtils.isEmpty(refreshToken)) {
                TokenApiService tokenApi = RetrofitClient.getInstance().create(TokenApiService.class);
                TreeMap<String, Object> request = new TreeMap<>();
                request.put("refreshToken", refreshToken);
                Call<RefreshTokenResponse> responseCall = tokenApi.refreshToken(request);
                Response<RefreshTokenResponse> execute = responseCall.execute();
                if (!execute.isSuccessful()) {
                    SPUtils.getInstance(HttpSpKeys.SPKey.SP_NAME_ACCOUNT).put(HttpSpKeys.SPKey.SP_ASSESS_TOKEN, "");
                    SPUtils.getInstance(HttpSpKeys.SPKey.SP_NAME_ACCOUNT).put(HttpSpKeys.SPKey.SP_REFRESH_TOKEN, "");
                    SPUtils.getInstance(HttpSpKeys.SPKey.SP_NAME_ACCOUNT).put(HttpSpKeys.SPKey.SP_ACCOUNT_EMAIL, "");
                    SPUtils.getInstance(HttpSpKeys.SPKey.SP_NAME_ACCOUNT).put(HttpSpKeys.SPKey.SP_ACCOUNT_PERMISSION, "");
                    return null;
                }
                RefreshTokenResponse body = execute.body();
                if (body != null) {
                    String token = body.getAccessToken();
                    //保存Token
                    SPUtils.getInstance(HttpSpKeys.SPKey.SP_NAME_ACCOUNT).put(HttpSpKeys.SPKey.SP_ASSESS_TOKEN, token);
                    return response.request().newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                }
            }
        }
        return response.request();
    };
}
