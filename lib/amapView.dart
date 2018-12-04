import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

typedef void AMapViewCreatedCallback(AMapViewController controller);

class AMapView extends StatefulWidget {
  const AMapView({
    Key key,
    this.onAMapViewCreated,
  }) : super(key: key);

  final AMapViewCreatedCallback onAMapViewCreated;

  @override
  State<StatefulWidget> createState() => _AMapViewState();
}

class _AMapViewState extends State<AMapView> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'amap_plugin.jzy.com/mapview',
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
    return Text(
        '$defaultTargetPlatform is not yet supported by the text_view plugin');
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onAMapViewCreated == null) {
      return;
    }
    widget.onAMapViewCreated(new AMapViewController._(id));
  }
}

class AMapViewController {
  AMapViewController._(int id)
      : _channel = new MethodChannel('amap_plugin.jzy.com/mapview_$id');

  final MethodChannel _channel;

  Future<void> setMarkers(List<String> markers) async {
    assert(markers != null);
    return _channel.invokeMethod('setMarkers', markers);
  }
}