package cool.obb.android;

import com.blankj.utilcode.util.Utils;

import androidx.multidex.MultiDexApplication;

public class CCApplication extends MultiDexApplication {

    private static CCApplication application;

    public static CCApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Utils.init(this);
    }


}
