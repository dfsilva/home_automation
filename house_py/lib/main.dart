import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:housepy/screens/home.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/service/usuario_service.dart';
import 'package:housepy/store/usuario_store.dart';
import 'package:housepy/utils/navigator_utils.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) => BotToastInit(
    child: MultiProvider(
      providers: [
        Provider<UsuarioService>(
          create: (_) => UsuarioService(UsuarioStore()),
          dispose: (ctx, usuarioService) {
            usuarioService.dispose();
          },
        ),
        Provider<DeviceService>(
          create: (_) => DeviceService(),
          dispose: (ctx, atividadeService) {
            atividadeService.dispose();
          },
        )
      ],
      child: MaterialApp(
        title: 'HomePy',
        navigatorObservers: [BotToastNavigatorObserver()],
        navigatorKey: NavigatorUtils.nav,
        theme: ThemeData(
            primarySwatch: Colors.blue,
            buttonTheme: ButtonThemeData(buttonColor: Colors.blue[700], textTheme: ButtonTextTheme.primary, height: 50)),
        initialRoute: "splash",
        routes: {
//          "splash": (context) => Splash(),
//          "login": (context) => LoginScreen(),
          "home": (context) => HomeScreen(),
//          "register": (context) => RegisterScreen(),
//          "recover": (context) => RecoverScreen(),
//          "atividade": (context) => AtividadeScreen(),
        },
      ),
    ),
  );
//    return MaterialApp(
//      title: 'Flutter Demo',
//      theme: ThemeData(
//        primarySwatch: Colors.black,
//        visualDensity: VisualDensity.adaptivePlatformDensity,
//      ),
//      home: MyHomePage(title: 'Flutter Demo Home Page'),
//    );

}


