import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:housepy/bus/actions.dart';
import 'package:housepy/domain/user.dart';
import 'package:housepy/routes.dart';
import 'package:housepy/service/base_service.dart';
import 'package:housepy/store/user_store.dart';
import 'package:housepy/utils/http_utils.dart';
import 'package:housepy/utils/message.dart';
import 'package:housepy/utils/navigator.dart';
import 'package:housepy/utils/shared_prefs.dart';

class UserService extends BaseService<UserStore> {
  final FirebaseAuth _auth;
  StreamSubscription _authSubscription;

  UserService(rxBus, this._auth) : super(rxBus, UserStore());

  subscribeAuth() async {
    _authSubscription = _auth.onAuthStateChanged.listen((userData) async {
      if (userData == null) {
        bus().send(UserLogout());
        NavigatorUtils.nav.currentState.pushReplacementNamed(Routes.LOGIN);
      } else {
        if (store().usuario == null || store().usuario.uid == null) {
          userData.getIdToken().then((idToken) async {
            await Prefs.saveString(Prefs.user_token_key, idToken.token);
            getUserByUid(userData.uid).then((user) async {
              bus().send(UserLogged(user));
              NavigatorUtils.nav.currentState.pushReplacementNamed(Routes.HOME);
            }).catchError((error) {
              showErrorException("Tivemos um problema ao recuperar os dados do usuário");
              bus().send(UserLogout());
            });
          });
        }
      }
    });
  }

  Future<AuthResult> signin(String email, String password) async {
    bus().send(ShowHud(text: "Entrando..."));
    return _auth.signInWithEmailAndPassword(email: email, password: password).whenComplete(() => bus().send(HideHud()));
  }

  Future<void> recovery(String email) async {
    return _auth.sendPasswordResetEmail(email: email);
  }

  Future<User> create(User user, String password) {
    bus().send(ShowHud(text: "Criando..."));
    _authSubscription?.cancel();
    return _auth.createUserWithEmailAndPassword(email: user.email, password: password).then((authResult) async {
      User created = user.copyWith(uid: authResult.user.uid);
      return createApiUser(created).then((value) {
        subscribeAuth();
        return created;
      });
    }).whenComplete(() => bus().send(HideHud()));
  }

  Future<void> logout() {
    return _auth.signOut();
  }

  Future<User> getUserByUid(String uid) {
    return Api.doGet(uri: "/users/$uid").then((json) => User.fromJson(json));
  }

  Future<User> createApiUser(User user) {
    return Api.doPost(uri: "/users/", bodyParams: user.toJson()).then((json) => User.fromJson(json));
  }

  void dispose() {
    _authSubscription?.cancel();
  }

  @override
  void onReceiveMessage(msg) {
    if (msg is UserLogged) {
      store().setUsuario(msg.user);
    }
    if (msg is UserLogout) {
      Prefs.removeKey(Prefs.user_token_key);
      Prefs.removeKey(Prefs.logged_user);
      store().setUsuario(null);
    }
  }
}
