import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:housepy/dto/websocket.dart';
import 'package:housepy/service/device_service.dart';
import 'package:housepy/service/service_locator.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/store/model/sensor_model.dart';

class OnOffView extends StatelessWidget {
  final SensorModel sensor;
  final DeviceModel device;
  DeviceService _deviceService = Services.get(DeviceService);

  OnOffView({Key key, this.sensor, this.device}) : super(key: key);

  _getIcon(bool value) {
    return value
        ? Icon(FontAwesomeIcons.solidLightbulb, color: Colors.yellow)
        : Icon(FontAwesomeIcons.lightbulb, color: Colors.grey);
  }

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () {
        this
            ._deviceService
            .changeValue(Lecture(address: device.device.address, sensorNumber: sensor.sensor.number, value: (!sensor.value).toString()));
      },
      child: Observer(
          builder: (ctx) => sensor.value != null
              ? Row(
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
                            sensor.sensor.name,
                            style: TextStyle(fontSize: 20),
                          )
                        ],
                      ),
                    ),
                    Switch(
                        value: sensor.value,
                        onChanged: (value) {
                          this
                              ._deviceService
                              .changeValue(Lecture(address: device.device.address, sensorNumber: sensor.sensor.number, value: value.toString()));
                        })
                  ],
                )
              : Center(
                  child: Text("Carregando..."),
                )),
    );
  }
}
