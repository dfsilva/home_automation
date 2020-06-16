valueByType(String type, String value) {
  if (type == "bool") {
    return value == "1" ? true : false;
  }

  if (type == "float") {
    return value != null ? double.parse(value) : 0.0;
  }

  if (type == "int") {
    return value != null ? int.parse(value) : 0;
  }

  return value;
}
