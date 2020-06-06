import 'dart:convert';

import 'package:housepy/dto/websocket.dart';
import 'package:housepy/store/device_store.dart';
import 'package:http/http_utils.dart' as http;
import 'package:web_socket_channel/web_socket_channel.dart';

class DeviceService {
  final DeviceStore deviceStore;

  DeviceService(this.deviceStore);

  Future<Map> changeValue(Lecture lecture) async {
    deviceStore.changeSensorValue(lecture.id, lecture.sensor, lecture.value);
    return http.post("http://dfsilva.sytes.net:8180/api/device/",
        body: json.encode(lecture.toJson()), headers: {"Content-Type": "application/json"}).then((response) {
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception(response.reasonPhrase);
      }
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
