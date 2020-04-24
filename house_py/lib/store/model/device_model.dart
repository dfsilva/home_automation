import 'package:housepy/store/model/sensor_model.dart';
import 'package:mobx/mobx.dart';

part 'device_model.g.dart';

class DeviceModel = _DeviceModel with _$DeviceModel;

abstract class _DeviceModel with Store {

  @observable
  String id;

  @observable
  String title;

  @observable
  int position;

  @observable
  ObservableList<SensorModel> sensors;

  _DeviceModel({this.id, this.title, this.position, this.sensors});

}
