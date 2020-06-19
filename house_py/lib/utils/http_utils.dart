import 'dart:async';

import 'package:dio/dio.dart';
import 'package:housepy/utils/shared_prefs.dart';

class Api {
  //static const String HOST = "192.168.31.45:8080";
  static const String HOST = "dfsilva.sytes.net:8180";
  static const String _URL = "http://$HOST/api";
  static Dio _dio = new Dio();

  static Future<dynamic> doPost({String url = _URL, String uri, Map<String, dynamic> bodyParams = const {}}) async {
    String currentToken = await _getUserToken();

    return _dio
        .post(url + uri,
            data: bodyParams,
            options: Options(headers: {"Content-Type": "application/json", "Authorization": "Token $currentToken"}))
        .then((response) {
      if (response.statusCode == 200) {
        return response.data;
      } else {
        throw Exception(response.statusMessage);
      }
    });
  }

  static Future<dynamic> doGet({String url = _URL, String uri, Map<String, dynamic> params = const {}}) async {
    String currentToken = await _getUserToken();
    return _dio
        .get(url + uri,
            queryParameters: params,
            options: Options(headers: {"Content-Type": "application/json", "Authorization": "Token $currentToken"}))
        .then((response) {
      if (response.statusCode == 200) {
        return response.data;
      } else {
        throw Exception(response.statusMessage);
      }
    });
  }

  static _getUserToken() async => Prefs.getString(Prefs.user_token_key);
}
