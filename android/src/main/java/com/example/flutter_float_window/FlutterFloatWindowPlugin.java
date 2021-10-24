package com.example.flutter_float_window;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterFloatWindowPlugin
 */
public class FlutterFloatWindowPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    private Activity activity;
    private Context context;

    private static final String TAG = "FlutterFloatWindowPlugin";

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_float_window");
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + Build.VERSION.RELEASE);
                break;
            case "requestPermission":
                if (activity != null) {
                    requestFloatPermission(activity);
                } else {
                    requestFloatPermission((Activity) context);
                }
                break;
            case "isRequestFloatPermission":
                if (activity != null) {
                    result.success(isRequestFloatPermission(activity));
                } else {
                    result.success(isRequestFloatPermission((Activity) context));
                }
                break;
            case "open":
                Activity okActivity = activity != null ? activity : (Activity) context;
                EasyFloat.with(okActivity).setLayout(R.layout.item).setShowPattern(ShowPattern.ALL_TIME)
                        // 设置吸附方式，共15种模式，详情参考SidePattern
                        .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                        // 设置浮窗的标签，用于区分多个浮窗
                        .setTag("testFloat")
                        // 设置浮窗是否可拖拽
                        .setDragEnable(true)
                        // 浮窗是否包含EditText，默认不包含
                        .hasEditText(false)
                        // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
                        .setLocation(0, 300)
                        // 设置浮窗的对齐方式和坐标偏移量
                        .setGravity(Gravity.END | Gravity.CENTER_VERTICAL, 0, 200)
                        // 设置当布局大小变化后，整体view的位置对齐方式
                        .setLayoutChangedGravity(Gravity.END)
                        // 设置拖拽边界值
//                        .setBorder(100, 100,100,800)
                        // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
//                        .setMatchParent(widthMatch = false, heightMatch = false)
                        // 设置浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
//                        .setAnimator(DefaultAnimator())
                        // 设置系统浮窗的不需要显示的页面
//                        .setFilter(MainActivity::class.java, SecondActivity::class.java)
                // 设置系统浮窗的有效显示高度（不包含虚拟导航栏的高度），基本用不到，除非有虚拟导航栏适配问题
//    .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
            // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
                        .show();

                Log.e(TAG, "等待实现open");
                break;
            case "hide":
                Log.e(TAG, "等待实现hide");
                break;
            case "show":
                EasyFloat.show();
                Log.e(TAG, "等待实现show");
                break;
            case "dismiss":
                Log.e(TAG, "等待实现dismiss");
                break;
            default:
                result.notImplemented();
                break;
        }
    }


    /**
     * 申请悬浮窗权限
     *
     * @param activity activity
     */
    public static void requestFloatPermission(Activity activity) {
        jumpToSetting(activity);
    }

    /**
     * 是否有悬浮窗权限
     */
    public static boolean isRequestFloatPermission(Activity activity) {
        // 如果大于23则直接通过系统判断
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(activity);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            return checkLowVersion(activity, 24);
        }
        return true;
    }


    @TargetApi(19)
    private static boolean checkLowVersion(Context context, int i) {
        boolean z = false;
        if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class.forName(appOpsManager.getClass().getName());
                if ((Integer) appOpsManager.getClass()
                        .getDeclaredMethod("checkOp", new Class[]{Integer.TYPE, Integer.TYPE, String.class})
                        .invoke(appOpsManager, new Object[]{Integer.valueOf(i), Integer.valueOf(Binder.getCallingUid()), context.getPackageName()})
                        == 0) {
                    z = true;
                }
                return z;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }

        return false;
    }

    public static void jumpToSetting(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                toSettingFloatPermission(activity);
            } else if (TextUtils.equals("Meizu", Build.MANUFACTURER)) {
                Log.e(TAG, "jumpToSetting: " + "魅族手机");
                toMeiZuSetting(activity);
            } else if ("Oppo".equalsIgnoreCase(Build.MANUFACTURER)) {
                Log.e(TAG, "jumpToSetting: " + "Oppo手机");
                toOppoSetting(activity);
            } else if ("Xiaomi".equals(Build.MANUFACTURER)) {
                Log.e(TAG, "jumpToSetting: " + "小米手机");
                toXiaoMiSetting(activity);
            }
        } catch (Exception unused) {
            Toast.makeText(activity, "开启悬浮播放功能失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转到系统设置界面
     *
     * @param activity activity
     */
    @TargetApi(23)
    private static void toSettingFloatPermission(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("android.settings.action.MANAGE_OVERLAY_PERMISSION");
        String sb = "package:" + activity.getPackageName();
        intent.setData(Uri.parse(sb));
        activity.startActivityForResult(intent, 17);
    }

    /**
     * 魅族设置界面
     */
    private static void toMeiZuSetting(Activity activity) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", activity.getPackageName());
        activity.startActivityForResult(intent, 17);
    }

    private static void toXiaoMiSetting(Activity activity) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        if ("V5".equals(getXiaoMiVersion())) {
            PackageInfo packageInfo = null;
            try {
                packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                intent.setClassName("com.miui.securitycenter", "com.miui.securitycenter.permission.AppPermissionsEditor");
                intent.putExtra("extra_package_uid", packageInfo.applicationInfo.uid);
                activity.startActivityForResult(intent, 17);
            } catch (PackageManager.NameNotFoundException unused) {
                unused.printStackTrace();
            }
        } else if ("V6".equals(getXiaoMiVersion())) {
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", activity.getPackageName());
            activity.startActivityForResult(intent, 17);
        } else {
            Intent intent2 = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent2.setPackage("com.miui.securitycenter");
            intent2.putExtra("extra_pkgname", activity.getPackageName());
            activity.startActivityForResult(intent2, 17);
        }
    }

    /**
     * 跳转到oppo手机设置界面
     */
    private static void toOppoSetting(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity"));
        activity.startActivity(intent);
    }

    /**
     * 跳转到iqoo
     */
    private static void toIqoo(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager"));
        activity.startActivity(intent);
    }

    public static String getXiaoMiVersion() {
        String str = "null";
        if (!"Xiaomi".equals(Build.MANUFACTURER)) {
            return str;
        }
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            str = (String) cls.getDeclaredMethod("get", new Class[]{String.class, String.class}).invoke(cls, new Object[]{"ro.miui.ui.version.name", null});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        context = binding.getApplicationContext();
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        context = activity.getApplicationContext();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        context = activity.getApplicationContext();
    }

    @Override
    public void onDetachedFromActivity() {

    }
}
