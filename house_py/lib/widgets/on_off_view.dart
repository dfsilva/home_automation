import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:flutter_progress_hud/flutter_progress_hud.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:housepy/dto/websocket.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:provider/provider.dart';

class OnOffView extends StatelessWidget {
  final SensorModel sensor;
  final DeviceModel device;
  DeviceService _deviceService;

  OnOffView({Key key, this.sensor, this.device}) :super(key: key);

  _getIcon(bool value) {
    return value
        ? Icon(FontAwesomeIcons.solidLightbulb, color: Colors.yellow)
        : Icon(FontAwesomeIcons.lightbulb, color: Colors.grey);
  }

  @override
  Widget build(BuildContext context) {
    this._deviceService = Provider.of<DeviceService>(context);

    return InkWell(
      onTap: () {
        this._deviceService.changeValue(Lecture(id: device.id, sensor: sensor.type, value: !sensor.value));
      },
      child: Observer(
          builder: (ctx) =>
              Row(
                mainAxisSize: MainAxisSize.max,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Container(
                    height: 50,
                    child: Row(
                      children: [
                        _getIcon(sensor.value),
                        SizedBox(width: 5),
                        Text(
                          sensor.title,
                          style: TextStyle(fontSize: 20),
                        )
                      ],
                    ),
                  ),
                  Switch(
                    value: sensor.value,
                    onChanged: (value) {
                        this._deviceService.changeValue(Lecture(id: device.id, sensor: sensor.type, value: value));
                    }
                  )
                ],
              )),
    );
  }
}
