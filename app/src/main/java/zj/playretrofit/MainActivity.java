package zj.playretrofit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import zj.playretrofit.base.BaseAppCompatActivity;
import zj.playretrofit.net.RequestApi;
import zj.playretrofit.net.ResponseData;

public class MainActivity extends BaseAppCompatActivity {

    private String TAG = "HardPlayReTro";

    //以此URL为例
    String URL = "https://api.douban.com/v2/movie/top250?start=0&count=10"; //豆瓣接口
    RequestApi retrofitPostApi = null;
    private TextView mtv;
    private Button btnPostGoFrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 初始化,得到retrofitPostApi的接口实例
         */
        init();

        testNetStView();

    }

    /**
     * 测试网络状态View
     */
    private void testNetStView() {
        Button btnPost = (Button) findViewById(R.id.btn_post);
        mtv = (TextView) findViewById(R.id.tv);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNet(mtv);
            }
        });

        Button btnPostGoFrag = (Button) findViewById(R.id.btn_post_go_frag);
        btnPostGoFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FragmentContainerActivity.class));
            }
        });
    }

    /**
     * 处理网络
     * @param tv
     */
    private void handleNet(final TextView tv) {
        String url = "v2/book/1003078";
        Observable<String> mapObservable = retrofitPostApi.getPathDoubadataRx(url).map(new Function<ResponseData, String>() {

            @Override
            public String apply(ResponseData responseData) throws Exception {
                return responseData.toString();
            }
        });

        mapObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            /**
             * @param value
             */
            @Override
            public void onNext(String value) {
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                tv.setText(value);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 得到retrofitPostApi的接口实例
     */
    private void init() {
        //ps:这里采用的是Java的动态代理模式，得到请求接口对象
        retrofitPostApi = createRetrofit().create(RequestApi.class);
    }

    /**
     * 创建retrofit
     *
     * @return
     */
    @NonNull
    private Retrofit createRetrofit() {
        String BASE_URL = "https://api.douban.com/"; //豆瓣接口
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);

        return new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(BASE_URL)//baseurl设置
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())//增加返回值为Gson的支持（返回实体类）
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//这个adapter很重要，专门找了方案来解决这个问题
                .build();
    }

    @Override
    public void netWorkStatusEmptyRefresh() {
        super.netWorkStatusEmptyRefresh();
        handleNet(mtv);
    }

    @Override
    public void netWorkStatusErrorRefresh() {
        super.netWorkStatusErrorRefresh();
        handleNet(mtv);
    }

    @Override
    public void netWorkStatusNoNetWorkRefresh() {
        super.netWorkStatusNoNetWorkRefresh();
        handleNet(mtv);
    }
}
