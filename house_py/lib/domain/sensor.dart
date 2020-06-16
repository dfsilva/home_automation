class Sensor {
  final int id;
  final String sensorType;
  final int deviceId;
  final String dataType;
  final String name;
  final int order;

  Sensor({this.id, this.sensorType, this.deviceId, this.dataType, this.name, this.order});

  Map<String, Object> toJson() {
    return {
      "id": this.id,
      "sensorType": this.sensorType,
      "deviceId": this.deviceId,
      "dataType": this.dataType,
      "name": this.name,
      "order": this.order,
    };
  }

  static Sensor fromJson(Map<String, Object> json) {
    return Sensor(
        id: json["id"],
        sensorType: json["sensorType"],
        deviceId: json["deviceId"],
        dataType: json["dataType"],
        name: json["name"],
        order: json["order"]);
  }
}
