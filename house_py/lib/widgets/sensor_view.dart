import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:housepy/utils/constants.dart';
import 'package:housepy/widgets/humidity_view.dart';
import 'package:housepy/widgets/on_off_view.dart';
import 'package:housepy/widgets/presense_view.dart';
import 'package:housepy/widgets/smoke_view.dart';
import 'package:housepy/widgets/temperature_view.dart';

class SensorView extends StatelessWidget {
  final SensorModel sensor;
  final DeviceModel device;

  const SensorView({Key key, this.sensor, this.device}) : super(key: key);

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
    if(type == SensorType.LIGA_DESLIGA){
      return OnOffView(sensor: this.sensor, device: this.device);
    }

    if(type == SensorType.LIGA_DESLIGA){
      return OnOffView(sensor: this.sensor, device: this.device);
    }

    if(type == "op1"){
      return OnOffView(sensor: this.sensor, device: this.device);
    }

    if(type == "op2"){
      return OnOffView(sensor: this.sensor, device: this.device);
    }

    return Column(children: [Text(sensor.value.toString())]);
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(5),
      child: Observer(builder: (ctx) => getViewByType(this.sensor.type)),
    );
  }
}
