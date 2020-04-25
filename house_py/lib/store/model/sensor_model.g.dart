// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sensor_model.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$SensorModel on _SensorModel, Store {
  final _$typeAtom = Atom(name: '_SensorModel.type');

  @override
  String get type {
    _$typeAtom.context.enforceReadPolicy(_$typeAtom);
    _$typeAtom.reportObserved();
    return super.type;
  }

  @override
  set type(String value) {
    _$typeAtom.context.conditionallyRunInAction(() {
      super.type = value;
      _$typeAtom.reportChanged();
    }, _$typeAtom, name: '${_$typeAtom.name}_set');
  }

  final _$valueAtom = Atom(name: '_SensorModel.value');

  @override
  dynamic get value {
    _$valueAtom.context.enforceReadPolicy(_$valueAtom);
    _$valueAtom.reportObserved();
    return super.value;
  }

  @override
  set value(dynamic value) {
    _$valueAtom.context.conditionallyRunInAction(() {
      super.value = value;
      _$valueAtom.reportChanged();
    }, _$valueAtom, name: '${_$valueAtom.name}_set');
  }

  final _$latestValuesAtom = Atom(name: '_SensorModel.latestValues');

  @override
  ObservableList<dynamic> get latestValues {
    _$latestValuesAtom.context.enforceReadPolicy(_$latestValuesAtom);
    _$latestValuesAtom.reportObserved();
    return super.latestValues;
  }

  @override
  set latestValues(ObservableList<dynamic> value) {
    _$latestValuesAtom.context.conditionallyRunInAction(() {
      super.latestValues = value;
      _$latestValuesAtom.reportChanged();
    }, _$latestValuesAtom, name: '${_$latestValuesAtom.name}_set');
  }

  final _$_SensorModelActionController = ActionController(name: '_SensorModel');

  @override
  dynamic setValue(dynamic _value) {
    final _$actionInfo = _$_SensorModelActionController.startAction();
    try {
      return super.setValue(_value);
    } finally {
      _$_SensorModelActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    final string =
        'type: ${type.toString()},value: ${value.toString()},latestValues: ${latestValues.toString()}';
    return '{$string}';
  }
}
