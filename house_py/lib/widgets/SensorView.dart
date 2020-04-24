import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:housepy/utils/colors.dart';

class SensorView extends StatelessWidget {
  final SensorModel sensor;

  const SensorView({Key key, this.sensor}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(5),
      color: HousePyColors.cardBackground,
      child: Column(
        children: [Observer(builder: (ctx) => Text(sensor.value.toString()))],
      ),
    );
  }
}
