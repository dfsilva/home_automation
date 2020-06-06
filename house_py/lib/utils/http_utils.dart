import 'dart:async';
import 'dart:convert';

import 'package:housepy/utils/shared_prefs.dart';
import 'package:http/http.dart' as http;

class Api {
  static const String _URL = "http://192.168.31.45:8080/api/";

  static Future<Map<String, dynamic>> doPost(
      {String url = _URL, String uri, Map<String, dynamic> bodyParams = const {}}) async {
    return http.post(url + uri,
        body: json.encode(bodyParams),
        headers: {"Content-Type": "application/json", "Authorization": "Token ${_getUserToken()}"}).then((response) {
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception(response.reasonPhrase);
      }
    });
  }

  static Future<Map<String, dynamic>> doGet(
      {String url = _URL, String uri, Map<String, dynamic> params = const {}}) async {
    return http.get(
        url +
            uri +
            params.entries.fold("?", (previousValue, element) => "$previousValue=${element.key}=${element.value}&"),
        headers: {"Content-Type": "application/json", "Authorization": "Token ${_getUserToken()}"}).then((response) {
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception(response.reasonPhrase);
      }
    });
  }

  static _getUserToken() async => Prefs.getString(Prefs.user_token_key);
}
