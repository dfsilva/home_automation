import 'dart:convert';

import 'package:housepy/dto/websocket.dart';
import 'package:housepy/store/device_store.dart';
import 'package:web_socket_channel/io.dart';

class DeviceService {
  final DeviceStore deviceStore;
  dynamic channel;

  DeviceService(this.deviceStore);

  Future<void> connect() {
//      channel = HtmlWebSocketChannel.connect('ws://dfsilva.sytes.net:8180/api/ws/diegofff');

    channel = IOWebSocketChannel.connect('ws://dfsilva.sytes.net:8180/api/ws/diego');
    channel.stream.listen((data) {
      dynamic result = json.decode(data);
      print(result);
      WebSocketMessage wsMessage = WebSocketMessage.fromJson(result);
      if (wsMessage.message is Lecture) {
        deviceStore.updateLecture(wsMessage.message);
      } else {
        channel.sink.add(json.encode({"uids": deviceStore.dashboardDevices.keys.toList()}));
      }
    });
  }

  void dispose() {
    channel?.sink?.close();
  }
}
