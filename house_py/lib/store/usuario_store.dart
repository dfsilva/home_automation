import 'package:housepy/domain/usuario.dart';
import 'package:mobx/mobx.dart';
import 'package:rxdart/rxdart.dart';

part 'usuario_store.g.dart';

enum StatusLogin{
  carregando, logado, nao_logado
}

class UsuarioStore = _UsuarioStore with _$UsuarioStore;

abstract class _UsuarioStore with Store {
  @observable
  Usuario usuario;

  @observable
  StatusLogin statusLogin = StatusLogin.carregando;

  BehaviorSubject<StatusLogin> statusSubject = BehaviorSubject<StatusLogin>();

  @action
  setUsuario(Usuario _usuario) {
    this.usuario = _usuario;
  }

  @action
  setStatusLogin(StatusLogin status) {
    this.statusLogin = status;
    statusSubject.add(this.statusLogin);
  }
}


