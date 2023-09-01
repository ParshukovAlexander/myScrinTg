package com.example.myscrintg.decode;


import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;
@Component
public class Decode {

    public String getDecode(String s) {
        Base64.Decoder decoder = Base64.getDecoder();
            String res = new String(decoder.decode(s));
        return res;
    }
    public String getEncode(String s){
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

}
