// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sensor_model.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$SensorModel on _SensorModel, Store {
  final _$sensorAtom = Atom(name: '_SensorModel.sensor');

  @override
  Sensor get sensor {
    _$sensorAtom.reportRead();
    return super.sensor;
  }

  @override
  set sensor(Sensor value) {
    _$sensorAtom.reportWrite(value, super.sensor, () {
      super.sensor = value;
    });
  }

  final _$valueAtom = Atom(name: '_SensorModel.value');

  @override
  dynamic get value {
    _$valueAtom.reportRead();
    return super.value;
  }

  @override
  set value(dynamic value) {
    _$valueAtom.reportWrite(value, super.value, () {
      super.value = value;
    });
  }

  final _$latestValuesAtom = Atom(name: '_SensorModel.latestValues');

  @override
  ObservableList<dynamic> get latestValues {
    _$latestValuesAtom.reportRead();
    return super.latestValues;
  }

  @override
  set latestValues(ObservableList<dynamic> value) {
    _$latestValuesAtom.reportWrite(value, super.latestValues, () {
      super.latestValues = value;
    });
  }

  final _$_SensorModelActionController = ActionController(name: '_SensorModel');

  @override
  dynamic setValue(dynamic _value) {
    final _$actionInfo = _$_SensorModelActionController.startAction(
        name: '_SensorModel.setValue');
    try {
      return super.setValue(_value);
    } finally {
      _$_SensorModelActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    return '''
sensor: ${sensor},
value: ${value},
latestValues: ${latestValues}
    ''';
  }
}
