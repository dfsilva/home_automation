import 'dart:convert';

import 'package:housepy/dto/websocket.dart';
import 'package:housepy/store/device_store.dart';
import 'package:http/http.dart' as http;
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
        if(wsMessage.message.toString().contains("conectado!!!")){
          channel.sink.add(json.encode({"uids": deviceStore.dashboardDevices.keys.toList()}));
        }
      }
    });
  }

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

  void dispose() {
    channel?.sink?.close();
  }
}
