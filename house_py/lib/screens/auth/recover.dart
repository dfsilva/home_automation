import 'package:flutter/material.dart';
import 'package:housepy/service/service_locator.dart';
import 'package:housepy/service/usuario_service.dart';
import 'package:housepy/utils/message.dart';

class RecoverScreen extends StatefulWidget {
  @override
  RecoverScreenState createState() => RecoverScreenState();
}

class RecoverScreenState extends State<RecoverScreen> {
  final _formKey = GlobalKey<FormState>();
  UserService _userService;

  String _email;
  FocusNode _focusEmail;

  @override
  void initState() {
    super.initState();
    this._focusEmail = FocusNode();
  }

  @override
  void dispose() {
    this._focusEmail.dispose();
    super.dispose();
  }

  _recover() {
    if (_formKey.currentState.validate()) {
      _formKey.currentState.save();
      _userService.recovery(_email).then((result) {
        showInfo("Senha enviada com sucesso!");
        Navigator.of(context).pop();
      }).catchError((error) {
        print("Errooooooo!!!!!");
        showError("Erro ao fazer login");
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    _userService = Services.get(UserService);

    return Scaffold(
      appBar: AppBar(
        title: Text("Recuperar sua senha"),
      ),
      body: Column(
        children: <Widget>[
          SizedBox(
            height: 50,
          ),
          Form(
            key: _formKey,
            child: ListView(
              shrinkWrap: true,
              children: <Widget>[
                Padding(
                  padding: const EdgeInsets.symmetric(vertical: 5, horizontal: 10),
                  child: TextFormField(
                    keyboardType: TextInputType.emailAddress,
                    focusNode: _focusEmail,
                    textInputAction: TextInputAction.send,
                    validator: (email) {
                      if (email.isEmpty) {
                        return "Informe o email.";
                      }
                      return null;
                    },
                    onFieldSubmitted: (nome) {
                      this._focusEmail.unfocus();
                    },
                    onSaved: (email) {
                      this._email = email;
                    },
                    decoration: InputDecoration(hintText: "email", labelText: "email", icon: Icon(Icons.email)),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.only(top: 30, left: 10, right: 10, bottom: 5),
                  child: ElevatedButton(
                    child: Text("Recuperar senha"),
                    onPressed: () {
                      _recover();
                    },
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
