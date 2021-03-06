package edu.andr.xyzyx.MyUtil;

import android.util.Log;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.engines.ChaChaEngine;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by asus on 2018/3/3.
 */

public class ChaCha {
    public void doChaCha(boolean encrypt, InputStream is, OutputStream os,
                         byte[] key, byte[] iv) throws IOException {
        CipherParameters cp = new KeyParameter(key);
        ParametersWithIV params = new ParametersWithIV(cp, iv);
        StreamCipher engine = new ChaChaEngine();
        engine.init(encrypt, params);

        byte in[] = new byte[8192];
        byte out[] = new byte[8192];
        int len = 0;
        while(-1 != (len = is.read(in))) {
            len = engine.processBytes(in, 0 , len, out, 0);
            os.write(out, 0, len);
        }
    }
    public void encChaCha(InputStream is, OutputStream os, byte[] key,
                          byte[] iv) throws IOException {
        doChaCha(true, is, os, key, iv);
    }

    public void decChaCha(InputStream is, OutputStream os, byte[] key,
                          byte[] iv) throws IOException {
        doChaCha(false, is, os, key, iv);
    }

    public void chachaString(byte[] test1,byte[] k,byte[] i) throws IOException, NoSuchAlgorithmException
    {
        String test = new String(test1);
        try (InputStream isEnc = new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8));
            ByteArrayOutputStream osEnc = new ByteArrayOutputStream())
        {
//            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//           byte[] key = new byte[32]; // 32 for 256 bit key or 16 for 128 bit
//            byte[] iv = new byte[8]; // 64 bit IV required by ChaCha20
//            sr.nextBytes(key);
//            sr.nextBytes(iv);
            encChaCha(isEnc, osEnc, k, i);
            byte[] encoded = osEnc.toByteArray();
            try (InputStream isDec = new ByteArrayInputStream(encoded);
                ByteArrayOutputStream osDec = new ByteArrayOutputStream())
            {
                decChaCha(isDec, osDec, k, i);
                byte[] decoded = osDec.toByteArray();
                String actual = new String(decoded, StandardCharsets.UTF_8);
            }
        }
    }
}
