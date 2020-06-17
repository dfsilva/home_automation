import 'package:housepy/domain/device.dart';
import 'package:housepy/store/model/sensor_model.dart';
import 'package:mobx/mobx.dart';

part 'device_model.g.dart';

class DeviceModel = _DeviceModel with _$DeviceModel;

abstract class _DeviceModel with Store {
  @observable
  Device device;

  @observable
  ObservableMap<String, SensorModel> sensors;

  _DeviceModel({this.device, this.sensors});
}
