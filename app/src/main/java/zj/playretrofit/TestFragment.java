package zj.playretrofit;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import zj.playretrofit.base.BaseFragment;
import zj.playretrofit.net.RequestApi;
import zj.playretrofit.net.ResponseData;

public class TestFragment extends BaseFragment {

    //以此URL为例
    String URL = "https://api.douban.com/v2/movie/top250?start=0&count=10"; //豆瓣接口
    RequestApi retrofitPostApi = null;
    private TextView mTv;

    @Override
    protected int getLayoutId() {
        return R.layout.test_fragment_layout;
    }

    /**
     * 得到retrofitPostApi的接口实例
     */
    private void init() {
        //ps:这里采用的是Java的动态代理模式，得到请求接口对象
        retrofitPostApi = createRetrofit().create(RequestApi.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        mTv = (TextView) getActivity().findViewById(R.id.tv);
        Button postBtn = (Button) getActivity().findViewById(R.id.btn_post);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNet();
            }
        });
    }

    /**
     * 测试网络
     */
    private void handleNet() {
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
             * onSuccess
             * @param value
             */
            @Override
            public void onNext(String value) {
                mNetworkStateView.showSuccess();
                Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
                mTv.setText(value);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "onError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
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
        handleNet();
    }

    @Override
    public void netWorkStatusErrorRefresh() {
        super.netWorkStatusErrorRefresh();
        handleNet();
    }

    @Override
    public void netWorkStatusNoNetWorkRefresh() {
        super.netWorkStatusNoNetWorkRefresh();
        handleNet();
    }

}
