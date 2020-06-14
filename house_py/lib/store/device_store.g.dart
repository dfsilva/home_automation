// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$DeviceStore on _DeviceStore, Store {
  final _$dashboardDevicesAtom = Atom(name: '_DeviceStore.dashboardDevices');

  @override
  ObservableMap<String, DeviceModel> get dashboardDevices {
    _$dashboardDevicesAtom.reportRead();
    return super.dashboardDevices;
  }

  @override
  set dashboardDevices(ObservableMap<String, DeviceModel> value) {
    _$dashboardDevicesAtom.reportWrite(value, super.dashboardDevices, () {
      super.dashboardDevices = value;
    });
  }

  final _$_DeviceStoreActionController = ActionController(name: '_DeviceStore');

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
  dynamic changeSensorValue(String deviceId, String sensor, dynamic value) {
    final _$actionInfo = _$_DeviceStoreActionController.startAction(
        name: '_DeviceStore.changeSensorValue');
    try {
      return super.changeSensorValue(deviceId, sensor, value);
    } finally {
      _$_DeviceStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    return '''
dashboardDevices: ${dashboardDevices}
    ''';
  }
}
