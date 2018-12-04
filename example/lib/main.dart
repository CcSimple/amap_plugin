import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:convert' show json;

import 'package:flutter/services.dart';
import 'package:amap_plugin/amap_plugin.dart';
import 'package:amap_plugin/amapView.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _locationInfo = 'Unknown';
  AmapPlugin _aMapPlugin = AmapPlugin();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  /// 插件开发 默认的 获取手机平台方法
  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await _aMapPlugin.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;
    setState(() {
      _platformVersion = platformVersion;
    });
  }

  getLocationInfo() async {
    _aMapPlugin.startLocation;
    _locationInfo = await _aMapPlugin.getLocation;
    _aMapPlugin.stopLocation;
    setState(() {});
  }

  showMap() async {
    List<String> markers = new List();
    markers.add(getJson("104.061487", "30.670596", "八宝大酒店"));
    markers.add(getJson("104.061691", "30.671551", "家乐福"));
    markers.add(getJson("104.063869", "30.671662", "成都市第五幼儿园"));
    markers.add(getJson("104.064657", "30.669161", "罗马国际"));
    markers.add(getJson("104.065333", "30.671773", "曙馨苑"));
    _aMapPlugin.showMap(
        toolbarColor: Theme.of(context).primaryColor,
        toolbarWidgetColor: Theme.of(context).primaryTextTheme.button.color,
        toolbarTitle: '附近店家',
        markers: markers);
  }

  getJson(String lon, String lat, String title) {
    return json.encode({
      "lon": lon,
      "lat": lat,
      "title": title,
    });
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Plugin example app'),
        ),
        body:
//        _buildLayout(),
        new Center(
          child: new Column(
            children: <Widget>[
              new Padding(padding: EdgeInsets.only(top: 20.0)),
              new Text('Running on: $_platformVersion\n'),
              new RaisedButton(
                onPressed: getLocationInfo,
                child: Text(
                  "获取定位信息",
                  style: Theme.of(context).primaryTextTheme.button,
                ),
                color: Theme.of(context).primaryColor,
              ),
              new Text('location info : $_locationInfo\n'),
              new RaisedButton(
                onPressed: showMap,
                child: Text(
                  "显示地图",
                  style: Theme.of(context).primaryTextTheme.button,
                ),
                color: Theme.of(context).primaryColor,
              ),
              new SizedBox(
                  height: 200.0,
                  child: new AMapView(
                    onAMapViewCreated: _onAMapViewCreated,
                  ))
            ],
          ),
        ),
      ),
    );
  }
  _buildLayout(){
    return new AMapView(
      onAMapViewCreated: _onAMapViewCreated,
    );
  }

  void _onAMapViewCreated(AMapViewController controller) {
    List<String> markers = new List();
    markers.add(getJson("104.061487", "30.670596", "八宝大酒店"));
    markers.add(getJson("104.061691", "30.671551", "家乐福"));
    markers.add(getJson("104.063869", "30.671662", "成都市第五幼儿园"));
    markers.add(getJson("104.064657", "30.669161", "罗马国际"));
    markers.add(getJson("104.065333", "30.671773", "曙馨苑"));
    controller.setMarkers(markers);
  }
}
