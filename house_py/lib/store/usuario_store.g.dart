// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'usuario_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$UsuarioStore on _UsuarioStore, Store {
  final _$usuarioAtom = Atom(name: '_UsuarioStore.usuario');

  @override
  Usuario get usuario {
    _$usuarioAtom.context.enforceReadPolicy(_$usuarioAtom);
    _$usuarioAtom.reportObserved();
    return super.usuario;
  }

  @override
  set usuario(Usuario value) {
    _$usuarioAtom.context.conditionallyRunInAction(() {
      super.usuario = value;
      _$usuarioAtom.reportChanged();
    }, _$usuarioAtom, name: '${_$usuarioAtom.name}_set');
  }

  final _$statusLoginAtom = Atom(name: '_UsuarioStore.statusLogin');

  @override
  StatusLogin get statusLogin {
    _$statusLoginAtom.context.enforceReadPolicy(_$statusLoginAtom);
    _$statusLoginAtom.reportObserved();
    return super.statusLogin;
  }

  @override
  set statusLogin(StatusLogin value) {
    _$statusLoginAtom.context.conditionallyRunInAction(() {
      super.statusLogin = value;
      _$statusLoginAtom.reportChanged();
    }, _$statusLoginAtom, name: '${_$statusLoginAtom.name}_set');
  }

  final _$_UsuarioStoreActionController =
      ActionController(name: '_UsuarioStore');

  @override
  dynamic setUsuario(Usuario _usuario) {
    final _$actionInfo = _$_UsuarioStoreActionController.startAction();
    try {
      return super.setUsuario(_usuario);
    } finally {
      _$_UsuarioStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  dynamic setStatusLogin(StatusLogin status) {
    final _$actionInfo = _$_UsuarioStoreActionController.startAction();
    try {
      return super.setStatusLogin(status);
    } finally {
      _$_UsuarioStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    final string =
        'usuario: ${usuario.toString()},statusLogin: ${statusLogin.toString()}';
    return '{$string}';
  }
}
