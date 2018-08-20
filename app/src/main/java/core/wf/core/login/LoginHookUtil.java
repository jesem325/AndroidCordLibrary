package core.wf.core.login;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import core.wf.core.ProxyActivity;

public class LoginHookUtil {

    private Context context;

    public LoginHookUtil(Context context) {
        this.context = context;
    }

    public void hookStartActivity() {
        try {
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefault = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefault.setAccessible(true);
            Object gDefaultObj = gDefault.get(null);
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstance = singletonClass.getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            Object iActivityManagerObj = mInstance.get(gDefaultObj);

            Class<?> iactivityManagerClass = Class.forName("android.app.IActivityManager");
            StartActivityInvocation startActivityInvocation = new StartActivityInvocation(iActivityManagerObj);

            Object oldIactivityManager = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iactivityManagerClass}, startActivityInvocation);
            //还原系统变量，第一个参数持有类对象，第二个参数要替换的对象
            mInstance.set(gDefaultObj, oldIactivityManager);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class StartActivityInvocation implements InvocationHandler {

        private Object iActivityManagerObj;

        public StartActivityInvocation(Object iActivityManagerObj) {
            this.iActivityManagerObj = iActivityManagerObj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            int index = 0;
            Intent oldIntent = null;
            //更改意图
            if ("startActivity".equals(method.getName())) {
                for (int i=0; i<args.length; i++) {
                    Object o = args[i];
                    if (o instanceof Intent) {
                        oldIntent = (Intent) args[i];
                        index = i;
                    }
                }
                Intent newIntent = new Intent();
                newIntent.putExtra("oldIntent", oldIntent);
                ComponentName componentName = new ComponentName(context,ProxyActivity.class);
                newIntent.setComponent(componentName);
                args[index] = newIntent;
            }
            return method.invoke(iActivityManagerObj, args);
        }
    }

    public void hookMH() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThread = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            Object sCurrentActivityThreadObj = sCurrentActivityThread.get(null);
            Field mH = activityThreadClass.getDeclaredField("mH");
            mH.setAccessible(true);
            Handler mHObj = (Handler) mH.get(sCurrentActivityThreadObj);
            Field mCallback = Handler.class.getDeclaredField("mCallback");
            mCallback.setAccessible(true);
            //还原系统变量，第一个参数持有类对象，第二个参数要替换的对象
            mCallback.set(mHObj, new mHImpl(mHObj));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class mHImpl implements Handler.Callback {

        private Handler mH;

        public mHImpl(Handler mH) {
            this.mH = mH;
        }

        @Override
        public boolean handleMessage(Message msg) {
            //LAUNCH_ACTIVITY ==100 即将要加载一个activity了
            if (msg.what == 100) {
                //加工 --完  一定丢给系统
                handleLuachActivity(msg);
            }

            mH.handleMessage(msg);
            return true;
        }

        private void handleLuachActivity(Message msg) {
            Object obj = msg.obj;
            try {
                Field intentField = obj.getClass().getDeclaredField("intent");
                intentField.setAccessible(true);
                Intent realIntent = (Intent) intentField.get(obj);
                Intent oldIntent = realIntent.getParcelableExtra("oldIntent");
                if (oldIntent != null) {
                    //                    集中式登录
                    SharedPreferences share = context.getSharedPreferences("feng", Context.MODE_PRIVATE);
                    if (share.getBoolean("isLogin", false)) {
                        ComponentName oldComponent = oldIntent.getComponent();
                        realIntent.setComponent(oldComponent);
                    } else {
                        ComponentName loginComponent = new ComponentName(context, LoginActivity.class);
                        realIntent.putExtra("extraIntent", oldIntent);
                        realIntent.setComponent(loginComponent);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }



}
