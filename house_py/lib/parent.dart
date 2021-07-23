import 'package:flutter/material.dart';
import 'package:flutter_hud/flutter_hud.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:housepy/service/hud_service.dart';
import 'package:housepy/service/service_locator.dart';
import 'package:housepy/store/hud_store.dart';

class ParentWidget extends StatefulWidget {
  ParentWidget(
    this.child, {
    Key key,
  }) : super(key: key);

  final Widget child;

  @override
  State createState() => new ParentWidgetState();
}

class ParentWidgetState extends State<ParentWidget> {
  static ParentWidget of(BuildContext context) => context.findAncestorWidgetOfExactType<ParentWidget>();

  HudStore _store = Services.get<HudService>(HudService).store();

  @override
  Widget build(BuildContext context) {
    return Observer(builder: (ctx) => WidgetHUD(builder: (__) => widget.child, showHUD: _store.loading));
  }
}
