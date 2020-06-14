import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:housepy/bus/actions.dart';
import 'package:housepy/domain/user.dart';
import 'package:housepy/routes.dart';
import 'package:housepy/screens/home/home.dart';
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
        bus().send(UserLogout);
        NavigatorUtils.nav.currentState.pushReplacementNamed("/login");
      } else {
        if (store().usuario == null || store().usuario.uid == null) {
          userData.getIdToken().then((idToken) async {
            await Prefs.saveString(Prefs.user_token_key, idToken.token);
            getUserByUid(userData.uid).then((user) async {
              bus().send(UserLogged(user));
              print("Entrando home.....");
              print("Entrando home.....");
              print("Entrando home.....");
              NavigatorUtils.nav.currentState.pushReplacementNamed(Routes.HOME);
            }).catchError((error) async {
              showErrorException("Tivemos um problema ao recuperar os dados do usuário");
              NavigatorUtils.nav.currentState.pushReplacementNamed(Routes.LOGIN);
//              await NavigatorUtils.nav.currentState.pushReplacementNamed(Routes.LOGIN);
            });
          });
        }
      }
    });
  }

  Future<AuthResult> signin(String email, String password) async {
    return _auth.signInWithEmailAndPassword(email: email, password: password);
  }

  Future<void> recovery(String email) async {
    return _auth.sendPasswordResetEmail(email: email);
  }

  Future<User> create(User user, String password) {
    _authSubscription?.cancel();
    return _auth.createUserWithEmailAndPassword(email: user.email, password: password).then((authResult) async {
      User created = user.copyWith(uid: authResult.user.uid);
      return createApiUser(created).then((value) {
        subscribeAuth();
        return created;
      });
    });
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
