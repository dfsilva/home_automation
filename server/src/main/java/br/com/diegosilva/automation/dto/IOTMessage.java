package br.com.diegosilva.automation.dto;

import br.com.diegosilva.automation.CborSerializable;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class IOTMessage implements CborSerializable {

    public String id;
    public String sensor;
    public String value;

    @JsonCreator
    public IOTMessage() {

    }

    @JsonCreator
    public IOTMessage(String id, String sensor, String value) {
        this.id = id;
        this.sensor = sensor;
        this.value = value;
    }

    public static IOTMessage decode(String message) {
        try {
            Map<String, String> map = Arrays.stream(message.split(","))
                    .collect(Collectors.toMap(value -> value.split(":")[0], value -> value.split(":")[1]));
            IOTMessage retorno = new IOTMessage();

            if (map.containsKey("id"))
                retorno.id = map.get("id");
            else
                return null;

            if (map.containsKey("sen"))
                retorno.sensor = map.get("sen");
            else
                return null;

            if (map.containsKey("val"))
                retorno.value = map.get("val");
            else
                return null;

            return retorno;
        } catch (Exception e) {
            return null;
        }
    }

    public String encode() {
        return "id:" + this.id + "," + "sen:" + this.sensor + "," + "val:" + value;
    }

    @Override
    public String toString() {
        return "IOTMessage{" +
                "id='" + id + '\'' +
                ", sensor='" + sensor + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
