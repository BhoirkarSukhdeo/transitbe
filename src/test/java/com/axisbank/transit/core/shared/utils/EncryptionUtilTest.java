package com.axisbank.transit.core.shared.utils;

import com.axisbank.transit.BaseTest;
import com.axisbank.transit.transitCardAPI.constants.TransitCardAPIConstants;
import org.junit.Assert;
import org.junit.Test;

public class EncryptionUtilTest extends BaseTest {

    @Test
    public void encryptTest() throws Exception {
        Assert.assertNotNull(EncryptionUtil.encrypt("2233445566", "U75m30vuhpikWy2Z", TransitCardAPIConstants.ALGORITHM_AES));
    }

    @Test
    public void decryptTest() throws Exception {
        Assert.assertNotNull(EncryptionUtil.decrypt("3Hl66bC0Nb4G75Gv3x6rSA==", "U75m30vuhpikWy2Z", TransitCardAPIConstants.ALGORITHM_AES));
    }

    @Test
    public void aesEncryptTest() throws Exception {
        Assert.assertNotNull(EncryptionUtil.aesEncrypt("223344566", "U75m30vuhpikWy2Z"));
    }

    @Test
    public void aesDecryptTest() throws Exception {
        Assert.assertNotNull(EncryptionUtil.aesDecrypt("Gya0iFmHTpV6AEEW1M4Ke/CyxMSwlUZwmNagTUWQ0MPmneHTOyi+3MnBFo2lZwtVrA3WGA==", "U75m30vuhpikWy2Z"));
    }

    @Test
    public void aesEncryptPpimTest() throws Exception {
        Assert.assertNotNull(EncryptionUtil.aesEncryptPpim("223344566", "transit", "U75m30vuhpikWy2Z"));
    }

    @Test
    public void aesDecryptPpimTest() throws Exception {
        Assert.assertNotNull(EncryptionUtil.aesDecryptPpim("1JlsKwZucoVDJtMQpMW6ug==", "U75m30vuhpikWy2Z", "U75m30vuhpikWy2Z"));
    }
}
