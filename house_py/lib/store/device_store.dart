import 'package:housepy/domain/device.dart';
import 'package:housepy/dto/websocket.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:mobx/mobx.dart';

part 'device_store.g.dart';

class DeviceStore = _DeviceStore with _$DeviceStore;

enum DevicesStatus{
  loading, loaded
}

abstract class _DeviceStore with Store {
  @observable
  ObservableMap<String, DeviceModel> devices = Map<String, DeviceModel>().asObservable();

  @observable
  DevicesStatus status = DevicesStatus.loading;

  @action
  setDevices(List<Device> devices){
    Iterable<DeviceModel> devicesModel = devices.map((d) => DeviceModel(device: d, sensors: d.sensors.map((s) => SensorModel(sensor: s)).toList().asObservable())).toList();
    Map<String, DeviceModel> mapDevices = Map<String, DeviceModel>.fromIterable(devicesModel, key: (element) => element.device.address, value: (element) => element);
    this.devices = mapDevices.asObservable();
    this.status = DevicesStatus.loaded;
  }

  @action
  updateLecture(Lecture _lecture) {
    devices[_lecture.id].sensors.forEach((sensor) {
      if (sensor.sensor.id == _lecture.sensor) {
        sensor.setValue(_lecture.value);
      }
    });
  }

  @action
  changeSensorValue(String address, String sensor, dynamic value) {
    devices[address].sensors.forEach((s) {
      if (s.sensor.id == sensor) {
        s.setValue(value);
      }
    });
  }
}
