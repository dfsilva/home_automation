import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:housepy/service/connection_service.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/service/service_locator.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/utils/colors.dart';
import 'package:housepy/widgets/device_view.dart';

class HomeScreen extends StatelessWidget {

  final ConnectionService _connectionService = Services.get<ConnectionService>(ConnectionService);
  final DeviceService _deviceService = Services.get<DeviceService>(DeviceService);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("DISPOSITIVOS"),
          centerTitle: true,
          actions: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 10),
              child: Observer(
                builder: (ctx) => _connectionService.store().connected
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
          List<DeviceModel> _devices = _deviceService.store().devices.values.toList();
          _devices.sort((d1, d2) => d1.device.order.compareTo(d2.device.order));
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
                    child: DeviceListView(key: ValueKey(_devices[i].device.id), device: _devices[i]),
                  ),
                );
              });
        }));
  }
}
