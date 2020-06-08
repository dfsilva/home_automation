class User {
  final String uid;
  final String name;
  final String email;

  User({this.uid, this.name, this.email});

  Map<String, Object> toJson() {
    return {"uid": this.uid, "name": this.name, "email": this.email};
  }

  static fromJson(Map<String, Object> json) {
    return User(uid: json["uid"], name: json["name"], email: json["email"]);
  }

  User copyWith({String uid, String name, String email}) {
    return User(uid: uid ?? this.uid, name: name ?? this.name, email: email ?? this.email);
  }
}
