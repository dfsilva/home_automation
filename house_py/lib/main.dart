import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter_progress_hud/flutter_progress_hud.dart';
import 'package:housepy/screens/auth/splash.dart';
import 'package:housepy/screens/home/home.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/service/usuario_service.dart';
import 'package:housepy/store/device_store.dart';
import 'package:housepy/store/user_store.dart';
import 'package:housepy/utils/theme.dart';
import 'package:housepy/utils/navigator.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) => BotToastInit(
        child: MultiProvider(
          providers: [
            Provider<UserService>(
              create: (_) => UserService(UserStore()),
              dispose: (ctx, userService) {
                userService.dispose();
              },
            ),
            Provider<DeviceService>(
              create: (_) => DeviceService(DeviceStore()),
              dispose: (ctx, deviceService) {
                deviceService.dispose();
              },
            )
          ],
          child: MaterialApp(
            title: 'HomePy',
            debugShowCheckedModeBanner: false,
            navigatorObservers: [BotToastNavigatorObserver()],
            navigatorKey: NavigatorUtils.nav,
            theme: HousePyTheme.buildTheme(),
            initialRoute: "home",
            builder: (ctx, widget) => ProgressHUD(
              child: widget,
            ),
            routes: {
              "splash": (context) => Splash(),
//          "login": (context) => LoginScreen(),
              "/": (context) => HomeScreen(),
//          "register": (context) => RegisterScreen(),
//          "recover": (context) => RecoverScreen(),
//          "atividade": (context) => AtividadeScreen(),
            },
          ),
        ),
      );
}
