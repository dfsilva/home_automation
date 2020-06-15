// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$DeviceStore on _DeviceStore, Store {
  final _$devicesAtom = Atom(name: '_DeviceStore.devices');

  @override
  ObservableMap<String, DeviceModel> get devices {
    _$devicesAtom.reportRead();
    return super.devices;
  }

  @override
  set devices(ObservableMap<String, DeviceModel> value) {
    _$devicesAtom.reportWrite(value, super.devices, () {
      super.devices = value;
    });
  }

  final _$statusAtom = Atom(name: '_DeviceStore.status');

  @override
  DevicesStatus get status {
    _$statusAtom.reportRead();
    return super.status;
  }

  @override
  set status(DevicesStatus value) {
    _$statusAtom.reportWrite(value, super.status, () {
      super.status = value;
    });
  }

  final _$_DeviceStoreActionController = ActionController(name: '_DeviceStore');

  @override
  dynamic setDevices(List<Device> devices) {
    final _$actionInfo = _$_DeviceStoreActionController.startAction(
        name: '_DeviceStore.setDevices');
    try {
      return super.setDevices(devices);
    } finally {
      _$_DeviceStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  dynamic updateLecture(Lecture _lecture) {
    final _$actionInfo = _$_DeviceStoreActionController.startAction(
        name: '_DeviceStore.updateLecture');
    try {
      return super.updateLecture(_lecture);
    } finally {
      _$_DeviceStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  dynamic changeSensorValue(String address, String sensor, dynamic value) {
    final _$actionInfo = _$_DeviceStoreActionController.startAction(
        name: '_DeviceStore.changeSensorValue');
    try {
      return super.changeSensorValue(address, sensor, value);
    } finally {
      _$_DeviceStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    return '''
devices: ${devices},
status: ${status}
    ''';
  }
}
