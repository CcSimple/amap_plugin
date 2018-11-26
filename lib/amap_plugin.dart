import 'dart:async';

import 'package:flutter/services.dart';

class AmapPlugin {
  static AmapPlugin _instance;
  final MethodChannel _methodChannel;
  final EventChannel _eventChannel;
  Stream<String> _onLocationFetched;

  factory AmapPlugin() {
    if (_instance == null) {
      final MethodChannel methodChannel =
        const MethodChannel('amap_plugin.jzy.com/methodchannel');
      final EventChannel eventChannel =
        const EventChannel('amap_plugin.jzy.com/eventchannel');
      _instance = AmapPlugin.private(methodChannel, eventChannel);
    }
    return _instance;
  }
  /// 初始化 AmapPlugin
  AmapPlugin.private(this._methodChannel, this._eventChannel);

  /// 开始定位
  Future<void> get startLocation {
    return _methodChannel.invokeMethod("startLocation");
  }
  /// 关闭定位
  Future<void> get stopLocation {
    return _methodChannel.invokeMethod("stopLocation");
  }
  /// 获取定位信息
  Future<String> get getLocation async {
    return await _methodChannel.invokeMethod("getLocation");
  }
  /// 定位回调
  Stream<String> get onLocationChanged {
    if (_onLocationFetched == null) {
      _onLocationFetched =
          _eventChannel.receiveBroadcastStream().map((dynamic event) => event);
    }
    return _onLocationFetched;
  }

  Future<String> get platformVersion async {
    final String version = await _methodChannel.invokeMethod('getPlatformVersion');
    return version;
  }
}
