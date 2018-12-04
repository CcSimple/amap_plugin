package com.jzy.plugin.amapplugin.navi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class SimpleMapDelegate implements PluginRegistry.ActivityResultListener {
    private final Activity activity;
    public static final int REQUEST_SIMPLE = 614;
    private Intent mIntent;
    private Bundle mOptionsBundle;

    private MethodChannel.Result pendingResult;
    private MethodCall methodCall;

    public SimpleMapDelegate(Activity activity) {
        this.activity = activity;
        mIntent = new Intent();
        mOptionsBundle = new Bundle();

    }
    public void showSimpleMap(MethodCall call, MethodChannel.Result result){
        String title = call.argument("toolbar_title");
        Long color = call.argument("toolbar_color");
        Long widgetColor = call.argument("toolbar_width_color");
        ArrayList<String> markers = call.argument("markers");
        mOptionsBundle.putString(SimpleOptions.EXTRA_TOOL_BAR_TITLE,title);
        mOptionsBundle.putInt(SimpleOptions.EXTRA_TOOL_BAR_COLOR,color.intValue());
        mOptionsBundle.putInt(SimpleOptions.EXTRA_STATUS_BAR_COLOR,color.intValue());
        mOptionsBundle.putInt(SimpleOptions.EXTRA_TOOL_BAR_WIDGET_COLOR,widgetColor.intValue());
        mOptionsBundle.putStringArrayList(SimpleOptions.EXTRA_MARKERS,markers);
        methodCall = call;
        pendingResult = result;
        start(activity,REQUEST_SIMPLE);
    }
    public void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }
    public Intent getIntent(@NonNull Context context) {
        mIntent.setClass(context, SimpleMapActivity.class);
        mIntent.putExtras(mOptionsBundle);
        return mIntent;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }
}
