import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';

showInfo(String text){
  BotToast.showText(text: text, contentColor: Colors.green[800], duration: Duration(seconds: 5), clickClose: true,
      textStyle: TextStyle(color: Colors.white));
}

showError(String text){
  BotToast.showText(text: text, contentColor: Colors.red, duration: Duration(seconds: 5), clickClose: true,
      textStyle: TextStyle(color: Colors.white));
}