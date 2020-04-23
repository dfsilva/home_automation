import 'dart:async';
import 'dart:convert';

import 'package:housepy/domain/usuario.dart';
import 'package:housepy/store/usuario_store.dart';
import 'package:housepy/utils/navigator_utils.dart';
import 'package:shared_preferences/shared_preferences.dart';

class UsuarioService {
  final UsuarioStore usuarioStore;
  StreamSubscription<StatusLogin> _statusLoginSubscription;
  SharedPreferences _preferences;

  UsuarioService(this.usuarioStore) {
    this._statusLoginSubscription = usuarioStore.statusSubject.listen((value) {
      if (value == StatusLogin.logado) {
        NavigatorUtils.nav.currentState.pushReplacementNamed("home");
      } else {
        NavigatorUtils.nav.currentState.pushReplacementNamed("login");
      }
    });
    SharedPreferences.getInstance().then((value) {
      _preferences = value;
    });
  }

  Future<Usuario> entrarComEmailSenha(String email, String senha) async {
    Usuario usuarioLogado = Usuario(nome: "Diego Ferreira", email: email);
    _preferences.setString("usuario_logado", jsonEncode(usuarioLogado.toJson()));
    usuarioStore.setUsuario(usuarioLogado);
    usuarioStore.setStatusLogin(StatusLogin.logado);
    return Future.value(usuarioLogado);
  }

  Future<bool> recuperarSenha(String email) async {
    return Future.delayed(Duration(seconds: 5), () {
      return Future.value(true);
    });
  }

  Future<Usuario> criarUsuario(String nome, String email, String senha) {
    return Future.value(Usuario(nome: nome, email: email));
  }

  Future<void> logout() {
    _preferences.remove("usuario_logado");
    usuarioStore.setStatusLogin(StatusLogin.nao_logado);
  }

  Future<Usuario> verificarUsuarioAutenticado() async {
    return Future.delayed(Duration(seconds: 5), () {
      String usuarioStr = _preferences.getString("usuario_logado");
      if (usuarioStr != null) {
        Usuario usuarioLogado = Usuario.fromJson(jsonDecode(usuarioStr));
        usuarioStore.setUsuario(usuarioLogado);
        usuarioStore.setStatusLogin(StatusLogin.logado);
        return usuarioLogado;
      } else {
        usuarioStore.setStatusLogin(StatusLogin.nao_logado);
        return null;
      }
    });
  }

  void dispose() {
    _statusLoginSubscription.cancel();
  }
}
