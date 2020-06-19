import 'package:housepy/domain/device.dart';
import 'package:housepy/dto/websocket.dart';
import 'package:housepy/store/model/device_model.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:housepy/utils/type_utils.dart';
import 'package:mobx/mobx.dart';

part 'device_store.g.dart';

class DeviceStore = _DeviceStore with _$DeviceStore;

enum DevicesStatus { loading, loaded }

abstract class _DeviceStore with Store {
  @observable
  ObservableMap<String, DeviceModel> devices = Map<String, DeviceModel>().asObservable();

  @observable
  DevicesStatus status = DevicesStatus.loading;

  @action
  setDevices(List<Device> devices) {
    Iterable<DeviceModel> devicesModel = devices.map((device) {
      return DeviceModel(
          device: device,
          sensors: Map<String, SensorModel>.fromIterable(device.sensors.map((sensor) => SensorModel(sensor)),
              key: (element) => element.sensor.getKey(), value: (element) => element).asObservable());
    });

    Map<String, DeviceModel> mapDevices = Map<String, DeviceModel>.fromIterable(devicesModel,
        key: (element) => element.device.address, value: (element) => element);

    this.devices = mapDevices.asObservable();
    this.status = DevicesStatus.loaded;
  }


  @action
  updateLecture(Lecture _lecture) {
    SensorModel sm = devices[_lecture.address]?.sensors[_lecture.getSensorKey()];
    sm?.setValue(valueByType(sm?.sensor?.dataType, _lecture.value));
  }

  @action
  changeSensorValue(String address, String sensorKey, dynamic value) {
    devices[address].sensors[sensorKey]?.setValue(value);
  }
}
