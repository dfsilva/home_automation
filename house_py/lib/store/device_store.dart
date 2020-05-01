import 'package:housepy/dto/websocket.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:housepy/utils/constants.dart';
import 'package:mobx/mobx.dart';

part 'device_store.g.dart';

class DeviceStore = _DeviceStore with _$DeviceStore;

abstract class _DeviceStore with Store {
  @observable
  ObservableMap<String, DeviceModel> dashboardDevices = {
    "s_10": DeviceModel(
        id: "s_10",
        order: 1,
        title: "Quarto Casal",
        sensors: [
          SensorModel(type: SensorType.TEMPERATURE, value: 0.0),
          SensorModel(type: SensorType.HUMIDITY, value: 0.0),
          SensorModel(type: SensorType.SMOKE, value: 0.0),
          SensorModel(type: SensorType.PRESENCE, value: false),
          SensorModel(title: "Humidificador", type: SensorType.LIGA_DESLIGA, value: false)
        ].asObservable()),
    "s_11": DeviceModel(
        id: "s_11",
        order: 2,
        title: "Sala",
        sensors: [
          SensorModel(type: SensorType.TEMPERATURE, value: 0.0),
          SensorModel(type: SensorType.HUMIDITY, value: 0.0),
          SensorModel(type: SensorType.SMOKE, value: 0.0),
          SensorModel(type: SensorType.PRESENCE, value: false),
          SensorModel(title: "Portao Saida", type: "op1", value: false),
          SensorModel(title: "Portao Entrada", type: "op2", value: false)
        ].asObservable()),
    "s_12": DeviceModel(
        id: "s_12",
        order: 3,
        title: "Quarto Filha",
        sensors: [
          SensorModel(type: SensorType.TEMPERATURE, value: 0.0),
          SensorModel(type: SensorType.HUMIDITY, value: 0.0),
          SensorModel(type: SensorType.SMOKE, value: 0.0),
          SensorModel(type: SensorType.PRESENCE, value: false)
        ].asObservable()),
  }.asObservable();

  @action
  updateLecture(Lecture _lecture) {
    dashboardDevices[_lecture.id].sensors.forEach((sensor) {
      if (sensor.type == _lecture.sensor) {
        sensor.setValue(_lecture.value);
      }
    });
  }

  @action
  changeSensorValue(String deviceId, String sensor, dynamic value) {
    dashboardDevices[deviceId].sensors.forEach((s) {
      if (s.type == sensor) {
        s.setValue(value);
      }
    });
  }
}
