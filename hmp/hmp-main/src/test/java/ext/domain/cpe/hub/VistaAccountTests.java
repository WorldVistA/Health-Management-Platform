package EXT.DOMAIN.cpe.hub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Before;
import org.junit.Test;

public class VistaAccountTests {

    VistaAccount account;

    @Before
    public void setUp() throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        account = new VistaAccount();
		account.setDivision("999");
		account.setName("FOO");
		account.setHost("foo.gov");
    }

    @Test
    public void testConstruct() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException {
        assertEquals("999", account.getDivision());
        assertEquals("FOO", account.getName());
        assertEquals("foo.gov", account.getHost());
        assertEquals(VistaAccount.DEFAULT_PORT, account.getPort());
        assertFalse(account.isProduction());
    }

//    void testNotNullableConstraints() {
//        account = new VistaAccount()
//    }
//
//    void testUniqueConstraints() {
//        mockForConstraintsTests(VistaAccount, [account])
//
//        account = new VistaAccount(division: "999", name: "FOO")
//        assertFalse account.validate()
//        assertEquals("unique", account.errors["name"])
//    }
//
//    void testBlankConstraints() {
//        account = new VistaAccount(division: "", name: "", host: "")
//        assertFalse account.validate()
//        assertEquals("blank", account.errors["division"])
//        assertEquals("blank", account.errors["name"])
//        assertEquals("blank", account.errors["host"])
//    }
}
