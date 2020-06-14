import 'package:rxdart/rxdart.dart';

class RxBus {
  BehaviorSubject<dynamic> _bus = BehaviorSubject<dynamic>();

  void send(dynamic msg) {
    _bus.add(msg);
  }

  BehaviorSubject<dynamic> subscribe() {
    return _bus;
  }

  hasListeners() {
    return _bus.hasListener;
  }

  void close() {
    _bus?.close();
  }
}
