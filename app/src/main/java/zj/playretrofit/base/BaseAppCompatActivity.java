package zj.playretrofit.base;

import android.view.View;
import android.view.ViewGroup;

import com.netstatus.assembly.NetObserverAppCompatActivity;


public class BaseAppCompatActivity extends NetObserverAppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        super.initNetState(rootView);//侵入代码
    }

}
