import 'package:housepy/utils/constants.dart';

class Lecture {
  final String id;
  final String sensor;
  final dynamic value;

  Lecture({this.id, this.sensor, this.value});

  static fromJson(Map<String, Object> json) {
    return Lecture(id: json["id"], sensor: json["sensor"], value: getValueBySensor(json["sensor"], json["value"]));
  }

  static getValueBySensor(String sensor, String value) {
    if (SensorType.PRESENCE == sensor) {
      return value == "1" ? true : false;
    }

    if (SensorType.TEMPERATURE == sensor) {
      return double.parse(value);
    }

    if (SensorType.HUMIDITY == sensor) {
      return double.parse(value);
    }

    if (SensorType.SMOKE == sensor) {
      return double.parse(value);
    }
  }

  @override
  String toString() {
    return 'Lecture{id: $id, sensor: $sensor, value: $value}';
  }
}

class WebSocketMessage {
  final dynamic message;

  WebSocketMessage(this.message);

  static fromJson(Map<String, Object> json) {
    if (json["message"] is Map) {
      return WebSocketMessage(Lecture.fromJson(json["message"]));
    } else {
      return WebSocketMessage(json["message"]);
    }
  }

  @override
  String toString() {
    return 'WebSocketMessage{message: $message}';
  }
}
