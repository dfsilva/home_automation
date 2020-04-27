import 'dart:convert';

import 'package:housepy/dto/websocket.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/store/connection_store.dart';
import 'package:web_socket_channel/io.dart';

class ConnectionService {
  final DeviceService deviceService;
  final ConnectionStore connectionStore;
  IOWebSocketChannel channel;

  ConnectionService(this.deviceService, this.connectionStore);

  Future<void> connect() {
    if (!connectionStore.connected) {
      try {
        channel?.sink?.close();
        //      channel = HtmlWebSocketChannel.connect('ws://dfsilva.sytes.net:8180/api/ws/diegofff');
        channel = IOWebSocketChannel.connect('ws://dfsilva.sytes.net:8180/api/ws/diego');
        channel.stream.listen((data) {
          dynamic result = json.decode(data);
          print(result);
          WebSocketMessage wsMessage = WebSocketMessage.fromJson(result);
          if (wsMessage.message is Lecture) {
            if(connectionStore.connected){
              deviceService.onReceiveLecture(wsMessage.message);
            }
          } else {
            if (wsMessage.message.toString().compareTo("conectado") == 0) {
              connectionStore.setConnected(true);
              deviceService.onConected(channel);
            }
          }
        }, onDone: () {
          connectionStore.setConnected(false);
          Future.delayed(Duration(seconds: 10), connect);
        });
      } catch (e) {
        connectionStore.setConnected(false);
        Future.delayed(Duration(seconds: 10), connect);
      }
    }
  }

  void dispose() {
    channel?.sink?.close();
  }
}
