import 'package:housepy/domain/sensor.dart';
import 'package:mobx/mobx.dart';

part 'sensor_model.g.dart';

class SensorModel = _SensorModel with _$SensorModel;

abstract class _SensorModel with Store {

  @observable
  Sensor sensor;

  @observable
  dynamic value;

  @observable
  ObservableList<dynamic> latestValues;

  _SensorModel({this.sensor, this.value, this.latestValues});

  @action
  setValue(dynamic _value) {
    this.value = _value;
    if (latestValues == null) {
      latestValues = [_value].asObservable();
    } else {
      if (latestValues.length > 20) {
        latestValues.removeAt(0);
      }
      latestValues.add(_value);
    }
  }
}
