import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class Sha_3_Test {

    Sha_3 sha;

    @Before
    public void before() {
        sha = new Sha_3();
    }

    @Test
    void testHash() {
        String s = "Email Signature";
        String hashS = "ÁÃi%¶@š\u0004ñµ\u0004ü¼©Ø+?@\u0017'|µí+ eü\u001D8\u0014Õªõ";

        String hash = sha.hash(s);
        assertEquals(hashS, hash);
    }

    @Test
    void testEncrypt() {
        String s = "hello world!";
        String pw = "howdy";
        SymmetricCryptogram encrypted = sha.encrypt(s, pw);
        assertEquals(s, sha.decrypt(encrypted, pw));
    }
}