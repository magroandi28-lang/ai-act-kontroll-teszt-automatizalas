package org.example;

import java.io.InputStream;
import java.util.Properties;

public class Konfig {
    private static final Properties TULAJDONSAGOK = betolt();

    private static Properties betolt() {
        Properties p = new Properties();
        try (InputStream be = Konfig.class.getClassLoader().getResourceAsStream("test.properties")) {
            if (be != null) {
                p.load(be);
            }
        } catch (Exception e) {
            throw new RuntimeException("A test.properties nem tölthető be.", e);
        }
        return p;
    }

    // Sorrend: 1) környezeti változó (pipeline), 2) test.properties fájl (helyi futás).
    private static String ertek(String kulcs, String kornyezetiValtozo) {
        String kv = System.getenv(kornyezetiValtozo);
        if (kv != null && !kv.isBlank()) {
            return kv;
        }
        return TULAJDONSAGOK.getProperty(kulcs);
    }

    public static String alapUrl() { return ertek("alapUrl", "TEST_ALAP_URL"); }
    public static String email()   { return ertek("tesztEmail", "TEST_EMAIL"); }
    public static String jelszo()  { return ertek("tesztJelszo", "TEST_JELSZO"); }
    public static String szervezetId()   { return ertek("tesztSzervezetId", "TEST_SZERVEZET_ID"); }
    public static String felhasznaloId() { return ertek("tesztFelhasznaloId", "TEST_FELHASZNALO_ID"); }
    public static String supabaseUrl()       { return ertek("supabaseUrl", "SUPABASE_URL"); }
    public static String supabaseSecretKey() { return ertek("supabaseSecretKey", "SUPABASE_SECRET_KEY"); }
}
