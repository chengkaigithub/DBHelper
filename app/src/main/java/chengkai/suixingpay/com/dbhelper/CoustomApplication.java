package chengkai.suixingpay.com.dbhelper;

import android.app.Application;

import com.chengkai.helper.globalcrashhelper.GlobalCrashHelper;


/**
 * Created by chengkai on 2017/8/14.
 */

public class CoustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalCrashHelper.getInstance().init(this);
    }
}
