class Lecture {
  final String address;
  final int sensorNumber;
  final String sensorType;
  final String value;

  Lecture({this.address, this.sensorNumber, this.sensorType, this.value});

  static fromJson(Map<String, Object> json) {
    return Lecture(
        address: json["address"], sensorNumber: json["sensorNumber"], sensorType: json["sensorType"], value: json["value"]);
  }

  Map<String, Object> toJson() {
    return {
      "address": this.address,
      "sensorNumber": this.sensorNumber,
      "sensorType": this.sensorType,
      "value": this.value.toString()
    };
  }

  @override
  String toString() {
    return 'Lecture{address: $address, sensorNumber: $sensorNumber, sensorType: $sensorType, value: $value}';
  }

  String getSensorKey(){
    return "$sensorType$sensorNumber";
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
