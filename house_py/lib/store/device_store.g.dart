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
    _$dashboardDevicesAtom.context.enforceReadPolicy(_$dashboardDevicesAtom);
    _$dashboardDevicesAtom.reportObserved();
    return super.dashboardDevices;
  }

  @override
  set dashboardDevices(ObservableMap<String, DeviceModel> value) {
    _$dashboardDevicesAtom.context.conditionallyRunInAction(() {
      super.dashboardDevices = value;
      _$dashboardDevicesAtom.reportChanged();
    }, _$dashboardDevicesAtom, name: '${_$dashboardDevicesAtom.name}_set');
  }

  @override
  String toString() {
    final string = 'dashboardDevices: ${dashboardDevices.toString()}';
    return '{$string}';
  }
}
