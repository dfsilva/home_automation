import 'dart:convert';

import 'package:housepy/dto/websocket.dart';
import 'package:housepy/store/device_store.dart';
import 'package:housepy/utils/http_utils.dart';
import 'package:housepy/utils/message.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class DeviceService {
  final DeviceStore deviceStore;

  DeviceService(this.deviceStore);

  Future<Map> changeValue(Lecture lecture) async {
    deviceStore.changeSensorValue(lecture.id, lecture.sensor, lecture.value);
    Api.doPost(uri: "device/", bodyParams: lecture.toJson()).catchError((error) {
      showErrorException(error);
    });
  }

  void onConected(WebSocketChannel channel) {
    channel.sink.add(json.encode({"uids": deviceStore.dashboardDevices.keys.toList()}));
  }

  void onReceiveLecture(Lecture lecture) {
    deviceStore.updateLecture(lecture);
  }

  void dispose() {}
}
