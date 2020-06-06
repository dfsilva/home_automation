import 'dart:async';
import 'dart:convert';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:housepy/domain/user.dart';
import 'package:housepy/store/user_store.dart';
import 'package:housepy/utils/navigator.dart';
import 'package:shared_preferences/shared_preferences.dart';

class UserService {
  final UserStore userStore;
  FirebaseAuth _auth;
  StreamSubscription<StatusLogin> _statusLoginSubscription;
  StreamSubscription _authSubscription;
  SharedPreferences _preferences;

  UserService(this.userStore, this._auth) {
//    this._statusLoginSubscription = userStore.statusSubject.listen((value) {
//      if (value == StatusLogin.logado) {
//        NavigatorUtils.nav.currentState.pushReplacementNamed("home");
//      } else {
//        NavigatorUtils.nav.currentState.pushReplacementNamed("login");
//      }
//    });

    _authSubscription = _auth.onAuthStateChanged.listen((userData) {

    });

    SharedPreferences.getInstance().then((value) {
      _preferences = value;
    });
  }

  Future<AuthResult> signin(String email, String password) async {
    return _auth.signInWithEmailAndPassword(email: email, password: password);
  }

  Future<bool> recuperarSenha(String email) async {
    return Future.delayed(Duration(seconds: 5), () {
      return Future.value(true);
    });
  }

  Future<User> criarUsuario(String nome, String email, String senha) {
    return Future.value(User(name: nome, email: email));
  }

  Future<void> logout() {
    _preferences.remove("usuario_logado");
    userStore.setStatusLogin(StatusLogin.nao_logado);
  }

  Future<User> verificarUsuarioAutenticado() async {
    return Future.delayed(Duration(seconds: 5), () {
      String usuarioStr = _preferences.getString("usuario_logado");
      if (usuarioStr != null) {
        User usuarioLogado = User.fromJson(jsonDecode(usuarioStr));
        userStore.setUsuario(usuarioLogado);
        userStore.setStatusLogin(StatusLogin.logado);
        return usuarioLogado;
      } else {
        userStore.setStatusLogin(StatusLogin.nao_logado);
        return null;
      }
    });
  }

  void dispose() {
    _statusLoginSubscription.cancel();
  }
}
