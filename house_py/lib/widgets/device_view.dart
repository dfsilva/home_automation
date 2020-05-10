import 'package:flutter/material.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/utils/colors.dart';
import 'package:housepy/widgets/sensor_view.dart';

class DeviceListView extends StatelessWidget {
  final DeviceModel device;

  const DeviceListView({Key key, this.device}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.max,
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
        Expanded(
          child: Container(
            child: ListView.separated(
                separatorBuilder: (_, i)=> Divider(
                  color: HousePyColors.dividerColor,
                  height: 1,
                  thickness: .5,
                ),
                shrinkWrap: true,
                itemCount: this.device.sensors.length,
                itemBuilder: (_, i) => SensorView(
                      sensor: this.device.sensors[i],
                      device: this.device,
                    )),
          ),
        )
      ],
    );
  }
}
