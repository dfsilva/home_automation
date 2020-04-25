import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:housepy/utils/constants.dart';
import 'package:housepy/widgets/HumditityView.dart';
import 'package:housepy/widgets/SmokeView.dart';
import 'package:housepy/widgets/TemperatureView.dart';
import 'package:housepy/widgets/presense_view.dart';

class SensorView extends StatelessWidget {
  final SensorModel sensor;

  const SensorView({Key key, this.sensor}) : super(key: key);

  Widget getViewByType(String type) {
    if (type == SensorType.TEMPERATURE) {
      return TemperatureView(sensor: this.sensor);
    }
    if (type == SensorType.HUMIDITY) {
      return HumidityView(sensor: this.sensor);
    }
    if (type == SensorType.SMOKE) {
      return SmokeView(sensor: this.sensor);
    }
    if (type == SensorType.PRESENCE) {
      return PresenseView(sensor: this.sensor);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(5),
      child: Observer(
        builder: (ctx) => Column(children: [Text(sensor.value.toString())]),
      ),
    );
  }
}
