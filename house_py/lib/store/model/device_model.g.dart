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
    _$idAtom.context.enforceReadPolicy(_$idAtom);
    _$idAtom.reportObserved();
    return super.id;
  }

  @override
  set id(String value) {
    _$idAtom.context.conditionallyRunInAction(() {
      super.id = value;
      _$idAtom.reportChanged();
    }, _$idAtom, name: '${_$idAtom.name}_set');
  }

  final _$titleAtom = Atom(name: '_DeviceModel.title');

  @override
  String get title {
    _$titleAtom.context.enforceReadPolicy(_$titleAtom);
    _$titleAtom.reportObserved();
    return super.title;
  }

  @override
  set title(String value) {
    _$titleAtom.context.conditionallyRunInAction(() {
      super.title = value;
      _$titleAtom.reportChanged();
    }, _$titleAtom, name: '${_$titleAtom.name}_set');
  }

  final _$positionAtom = Atom(name: '_DeviceModel.position');

  @override
  int get position {
    _$positionAtom.context.enforceReadPolicy(_$positionAtom);
    _$positionAtom.reportObserved();
    return super.position;
  }

  @override
  set position(int value) {
    _$positionAtom.context.conditionallyRunInAction(() {
      super.position = value;
      _$positionAtom.reportChanged();
    }, _$positionAtom, name: '${_$positionAtom.name}_set');
  }

  final _$sensorsAtom = Atom(name: '_DeviceModel.sensors');

  @override
  ObservableList<SensorModel> get sensors {
    _$sensorsAtom.context.enforceReadPolicy(_$sensorsAtom);
    _$sensorsAtom.reportObserved();
    return super.sensors;
  }

  @override
  set sensors(ObservableList<SensorModel> value) {
    _$sensorsAtom.context.conditionallyRunInAction(() {
      super.sensors = value;
      _$sensorsAtom.reportChanged();
    }, _$sensorsAtom, name: '${_$sensorsAtom.name}_set');
  }

  @override
  String toString() {
    final string =
        'id: ${id.toString()},title: ${title.toString()},position: ${position.toString()},sensors: ${sensors.toString()}';
    return '{$string}';
  }
}
