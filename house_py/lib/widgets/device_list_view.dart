import 'package:flutter/material.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/utils/colors.dart';
import 'package:housepy/widgets/SensorView.dart';

class DeviceListView extends StatelessWidget {
  final DeviceModel device;

  const DeviceListView({Key key, this.device}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(5),
      margin: EdgeInsets.symmetric(vertical: 5),
      color: HousePyColors.cardBackground,
      width: double.maxFinite,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(device.title),
          Container(
            child: Column(
              children: device.sensors.map((sensor) => SensorView(sensor: sensor)).toList(),
            ),
          )
        ],
      ),
    );
  }
}
