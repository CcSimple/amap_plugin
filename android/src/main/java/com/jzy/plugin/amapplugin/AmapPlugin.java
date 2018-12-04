package com.jzy.plugin.amapplugin;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.jzy.plugin.amapplugin.navi.SimpleMapDelegate;
import com.jzy.plugin.amapplugin.view.AMapViewFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** AmapPlugin */
public class AmapPlugin implements MethodCallHandler,EventChannel.StreamHandler {
  private static final String TAG = "AmapPlugin";
  private String mLocation;
  private EventChannel.EventSink mEventSink;
  // 声明AMapLocationClient类对象
  private AMapLocationClient mLocationClient = null;

  private final PluginRegistry.Registrar registrar;

  // 简单地图
  private final SimpleMapDelegate delegate;

  // 异步获取定位结果
  private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
      if (amapLocation != null) {
        if (amapLocation.getErrorCode() == 0) {
          mLocation = getLocationInfo(amapLocation);
          mEventSink.success(mLocation);
          Log.d("onLocationChanged", mLocation);
        }
      }
    }
  };
  private String getLocationInfo(AMapLocation amapLocation) {
    Map<String, String> map = new HashMap<>();
    // 经纬度
    map.put("longitude", String.valueOf(amapLocation.getLongitude()));
    map.put("latitude", String.valueOf(amapLocation.getLatitude()));
    // 省
    map.put("province", amapLocation.getProvince());
    // 城市
    map.put("city", amapLocation.getCity());
    // 城区
    map.put("district", amapLocation.getDistrict());
    // 街道
    map.put("address", amapLocation.getAddress());
    // 城市编码
    map.put("cityCode", amapLocation.getCityCode());
    // 地区编码
    map.put("addressCode", amapLocation.getAdCode());
    // 获取当前定位点的AOI信息
    map.put("poi", amapLocation.getPoiName());
    return new JSONObject(map).toString();
  }

  AmapPlugin(Registrar registrar,SimpleMapDelegate delegate) {
    this.registrar = registrar;
    this.delegate = delegate;
    //初始化定位
    mLocationClient = new AMapLocationClient(registrar.context());

    //设置定位回调监听
    mLocationClient.setLocationListener(mAMapLocationListener);
  }

  @Override
  public void onListen(Object o, EventChannel.EventSink eventSink) {
    this.mEventSink = eventSink;
  }

  @Override
  public void onCancel(Object o) {
    mLocationClient.stopLocation();
  }

  /** 注册插件 */
  public static void registerWith(Registrar registrar) {
    final MethodChannel methodChannel = new MethodChannel(registrar.messenger(), "amap_plugin.jzy.com/methodchannel");
    final EventChannel eventChannel = new EventChannel(registrar.messenger(), "amap_plugin.jzy.com/eventchannel");
    // 跳转Activity显示地图
    final SimpleMapDelegate delegate = new SimpleMapDelegate(registrar.activity());
    registrar.addActivityResultListener(delegate);
    // flutter端显示的地图
    registrar.platformViewRegistry()
            .registerViewFactory("amap_plugin.jzy.com/mapview",
                    new AMapViewFactory(registrar.activity(),registrar.messenger()));
    // 基础定位
    final AmapPlugin instance = new AmapPlugin(registrar,delegate);
    methodChannel.setMethodCallHandler(instance);
    eventChannel.setStreamHandler(instance);


  }
  /* 插件可用方法 */
  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("startLocation")) {
      // 启动定位
      mLocationClient.startLocation();
    } else if (call.method.equals("stopLocation")) {
      // 停止定位
      mLocationClient.stopLocation();
    } else if (call.method.equals("getLocation")) {
      // 返回位置信息
      result.success(mLocation);
    } else if (call.method.equals("showMap")) {
      // 跳转到地图Activity
      delegate.showSimpleMap(call,result);
    }else {
      result.notImplemented();
    }
  }
}
