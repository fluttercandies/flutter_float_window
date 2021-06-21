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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
