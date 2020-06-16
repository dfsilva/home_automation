import 'package:flutter/material.dart';
import 'package:housepy/service/service_locator.dart';
import 'package:housepy/service/usuario_service.dart';

class Splash extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    Services.get<UserService>(UserService).subscribeAuth();
    return Scaffold(
      body: Center(
        child: Text("HousePy", style: TextStyle(fontSize: 20)),
      ),
    );
  }
}

