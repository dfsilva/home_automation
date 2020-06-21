import 'dart:convert';

import 'package:housepy/bus/actions.dart';
import 'package:housepy/dto/websocket.dart';
import 'package:housepy/service/base_service.dart';
import 'package:housepy/store/connection_store.dart';
import 'package:housepy/utils/http_utils.dart';
import 'package:web_socket_channel/html.dart';
//import 'package:web_socket_channel/io.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class ConnectionService extends BaseService<ConnectionStore> {
  WebSocketChannel _channel;

  ConnectionService(rxBus) : super(rxBus, ConnectionStore());

  Future<void> connect(String uid) {
    if (!store().connected) {
      try {
        _channel?.sink?.close();
              _channel = HtmlWebSocketChannel.connect('ws://${Api.HOST}/api/ws/$uid');
//        _channel = IOWebSocketChannel.connect('ws://${Api.HOST}/api/ws/$uid');
        _channel.stream.listen((data) {
          dynamic result = json.decode(data);
          print(result);
          WebSocketMessage wsMessage = WebSocketMessage.fromJson(result);
          if (wsMessage.message is Lecture) {
            if (store().connected) {
              bus().send(ReceiveLecture(wsMessage.message));
            }
          } else {
            if (wsMessage.message.toString().compareTo("conectado") == 0) {
              bus().send(WsConnected(_channel));
            }
          }
        }, onDone: () {
          bus().send(WsDisconected());
          Future.delayed(Duration(seconds: 10), () => connect(uid));
        });
      } catch (e) {
        bus().send(WsDisconected());
        Future.delayed(Duration(seconds: 10), () => connect(uid));
      }
    }
  }

  void close() {
    _channel?.sink?.close();
  }

  @override
  void dispose() {
    close();
  }

  @override
  bool filter(event) => [WsDisconected, WsConnected, UserLogged, UserLogout].contains(event.runtimeType);

  @override
  void onReceiveMessage(msg) {
    if (msg is WsDisconected) {
      store().setConnected(false);
    }
    if (msg is WsConnected) {
      store().setConnected(true);
    }
    if (msg is UserLogged) {
      connect(msg.user.uid);
    }
    if (msg is UserLogout) {
      close();
    }
  }
}
