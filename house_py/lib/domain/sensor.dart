class Sensor {
  final int number;
  final String sensorType;
  final int deviceId;
  final String dataType;
  final String name;
  final int position;

  Sensor({this.number, this.sensorType, this.deviceId, this.dataType, this.name, this.position});

  Map<String, Object> toJson() {
    return {
      "number": this.number,
      "sensorType": this.sensorType,
      "deviceId": this.deviceId,
      "dataType": this.dataType,
      "name": this.name,
      "position": this.position,
    };
  }

  static Sensor fromJson(Map<String, Object> json) {
    return Sensor(
        number: json["number"],
        sensorType: json["sensorType"],
        deviceId: json["deviceId"],
        dataType: json["dataType"],
        name: json["name"],
        position: json["position"]);
  }

  String getKey(){
    return "$sensorType$number";
  }
}
