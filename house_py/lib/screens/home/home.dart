import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/widgets/device_view.dart';
import 'package:provider/provider.dart';

class HomeScreen extends StatelessWidget {
  DeviceService _deviceService;

  @override
  Widget build(BuildContext context) {
    this._deviceService = Provider.of<DeviceService>(context);
    this._deviceService.connect();
    return Scaffold(
      body: Container(
        padding: EdgeInsets.symmetric(vertical: 40, horizontal: 5),
        child: Observer(
          builder: (ctx) {
            List<DeviceModel> _devices = _deviceService.deviceStore.dashboardDevices.values.toList();
            _devices.sort((d1, d2) => d1.order.compareTo(d2.order));
            return ReorderableListView(
              scrollDirection: Axis.vertical,
              onReorder: (oldIndex, newIndex) => {},
              children: _devices.map((d) => DeviceListView(key: ValueKey(d.id), device: d)).toList(),
            );
          },
        ),
      ),
    );
  }
}
