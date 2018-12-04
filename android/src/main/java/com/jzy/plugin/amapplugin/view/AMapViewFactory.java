package com.jzy.plugin.amapplugin.view;

import android.content.Context;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class AMapViewFactory extends PlatformViewFactory{
    private final BinaryMessenger messenger;
    private Context mContext;

    public AMapViewFactory(Context context,BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.mContext = context;
        this.messenger = messenger;
    }

    @Override
    public PlatformView create(Context context, int id, Object o) {
        return new FlutterAMapView(context,messenger,id);
    }
}
