import 'package:flutter/foundation.dart';
import 'package:housepy/domain/user.dart';
import 'package:housepy/dto/websocket.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

@immutable
class WsConnected {
  final WebSocketChannel channel;

  WsConnected(this.channel);
}

@immutable
class WsDisconected {}

@immutable
class ReceiveLecture {
  final Lecture lecture;

  ReceiveLecture(this.lecture);
}

@immutable
class UserLogged {
  final User user;

  UserLogged(this.user);
}

@immutable
class UserLogout {}

@immutable
class ShowHud {
  final String text;

  ShowHud({this.text = "Carregando..."});
}

@immutable
class HideHud {}
