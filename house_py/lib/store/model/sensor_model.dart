import 'package:mobx/mobx.dart';

part 'sensor_model.g.dart';

class SensorModel = _SensorModel with _$SensorModel;

abstract class _SensorModel with Store {
  @observable
  String type;

  @observable
  dynamic value;

  @observable
  ObservableMap<int, dynamic> latestValues;

  _SensorModel({this.type, this.value, this.latestValues});


  @action
  setValue(dynamic _value) {
    this.value = _value;
  }

}
