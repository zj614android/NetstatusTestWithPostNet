package zj.playretrofit;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import zj.playretrofit.base.BaseAppCompatActivity;

public class FragmentContainerActivity extends BaseAppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        TestFragment tf = new TestFragment();
        trans.replace(R.id.content_framelayout, tf);
        trans.commit();
    }
}
