class Sensor {
  final String id;
  final String dataType;
  final String name;

  Sensor({this.id, this.dataType, this.name});

  Map<String, Object> toJson() {
    return {"id": this.id, "dataType": this.dataType, "name": this.name};
  }

  static Sensor fromJson(Map<String, Object> json) {
    return Sensor(id: json["id"]?.toString()?.trim(), dataType: json["dataType"], name: json["name"]);
  }
}
