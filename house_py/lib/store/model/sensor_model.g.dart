// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sensor_model.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$SensorModel on _SensorModel, Store {
  final _$titleAtom = Atom(name: '_SensorModel.title');

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

  final _$typeAtom = Atom(name: '_SensorModel.type');

  @override
  String get type {
    _$typeAtom.reportRead();
    return super.type;
  }

  @override
  set type(String value) {
    _$typeAtom.reportWrite(value, super.type, () {
      super.type = value;
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
title: ${title},
type: ${type},
value: ${value},
latestValues: ${latestValues}
    ''';
  }
}
