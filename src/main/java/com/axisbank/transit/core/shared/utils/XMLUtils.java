package com.axisbank.transit.core.shared.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;

@Slf4j
public class XMLUtils {
    /**
     * This method converts XML string to JSONObject
     * @param xmlString
     * @return
     */
    public static JSONObject xmlStringToJson(String xmlString){
        return XML.toJSONObject(xmlString);
    }

    public static JsonNode xmlStringToJsonNode(String string) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readTree(string.getBytes());
    }
}
