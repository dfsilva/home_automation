import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

showInfo(String text){
  BotToast.showText(text: text, contentColor: Colors.green[800], duration: Duration(seconds: 5), clickClose: true,
      textStyle: TextStyle(color: Colors.white));
}

showError(String text){
  BotToast.showText(text: text, contentColor: Colors.red, duration: Duration(seconds: 5), clickClose: true,
      textStyle: TextStyle(color: Colors.white));
}

showErrorException(dynamic error, {String message}) {
  if (error is PlatformException) showError("$message ${error.message}");
  else if (error is Exception)
    showError("$message ${error.toString()}");
  else
    showError(message);
}