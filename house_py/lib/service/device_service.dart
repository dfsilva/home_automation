import 'dart:convert';

import 'package:housepy/bus/actions.dart';
import 'package:housepy/bus/rx_bus.dart';
import 'package:housepy/domain/user.dart';
import 'package:housepy/dto/websocket.dart';
import 'package:housepy/service/base_service.dart';
import 'package:housepy/store/device_store.dart';
import 'package:housepy/utils/http_utils.dart';
import 'package:housepy/utils/message.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class DeviceService extends BaseService<DeviceStore> {
  DeviceService(RxBus rxBus) : super(rxBus, DeviceStore());

  User user;

  Future<Map> changeValue(Lecture lecture) async {
    store().changeSensorValue(lecture.id, lecture.sensor, lecture.value);
    Api.doPost(uri: "device/send", bodyParams: lecture.toJson()).catchError((error) {
      showErrorException(error);
    });
  }

  void onConected(WebSocketChannel channel) {
    channel.sink.add(json.encode({"uids": store().dashboardDevices.keys.toList()}));
  }

  void loadDevices() {
    Api.doGet(uri: "device/user/${user.uid}").then((devices) {
      print(devices);
    });
  }

  @override
  void dispose() {}

  @override
  void onReceiveMessage(msg) {
    if (msg is ReceiveLecture) {
      store().updateLecture(msg.lecture);
    }

    if (msg is UserLogged) {
      this.user = msg.user;
    }

    if (msg is WsConnected) {
      onConected(msg.channel);
    }
  }
}
