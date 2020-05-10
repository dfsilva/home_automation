import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:housepy/service/connection_service.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/utils/colors.dart';
import 'package:housepy/widgets/device_view.dart';
import 'package:provider/provider.dart';

class HomeScreen extends StatelessWidget {
  ConnectionService _connectionService;
  DeviceService _deviceService;

  @override
  Widget build(BuildContext context) {
    this._connectionService = Provider.of<ConnectionService>(context);
    this._connectionService.connect();
    this._deviceService = Provider.of<DeviceService>(context);
    return Scaffold(
        appBar: AppBar(
          title: Text("DISPOSITIVOS"),
          centerTitle: true,
          actions: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 10),
              child: Observer(
                builder: (ctx) => _connectionService.connectionStore.connected
                    ? Icon(
                        FontAwesomeIcons.satelliteDish,
                        color: Colors.white,
                      )
                    : Icon(
                        FontAwesomeIcons.satelliteDish,
                        color: Colors.black38,
                      ),
              ),
            )
          ],
        ),
        body: Observer(builder: (ctx) {
          List<DeviceModel> _devices = _deviceService.deviceStore.dashboardDevices.values.toList();
          _devices.sort((d1, d2) => d1.order.compareTo(d2.order));
          return PageView.builder(
              controller: PageController(viewportFraction: 0.9),
              itemCount: _devices.length,
              itemBuilder: (_, i) {
                return Padding(
                  padding: EdgeInsets.symmetric(horizontal: 10, vertical: 10),
                  child: Container(
                    decoration: BoxDecoration(
                      color: HousePyColors.cardBackground,
                      borderRadius: BorderRadius.all(Radius.circular(4.0)),
                    ),
                    child: DeviceListView(key: ValueKey(_devices[i].id), device: _devices[i]),
                  ),
                );
              });
        }));
  }
}
