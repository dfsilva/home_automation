import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:housepy/store/model/sensor_model.dart';

import 'components/sparkline.dart';

class SmokeView extends StatelessWidget {
  final SensorModel sensor;

  const SmokeView({Key key, this.sensor}) : super(key: key);

  _getIcon(int temp) {
    if (temp > 400) {
      return Icon(FontAwesomeIcons.smog, color: Colors.red);
    } else {
      return Icon(FontAwesomeIcons.smog, color: Colors.green);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Observer(
        builder: (ctx) => sensor.value != null
            ? Row(
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
                                data: sensor.latestValues.map((v) => v as double).toList(), lineColor: Colors.grey)
                            : SizedBox.shrink(),
                      ),
                    ),
                  ),
                  Text(
                    "${sensor.value}",
                    style: TextStyle(fontSize: 20),
                  ),
                  const Icon(
                    Icons.chevron_right,
                    color: Colors.grey,
                    size: 30,
                  )
                ],
              )
            : Center(
                child: Text("Carregando..."),
              ));
  }
}
