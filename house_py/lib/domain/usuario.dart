class Usuario {
  final String nome;
  final String email;

  Usuario({this.nome, this.email});

  Map<String, Object> toJson() {
    return {"nome": this.nome, "email": this.email};
  }

  static fromJson(Map<String, Object> json) {
    return Usuario(nome: json["nome"], email: json["email"]);
  }
}
