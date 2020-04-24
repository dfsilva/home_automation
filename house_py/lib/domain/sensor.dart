class Sensor {
  final String type;

  Sensor({this.type});

  Map<String, Object> toJson() {
    return {"type": this.type};
  }

  static fromJson(Map<String, Object> json) {
    return Sensor(type: json["type"]);
  }
}
