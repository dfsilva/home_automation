import 'package:housepy/domain/user.dart';
import 'package:mobx/mobx.dart';
import 'package:rxdart/rxdart.dart';

part 'user_store.g.dart';

enum StatusLogin{
  carregando, logado, nao_logado
}

class UserStore = _UserStore with _$UserStore;

abstract class _UserStore with Store {
  @observable
  User usuario;

  @observable
  StatusLogin statusLogin = StatusLogin.carregando;

  BehaviorSubject<StatusLogin> statusSubject = BehaviorSubject<StatusLogin>();

  @action
  setUsuario(User _usuario) {
    this.usuario = _usuario;
  }

  @action
  setStatusLogin(StatusLogin status) {
    this.statusLogin = status;
    statusSubject.add(this.statusLogin);
  }
}


