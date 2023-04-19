package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XMLUtilsTest extends BaseTest {
    String xmlData;

    @Before
    public void setUp() throws Exception {
        xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<ROOT>\n" +
                "    <Versions>\n" +
                "        <ID Name=\"OGT-G\" Version=\"OGT-G-05.16\"></ID>\n" +
                "        <ID Name=\"OGT-ATS-INTERFACE\" Version=\"3.0\"></ID>\n" +
                "        <ID Name=\"ODPT_APPLICATION_AREA\" Version=\"UEVOL_REG_KMRL_2.6.1_0072\"></ID>\n" +
                "    </Versions>\n" +
                "    <TITLE>Schedule file</TITLE>\n" +
                "    <SCHEDULE NAME=\"14WPETTwef14sep\" COMMENT=\"\">\n" +
                "        <TRIPS>\n" +
                "            <TRIP NUMBER=\"245\" TRIP_ID=\"0245\" SERVICE_ID=\"05\" DIRECTION=\"LEFT\" ENTRY_TIME=\"06:46:55\" DISTANCE=\"4611\" TRAIN_CLASS=\"TRFC_EMU\" MISSION_TYPE=\"Passenger\" RUNNING_MODE=\"Regulated\" PREVIOUS_NUMBER=\"\" NEXT_NUMBER=\"3\">\n" +
                "                <STOP TOP=\"STA_COD_3509T_DN\" DWELLTIME=\"30\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_COD_3509T_DN_STA_PF_DN_AATK\" RUNTIME=\"87\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_DN_AATK\" DWELLTIME=\"50\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_DN_AATK_STA_PF_DN_CPPY\" RUNTIME=\"87\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_DN_CPPY\" DWELLTIME=\"50\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_DN_CPPY_STA_PF_DN_PNCU\" RUNTIME=\"86\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_DN_PNCU\" DWELLTIME=\"50\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_DN_PNCU_STA_COD_3106T_UP\" RUNTIME=\"132\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_COD_3106T_UP\" DWELLTIME=\"0\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "            </TRIP>\n" +
                "            <TRIP NUMBER=\"258\" TRIP_ID=\"0258\" SERVICE_ID=\"05\" DIRECTION=\"RIGHT\" ENTRY_TIME=\"12:00:06\" DISTANCE=\"4616\" TRAIN_CLASS=\"TRFC_EMU\" MISSION_TYPE=\"Passenger\" RUNNING_MODE=\"Regulated\" PREVIOUS_NUMBER=\"62\" NEXT_NUMBER=\"\">\n" +
                "                <STOP TOP=\"STA_COD_3105T_UP\" DWELLTIME=\"180\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_COD_3105T_UP_STA_PF_UP_PNCU\" RUNTIME=\"137\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_UP_PNCU\" DWELLTIME=\"30\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_UP_PNCU_STA_PF_UP_CPPY\" RUNTIME=\"86\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_UP_CPPY\" DWELLTIME=\"30\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_UP_CPPY_STA_PF_UP_AATK\" RUNTIME=\"87\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_PF_UP_AATK\" DWELLTIME=\"30\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "                <RUN TOP=\"I_STA_PF_UP_AATK_STA_COD_3512T_BH\" RUNTIME=\"87\" SITUATION=\"REVENUE_SERVICE\" RUNNING=\"1\"/>\n" +
                "                <STOP TOP=\"STA_COD_3512T_BH\" DWELLTIME=\"0\" SITUATION=\"REVENUE_SERVICE\"/>\n" +
                "            </TRIP>\n" +
                "        </TRIPS>\n" +
                "    </SCHEDULE>\n" +
                "</ROOT>";
    }

    @Test
    public void xmlStringToJsonTest() throws Exception {
        Assert.assertNotNull(XMLUtils.xmlStringToJson(xmlData));
    }

    @Test
    public void xmlStringToJsonNodeTest() throws Exception {
        Assert.assertNotNull(XMLUtils.xmlStringToJsonNode(xmlData));
    }
}
