import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_float_window/flutter_float_window.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
  String _platformVersion = 'Unknown';

  bool isHavePermission = false;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    requestPermission(false);

    WidgetsBinding.instance.addObserver(this);
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await FlutterFloatWindow.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Future<void> requestPermission(bool isOpen) async {
    try {
      if (isOpen) {
        await FlutterFloatWindow.requestPermission();
      } else {
        isHavePermission = await FlutterFloatWindow.isRequestFloatPermission;
        if (mounted) setState(() {});
      }
    } on PlatformException {
      print('获取isHavePermission失败');
    }
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);

    if (state == AppLifecycleState.resumed) {
      requestPermission(false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('权限$isHavePermission'),
          actions: [
            ElevatedButton(
              onPressed: () {
                requestPermission(true);
              },
              child: Text('获取权限'),
            ),
            ElevatedButton(
              onPressed: () {
                requestPermission(false);
              },
              child: Text('刷新'),
            ),
          ],
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
      ),
    );
  }

  @override
  void dispose() {
    super.dispose();
    WidgetsBinding.instance.removeObserver(this);
  }
}
