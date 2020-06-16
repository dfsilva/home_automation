class Lecture {
  final String address;
  final int sensorId;
  final String sensorType;
  final String value;

  Lecture({this.address, this.sensorId, this.sensorType, this.value});

  static fromJson(Map<String, Object> json) {
    return Lecture(
        address: json["address"], sensorId: json["sensorId"], sensorType: json["sensorType"], value: json["value"]);
  }

  Map<String, Object> toJson() {
    return {
      "address": this.address,
      "sensorId": this.sensorId,
      "sensorType": this.sensorType,
      "value": this.value.toString()
    };
  }

  @override
  String toString() {
    return 'Lecture{address: $address, sensorId: $sensorId, sensorType: $sensorType, value: $value}';
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
