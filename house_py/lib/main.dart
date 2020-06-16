import 'package:bot_toast/bot_toast.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:housepy/bus/rx_bus.dart';
import 'package:housepy/parent.dart';
import 'package:housepy/routes.dart';
import 'package:housepy/screens/auth/login.dart';
import 'package:housepy/screens/auth/recover.dart';
import 'package:housepy/screens/auth/splash.dart';
import 'package:housepy/screens/home/home.dart';
import 'package:housepy/service/connection_service.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/service/hud_service.dart';
import 'package:housepy/service/service_locator.dart';
import 'package:housepy/service/usuario_service.dart';
import 'package:housepy/utils/navigator.dart';
import 'package:housepy/utils/theme.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  final _rxBus = new RxBus();
  Services.add(UserService(_rxBus, FirebaseAuth.instance));
  Services.add(DeviceService(_rxBus));
  Services.add(ConnectionService(_rxBus));
  Services.add(HudService(_rxBus));

  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) => MaterialApp(
        title: 'HousePy',
        debugShowCheckedModeBanner: false,
        navigatorObservers: [BotToastNavigatorObserver()],
        navigatorKey: NavigatorUtils.nav,
        theme: HousePyTheme.buildTheme(),
        routes: {
          Routes.SPLASH: (context) => Splash(),
          Routes.HOME: (context) => HomeScreen(),
          Routes.LOGIN: (context) => LoginScreen(),
          Routes.RECOVER: (context) => RecoverScreen(),
        },
        builder: (ctx, widget) => BotToastInit()(ctx, ParentWidget(widget)),
        initialRoute: Routes.SPLASH,
      );
}
