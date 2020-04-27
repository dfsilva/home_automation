// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'connection_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$ConnectionStore on _ConnectionStore, Store {
  final _$connectedAtom = Atom(name: '_ConnectionStore.connected');

  @override
  bool get connected {
    _$connectedAtom.context.enforceReadPolicy(_$connectedAtom);
    _$connectedAtom.reportObserved();
    return super.connected;
  }

  @override
  set connected(bool value) {
    _$connectedAtom.context.conditionallyRunInAction(() {
      super.connected = value;
      _$connectedAtom.reportChanged();
    }, _$connectedAtom, name: '${_$connectedAtom.name}_set');
  }

  final _$_ConnectionStoreActionController =
      ActionController(name: '_ConnectionStore');

  @override
  dynamic setConnected(bool connected) {
    final _$actionInfo = _$_ConnectionStoreActionController.startAction();
    try {
      return super.setConnected(connected);
    } finally {
      _$_ConnectionStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    final string = 'connected: ${connected.toString()}';
    return '{$string}';
  }
}
