import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:housepy/widgets/components/sparkline.dart';

class PresenseView extends StatelessWidget {
  final SensorModel sensor;

  const PresenseView({Key key, this.sensor}) : super(key: key);

  _getIcon(bool value) {
    return value
        ? Icon(FontAwesomeIcons.solidUser, color: Colors.blue)
        : Icon(FontAwesomeIcons.user, color: Colors.grey);
  }

  @override
  Widget build(BuildContext context) {
    return Observer(
        builder: (ctx) => Row(
              mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _getIcon(sensor.value),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(vertical: 5, horizontal: 10),
                    child: Container(
                      color: Colors.black12,
                      height: 50,
                      child: sensor.latestValues != null
                          ? Sparkline(
                              data: sensor.latestValues.map((v) => v ? 1.0 : 0.1).toList(), lineColor: Colors.grey)
                          : SizedBox.shrink(),
                    ),
                  ),
                ),
                Text(
                  sensor.value ? "Detectada" : "Não detectada",
                  style: TextStyle(fontSize: 20),
                ),
                const Icon(
                  Icons.chevron_right,
                  color: Colors.grey,
                  size: 30,
                )
              ],
            ));
  }
}
