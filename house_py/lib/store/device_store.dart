import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:mobx/mobx.dart';

part 'device_store.g.dart';

class DeviceStore = _DeviceStore with _$DeviceStore;

abstract class _DeviceStore with Store {
  @observable
  ObservableMap<String, DeviceModel> dashboardDevices = {
    "s_1": DeviceModel(
        id: "s_1",
        position: 0,
        title: "Sala",
        sensors: [
          SensorModel(type: "t", value: 25.40, latestValues: {1: 26.0, 2: 27.5}.asObservable()),
          SensorModel(type: "h", value: 70.40, latestValues: {1: 80.0, 2: 95.5}.asObservable()),
          SensorModel(type: "s", value: 200.40, latestValues: {1: 300.0, 2: 100.5}.asObservable()),
          SensorModel(type: "p", value: true, latestValues: {1: false, 2: true}.asObservable())
        ].asObservable()),
    "s_2": DeviceModel(
        id: "s_2",
        position: 1,
        title: "Quarto Luísa",
        sensors: [
          SensorModel(type: "t", value: 25.40, latestValues: {1: 26.0, 2: 27.5}.asObservable()),
          SensorModel(type: "h", value: 70.40, latestValues: {1: 80.0, 2: 95.5}.asObservable()),
          SensorModel(type: "s", value: 200.40, latestValues: {1: 300.0, 2: 100.5}.asObservable()),
          SensorModel(type: "p", value: true, latestValues: {1: false, 2: true}.asObservable())
        ].asObservable())
  }.asObservable();
}
