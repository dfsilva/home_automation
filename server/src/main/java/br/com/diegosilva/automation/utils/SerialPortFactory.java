package br.com.diegosilva.automation.utils;

import jssc.SerialPortException;

public class SerialPortFactory {

    private static jssc.SerialPort _instance = null;

    private SerialPortFactory() {
    }

    public static jssc.SerialPort get(String portName) throws SerialPortException {
        if (_instance == null) {
            _instance = new jssc.SerialPort(portName);
            _instance.openPort();
            _instance.setParams(jssc.SerialPort.BAUDRATE_57600,
                    jssc.SerialPort.DATABITS_8,
                    jssc.SerialPort.STOPBITS_1,
                    jssc.SerialPort.PARITY_NONE);

            int mask = jssc.SerialPort.MASK_RXCHAR + jssc.SerialPort.MASK_CTS + jssc.SerialPort.MASK_DSR;
            _instance.setEventsMask(mask);
            return _instance;
        }
        return _instance;
    }
}
