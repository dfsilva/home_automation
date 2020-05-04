import 'package:flutter/material.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/utils/colors.dart';
import 'package:housepy/widgets/sensor_view.dart';

class DeviceListView extends StatelessWidget {
  final DeviceModel device;

  const DeviceListView({Key key, this.device}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 5),
      color: HousePyColors.cardBackground,
      width: double.maxFinite,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 10),
            child: Text(device.title, style: TextStyle(fontSize: 30)),
          ),
          Divider(
            color: HousePyColors.dividerColor,
            height: 1,
            thickness: .5,
          ),
          Container(
            child: Column(
              children: device.sensors.map((sensor) => SensorView(sensor: sensor, device: this.device,)).toList(),
            ),
          ),
          Divider(
            color: HousePyColors.dividerColor,
            height: 3,
            thickness: 2.0,
          )
        ],
      ),
    );
  }
}