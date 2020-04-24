class User {
  final String name;
  final String email;

  User({this.name, this.email});

  Map<String, Object> toJson() {
    return {"name": this.name, "email": this.email};
  }

  static fromJson(Map<String, Object> json) {
    return User(name: json["name"], email: json["email"]);
  }
}
