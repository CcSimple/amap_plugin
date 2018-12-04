package com.jzy.plugin.amapplugin.view;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class FlutterAMapView implements PlatformView, MethodChannel.MethodCallHandler {
    private final TextureMapView mapView;
    private AMap mAMap;
    private boolean hasMoveLocation;
    private Location mLocation;
    private Context mContext;
    private final MethodChannel methodChannel;

    public FlutterAMapView(Context context, BinaryMessenger messenger, int id) {
        mContext = context;
        mapView = new TextureMapView(context);
        mapView.onCreate(null);
        mAMap = mapView.getMap();
        methodChannel = new MethodChannel(messenger, "amap_plugin.jzy.com/mapview_" + id);
        methodChannel.setMethodCallHandler(this);
        initMapLocation();
    }

    private void initMapLocation() {
        MyLocationStyle myLocationStyle;
        //  初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        // （1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        // 连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        // 连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
        myLocationStyle.showMyLocation(true);
        //设置定位蓝点的Style
        mAMap.setMyLocationStyle(myLocationStyle);
        // 设置默认定位按钮是否显示，非必需设置
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 控制指南针是否显示(true);
        mAMap.getUiSettings().setCompassEnabled(true);
        // 控制比例尺控件是否显示(true);
        mAMap.getUiSettings().setScaleControlsEnabled(true);
        mAMap.setMyLocationEnabled(true);
        //设置地图缩放级别 (14:500m,15:200m,16:100m)
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        // 获取定位
        mAMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.e("SimpleMapActivity", "onMyLocationChange: " + location);
                mLocation = location;
                if (!hasMoveLocation) {
                    moveLocation();
                    hasMoveLocation = true;
                }
            }
        });
    }
    private void moveLocation() {
        mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15, 0, 0)));
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        switch (methodCall.method) {
            case "setMarkers":
                setMarkers(methodCall, result);
                break;
            default:
                result.notImplemented();
        }
    }
    private void setMarkers(MethodCall call, MethodChannel.Result result) {
        Log.e("setMarkers", "setMarkers: "+call.arguments);
        ArrayList<String> Markers = (ArrayList<String>) call.arguments;
        addMarkers(Markers);
        result.success(null);
    }

    private void addMarkers(ArrayList<String> Markers) {
        ArrayList<MarkerOptions> mMarkerOptions = new ArrayList<>();
        try {
            ArrayList<JSONObject> jsonString = getMarkerList(Markers);
            int size = jsonString.size();
            for (int i = 0; i < size; i++) {
                JSONObject json = jsonString.get(i);
                mMarkerOptions.add(new MarkerOptions().position(
                        new LatLng(Double.parseDouble(json.getString("lat")),
                                Double.parseDouble(json.getString("lon"))
                        )).title(json.getString("title")));
            }
            mAMap.addMarkers(mMarkerOptions, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private ArrayList<JSONObject> getMarkerList(ArrayList<String> Markers) throws JSONException {
        ArrayList<JSONObject> jsonString = new ArrayList<>();
        int size = Markers.size();
        for (int i = 0; i < size; i++) {
            jsonString.add(new JSONObject(Markers.get(i)));
        }
        return jsonString;
    }





    @Override
    public View getView() {
        return mapView;
    }

    @Override
    public void dispose() {
        mapView.onPause();
        mapView.onDestroy();
    }
}
