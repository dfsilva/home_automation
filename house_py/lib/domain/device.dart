class Device {
  final String nome;
  final String descricao;
  final String usuario;
  final List<String> sensors;

  Device({this.nome, this.descricao, this.usuario, this.sensors});

  Map<String, Object> toJson() {
    return {"nome": this.nome, "descricao": this.descricao, "usuario": this.usuario, "sensors": this.sensors};
  }

  static fromJson(Map<String, Object> json) {
    return Device(
        nome: json["nome"], descricao: json["descricao"], usuario: json["usuario"], sensors: json["sensors"] as List);
  }
}
