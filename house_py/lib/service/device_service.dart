import 'dart:convert';

import 'package:housepy/dto/websocket.dart';
import 'package:housepy/store/device_store.dart';
import 'package:web_socket_channel/io.dart';

class DeviceService {
  final DeviceStore deviceStore;

  DeviceService(this.deviceStore);

  Future<void> connect() {
    final channel = IOWebSocketChannel.connect('ws://dfsilva.sytes.net:8180/api/ws/diegofff');
    channel.stream.listen((data) {
      dynamic result = json.decode(data);
      WebSocketMessage wsMessage = WebSocketMessage.fromJson(result);
      print(wsMessage);
      if (wsMessage.message is Lecture) {
        deviceStore.updateLecture(wsMessage.message);
      }
    });
    channel.sink.add(json.encode({"uids": deviceStore.dashboardDevices.keys.toList()}));
  }

  void dispose() {}
}
