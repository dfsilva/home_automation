import 'package:housepy/domain/device.dart';
import 'package:housepy/domain/user.dart';
import 'package:mobx/mobx.dart';
import 'package:rxdart/rxdart.dart';

part 'device_store.g.dart';

//enum StatusLogin{
//  carregando, logado, nao_logado
//}

class DeviceStore = _DeviceStore with _$DeviceStore;

abstract class _DeviceStore with Store {

//  ObservableMap<Map<String, Device>> dashboardDevices;
    ObservableMap<String, Device> dashboardDevices;



//  @observable
//  StatusLogin statusLogin = StatusLogin.carregando;
//
//  BehaviorSubject<StatusLogin> statusSubject = BehaviorSubject<StatusLogin>();

//  @action
//  setUsuario(User _usuario) {
//    this.usuario = _usuario;
//  }
//
//  @action
//  setStatusLogin(StatusLogin status) {
//    this.statusLogin = status;
//    statusSubject.add(this.statusLogin);
//  }
}


