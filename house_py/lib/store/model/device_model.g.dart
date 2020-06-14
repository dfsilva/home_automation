// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_model.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$DeviceModel on _DeviceModel, Store {
  final _$idAtom = Atom(name: '_DeviceModel.id');

  @override
  String get id {
    _$idAtom.reportRead();
    return super.id;
  }

  @override
  set id(String value) {
    _$idAtom.reportWrite(value, super.id, () {
      super.id = value;
    });
  }

  final _$titleAtom = Atom(name: '_DeviceModel.title');

  @override
  String get title {
    _$titleAtom.reportRead();
    return super.title;
  }

  @override
  set title(String value) {
    _$titleAtom.reportWrite(value, super.title, () {
      super.title = value;
    });
  }

  final _$orderAtom = Atom(name: '_DeviceModel.order');

  @override
  int get order {
    _$orderAtom.reportRead();
    return super.order;
  }

  @override
  set order(int value) {
    _$orderAtom.reportWrite(value, super.order, () {
      super.order = value;
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
id: ${id},
title: ${title},
order: ${order},
sensors: ${sensors}
    ''';
  }
}
