import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:housepy/domain/user.dart';
import 'package:housepy/store/user_store.dart';
import 'package:housepy/utils/http_utils.dart';
import 'package:housepy/utils/message.dart';
import 'package:housepy/utils/navigator.dart';
import 'package:housepy/utils/shared_prefs.dart';

class UserService {
  final UserStore store;
  final FirebaseAuth _auth;
  StreamSubscription _authSubscription;

  UserService(this.store, this._auth) {
    subscribeAuth();
  }

  subscribeAuth() {
    _authSubscription = _auth.onAuthStateChanged.listen((userData) {
      if (userData == null) {
        NavigatorUtils.nav.currentState.pushReplacementNamed("login");
      } else {
        if (store.usuario == null || store.usuario.uid == null) {
          userData.getIdToken().then((idToken) {
            Prefs.save(Prefs.user_token_key, idToken.token);
            getUserByUid(userData.uid).then((user) {
              store.setUsuario(user);
              NavigatorUtils.nav.currentState.pushReplacementNamed("home");
            }).catchError((error) {
              showErrorException("Tivemos um problema ao recuperar os dados do usuário");
              NavigatorUtils.nav.currentState.pushReplacementNamed("login");
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
    Prefs.removeKey(Prefs.user_token_key);
    Prefs.removeKey(Prefs.logged_user);
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
}
