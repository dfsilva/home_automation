import 'dart:convert';

import 'package:housepy/bus/actions.dart';
import 'package:housepy/bus/rx_bus.dart';
import 'package:housepy/domain/device.dart';
import 'package:housepy/domain/user.dart';
import 'package:housepy/dto/websocket.dart';
import 'package:housepy/service/base_service.dart';
import 'package:housepy/store/device_store.dart';
import 'package:housepy/utils/http_utils.dart';
import 'package:housepy/utils/message.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class DeviceService extends BaseService<DeviceStore> {
  DeviceService(RxBus rxBus) : super(rxBus, DeviceStore());

  User _loggedUser;
  WebSocketChannel _wsChannel;

  Future<Map> changeValue(Lecture lecture) async {
    store().changeSensorValue(lecture.address, lecture.getSensorKey(), lecture.value);
    Api.doPost(uri: "/device/send", bodyParams: lecture.toJson()).catchError((error) {
      showErrorException(error);
    });
  }

  void loadDevices() {
    Api.doGet(uri: "/device/user/${_loggedUser.uid}").then((devices) {
      List<Device> myDevices =
          (devices as List<dynamic>).map((m) => Device.fromJson({...m["device"], "sensors": m["sensors"]})).toList();
      bus().send(SetMyDevices(myDevices));
    });
  }

  _registerDevices() {
    if (store().devices.isNotEmpty)
      _wsChannel?.sink?.add(json.encode({"uids": store().devices.values.map((dm) => dm.device.address).toList()}));
  }

  @override
  void dispose() {}

  @override
  void onReceiveMessage(msg) {
    if (msg is ReceiveLecture) {
      store().updateLecture(msg.lecture);
    }

    if (msg is UserLogged) {
      this._loggedUser = msg.user;
      loadDevices();
    }

    if (msg is WsConnected) {
      this._wsChannel = msg.channel;
      _registerDevices();
    }

    if (msg is SetMyDevices) {
      store().setDevices(msg.devices);
      _registerDevices();
    }
  }
}
