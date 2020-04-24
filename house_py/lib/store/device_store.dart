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
    "s_1": DeviceModel(
        id: "s_1",
        order: 1,
        title: "Sala",
        sensors: [
          SensorModel(type: DeviceTypes.TEMPERATURE, value: 25.40, latestValues: {1: 26.0, 2: 27.5}.asObservable()),
          SensorModel(type: DeviceTypes.HUMIDITY, value: 70.40, latestValues: {1: 80.0, 2: 95.5}.asObservable()),
          SensorModel(type: DeviceTypes.SMOKE, value: 200.40, latestValues: {1: 300.0, 2: 100.5}.asObservable()),
          SensorModel(type: DeviceTypes.PRESENCE, value: true, latestValues: {1: false, 2: true}.asObservable())
        ].asObservable()),
    "s_2": DeviceModel(
        id: "s_2",
        order: 2,
        title: "Quarto Filha",
        sensors: [
          SensorModel(type: DeviceTypes.TEMPERATURE, value: 25.40, latestValues: {1: 26.0, 2: 27.5}.asObservable()),
          SensorModel(type: DeviceTypes.HUMIDITY, value: 70.40, latestValues: {1: 80.0, 2: 95.5}.asObservable()),
          SensorModel(type: DeviceTypes.SMOKE, value: 200.40, latestValues: {1: 300.0, 2: 100.5}.asObservable()),
          SensorModel(type: DeviceTypes.PRESENCE, value: true, latestValues: {1: false, 2: true}.asObservable())
        ].asObservable()),
    "s_3": DeviceModel(
        id: "s_3",
        order: 3,
        title: "Quarto Casal",
        sensors: [
          SensorModel(type: DeviceTypes.TEMPERATURE, value: 25.40, latestValues: {1: 26.0, 2: 27.5}.asObservable()),
          SensorModel(type: DeviceTypes.HUMIDITY, value: 70.40, latestValues: {1: 80.0, 2: 95.5}.asObservable()),
          SensorModel(type: DeviceTypes.SMOKE, value: 200.40, latestValues: {1: 300.0, 2: 100.5}.asObservable()),
          SensorModel(type: DeviceTypes.PRESENCE, value: true, latestValues: {1: false, 2: true}.asObservable())
        ].asObservable()),
    "s_4": DeviceModel(
        id: "s_4",
        order: 4,
        title: "Escritório",
        sensors: [
          SensorModel(type: DeviceTypes.TEMPERATURE, value: 25.40, latestValues: {1: 26.0, 2: 27.5}.asObservable()),
          SensorModel(type: DeviceTypes.HUMIDITY, value: 70.40, latestValues: {1: 80.0, 2: 95.5}.asObservable()),
          SensorModel(type: DeviceTypes.SMOKE, value: 200.40, latestValues: {1: 300.0, 2: 100.5}.asObservable()),
          SensorModel(type: DeviceTypes.PRESENCE, value: true, latestValues: {1: false, 2: true}.asObservable())
        ].asObservable())
  }.asObservable();

  @action
  updateLecture(Lecture _lecture) {
    dashboardDevices[_lecture.id].sensors.forEach((sensor) {
      if (sensor.type == _lecture.sensor) {
        sensor.setValue(_lecture.value);
      }
    });
  }
}
