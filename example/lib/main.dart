import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:amap_plugin/amap_plugin.dart';

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

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Plugin example app'),
        ),
        body: new Center(
          child: new Column(
            children: <Widget>[
              new Padding(padding: EdgeInsets.only(top: 20.0)),
              new Text('Running on: $_platformVersion\n'),
              new RaisedButton(
                onPressed: getLocationInfo,
                child: Text("获取定位信息",style: Theme.of(context).primaryTextTheme.button,),
                color: Theme.of(context).primaryColor,
              ),
              new Text('location info : $_locationInfo\n'),
            ],
          ),
        ),
      ),
    );
  }
}
