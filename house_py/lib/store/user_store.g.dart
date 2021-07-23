// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$UserStore on _UserStore, Store {
  final _$usuarioAtom = Atom(name: '_UserStore.usuario');

  @override
  User get usuario {
    _$usuarioAtom.reportRead();
    return super.usuario;
  }

  @override
  set usuario(User value) {
    _$usuarioAtom.reportWrite(value, super.usuario, () {
      super.usuario = value;
    });
  }

  final _$statusLoginAtom = Atom(name: '_UserStore.statusLogin');

  @override
  StatusLogin get statusLogin {
    _$statusLoginAtom.reportRead();
    return super.statusLogin;
  }

  @override
  set statusLogin(StatusLogin value) {
    _$statusLoginAtom.reportWrite(value, super.statusLogin, () {
      super.statusLogin = value;
    });
  }

  final _$_UserStoreActionController = ActionController(name: '_UserStore');

  @override
  dynamic setUsuario(User _usuario) {
    final _$actionInfo =
        _$_UserStoreActionController.startAction(name: '_UserStore.setUsuario');
    try {
      return super.setUsuario(_usuario);
    } finally {
      _$_UserStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  dynamic setStatusLogin(StatusLogin status) {
    final _$actionInfo = _$_UserStoreActionController.startAction(
        name: '_UserStore.setStatusLogin');
    try {
      return super.setStatusLogin(status);
    } finally {
      _$_UserStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    return '''
usuario: ${usuario},
statusLogin: ${statusLogin}
    ''';
  }
}
