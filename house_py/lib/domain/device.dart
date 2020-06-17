import 'package:housepy/domain/sensor.dart';

class Device {
  final int id;
  final String address;
  final String name;
  final String owner;
  final String devType;
  final int position;
  final List<Sensor> sensors;

  Device({this.id, this.address, this.name, this.owner, this.devType, this.position, this.sensors});

  Map<String, Object> toJson() {
    return {
      "id": this.id,
      "address": this.address,
      "name": this.name,
      "owner": this.owner,
      "devType": this.devType,
      "position": this.position,
      "sensors": sensors.map((e) => e.toJson()).toList()
    };
  }

  static Device fromJson(Map<String, Object> json) {
    return Device(
        id: json["id"],
        address: json["address"],
        name: json["name"],
        owner: json["owner"],
        devType: json["devType"],
        position: json["position"],
        sensors:
            json["sensors"] != null ? (json["sensors"] as List<dynamic>).map((e) => Sensor.fromJson(e)).toList() : []);
  }
}
