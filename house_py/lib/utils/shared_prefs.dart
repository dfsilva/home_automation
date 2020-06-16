import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';

dynamic myEncode(dynamic item) {
  if (item is DateTime) {
    return item.toIso8601String();
  }
  return item.toJson();
}

class Prefs {
  static const String logged_user = "LOGGED_USER";
  static const String user_token_key = "LOGGED_USER_TOKEN";

  static Future<bool> saveJson(String key, dynamic value) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.setString(key, json.encode(value, toEncodable: myEncode));
  }

  static Future<dynamic> getJson(String key) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    if (prefs.containsKey(key)) {
      return json.decode(prefs.getString(key));
    } else {
      return null;
    }
  }

  static Future<bool> saveString(String key, String value) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.setString(key, value);
  }

  static Future<dynamic> getString(String key) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    if (prefs.containsKey(key)) {
      return prefs.getString(key);
    } else {
      return null;
    }
  }

  static removeKey(String key) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.remove(key);
  }
}
