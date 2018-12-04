package com.jzy.plugin.amapplugin.navi;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.jzy.plugin.amapplugin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author CC
 */
public class SimpleMapActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private AMap mAMap;
    private boolean hasMoveLocation;
    private Location mLocation;
    ArrayList<String> mMarkers;
    private ArrayList<MarkerOptions> mMarkerOptions;
    private Intent mIntent;
    private Context mContext;
    // 页面参数
    private int mStatusBarColor;
    private int mToolbarColor;
    private int mToolbarWidgetColor;
    private String mToolbarTitle;
    @DrawableRes
    private int mToolbarCancelDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_map);
        mIntent = getIntent();
        mContext = this;
        initOptions(mIntent);
        initMap(savedInstanceState);
    }

    private void initOptions(@NonNull Intent intent) {
        mStatusBarColor = intent.getIntExtra(SimpleOptions.EXTRA_STATUS_BAR_COLOR, Color.BLACK);
        mToolbarColor = intent.getIntExtra(SimpleOptions.EXTRA_TOOL_BAR_COLOR, Color.WHITE);
        mToolbarWidgetColor = intent.getIntExtra(SimpleOptions.EXTRA_TOOL_BAR_WIDGET_COLOR, Color.BLACK);
        mToolbarCancelDrawable = intent.getIntExtra(SimpleOptions.EXTRA_TOOL_BAR_DRAWABLE, R.drawable.ic_left_black);
        mToolbarTitle = intent.getStringExtra(SimpleOptions.EXTRA_TOOL_BAR_TITLE);
        mToolbarTitle = mToolbarTitle != null ? mToolbarTitle : "地图";
        mMarkers = intent.getStringArrayListExtra(SimpleOptions.EXTRA_MARKERS);
        mMarkers = mMarkers != null ? mMarkers : new ArrayList<String>();
        initToolBar();
    }

    private void initToolBar() {
        setStatusBarColor(mStatusBarColor);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(mToolbarColor);
        toolbar.setTitleTextColor(mToolbarWidgetColor);
        final TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTextColor(mToolbarWidgetColor);
        toolbarTitle.setText(mToolbarTitle);
//        // Color buttons inside the Toolbar
        Drawable stateButtonDrawable = ContextCompat.getDrawable(mContext, mToolbarCancelDrawable);
        stateButtonDrawable.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(stateButtonDrawable);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initMap(Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        initMapLocation();
        addMarkers();
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

    private void addMarkers() {
        mMarkerOptions = new ArrayList<>();
        try {
            ArrayList<JSONObject> jsonString = getMarkerList();
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
    private ArrayList<JSONObject> getMarkerList() throws JSONException {
        ArrayList<JSONObject> jsonString = new ArrayList<>();
        int size = mMarkers.size();
        for (int i = 0; i < size; i++) {
            jsonString.add(new JSONObject(mMarkers.get(i)));
        }
        return jsonString;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
