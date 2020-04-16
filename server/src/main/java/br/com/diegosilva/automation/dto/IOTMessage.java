package br.com.diegosilva.automation.dto;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class IOTMessage {

    public String id;
    public String tipo;
    public String value;

    public static IOTMessage decode(String message) {
        try {
            Map<String, String> map = Arrays.stream(message.split(","))
                    .collect(Collectors.toMap(value -> value.split(":")[0], value -> value.split(":")[1]));
            IOTMessage retorno = new IOTMessage();

            if (map.containsKey("id"))
                retorno.id = map.get("id");
            else
                return null;
            if (map.containsKey("T"))
                retorno.tipo = map.get("T");
            else
                return null;
            if (map.containsKey("value"))
                retorno.value = map.get("value");
            else
                return null;
            return retorno;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "IOTMessage{" +
                "id='" + id + '\'' +
                ", tipo='" + tipo + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}