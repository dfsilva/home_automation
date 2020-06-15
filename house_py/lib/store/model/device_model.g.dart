// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_model.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$DeviceModel on _DeviceModel, Store {
  final _$deviceAtom = Atom(name: '_DeviceModel.device');

  @override
  Device get device {
    _$deviceAtom.reportRead();
    return super.device;
  }

  @override
  set device(Device value) {
    _$deviceAtom.reportWrite(value, super.device, () {
      super.device = value;
    });
  }

  final _$sensorsAtom = Atom(name: '_DeviceModel.sensors');

  @override
  ObservableList<SensorModel> get sensors {
    _$sensorsAtom.reportRead();
    return super.sensors;
  }

  @override
  set sensors(ObservableList<SensorModel> value) {
    _$sensorsAtom.reportWrite(value, super.sensors, () {
      super.sensors = value;
    });
  }

  @override
  String toString() {
    return '''
device: ${device},
sensors: ${sensors}
    ''';
  }
}
