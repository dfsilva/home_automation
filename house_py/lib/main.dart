import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter_progress_hud/flutter_progress_hud.dart';
import 'package:housepy/screens/auth/splash.dart';
import 'package:housepy/screens/home/home.dart';
import 'package:housepy/service/connection_service.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/service/usuario_service.dart';
import 'package:housepy/store/connection_store.dart';
import 'package:housepy/store/device_store.dart';
import 'package:housepy/store/user_store.dart';
import 'package:housepy/utils/navigator.dart';
import 'package:housepy/utils/theme.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) => MultiProvider(
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
          ),
          ProxyProvider<DeviceService, ConnectionService>(
            update: (_, deviceService, __) => ConnectionService(deviceService, ConnectionStore()),
          ),
        ],
        child: MaterialApp(
          title: 'HomePy',
          debugShowCheckedModeBanner: false,
          navigatorObservers: [BotToastNavigatorObserver()],
          navigatorKey: NavigatorUtils.nav,
          theme: HousePyTheme.buildTheme(),
          initialRoute: "home",
          builder: (ctx, widget) => ProgressHUD(
            child: BotToastInit()(ctx, widget),
          ),
          routes: {
            "splash": (context) => Splash(),
            "/": (context) => HomeScreen(),
          },
        ),
      );
}
