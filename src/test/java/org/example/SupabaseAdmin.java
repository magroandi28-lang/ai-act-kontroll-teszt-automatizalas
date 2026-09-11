package org.example;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.gson.GsonFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SupabaseAdmin {

    private static int seedSzamlalo = 0;

    public static String megerositoLink(String email, String jelszo,
                                        String teljesNev, String szervezetNeve) throws Exception {
        GsonFactory json = GsonFactory.getDefaultInstance();

        GenericJson adat = new GenericJson();
        adat.set("full_name", teljesNev);
        adat.set("organisation_name", szervezetNeve);

        GenericJson test = new GenericJson();
        test.set("type", "signup");
        test.set("email", email);
        test.set("password", jelszo);
        test.set("data", adat);

        HttpRequest keres = HttpRequest.newBuilder()
                .uri(URI.create(Konfig.supabaseUrl() + "/auth/v1/admin/generate_link"))
                .header("apikey", Konfig.supabaseSecretKey())
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString(test)))
                .build();

        HttpResponse<String> valasz = HttpClient.newHttpClient().send(keres, HttpResponse.BodyHandlers.ofString());
        if (valasz.statusCode() >= 300) {
            throw new RuntimeException("Admin generate_link hiba: " + valasz.statusCode() + " – " + valasz.body());
        }

        GenericJson valaszJson = json.createJsonParser(valasz.body()).parse(GenericJson.class);
        String hashedToken = (String) valaszJson.get("hashed_token");
        if (hashedToken == null) {
            throw new RuntimeException("A válaszban nincs hashed_token: " + valasz.body());
        }
        return Konfig.alapUrl() + "/auth/confirm?token_hash=" + hashedToken + "&type=signup";
    }

    public static void rendszertBeszur(String nev) throws Exception {
        GsonFactory json = GsonFactory.getDefaultInstance();

        GenericJson sor = new GenericJson();
        sor.set("organisation_id", Konfig.szervezetId());
        sor.set("created_by", Konfig.felhasznaloId());
        sor.set("name", nev);
        sor.set("intended_purpose", "Automatizált teszt-seed rendszer.");
        sor.set("system_type_id", "97fe63a3-cdca-47dc-8c3b-34730b2c119c");
        sor.set("organisation_role", "deployer");
        sor.set("industry_code", "energy");
        sor.set("lifecycle_stage", "production");
        sor.set("created_at", java.time.Instant.now().minusSeconds(++seedSzamlalo).toString());

        HttpRequest keres = HttpRequest.newBuilder()
                .uri(URI.create(Konfig.supabaseUrl() + "/rest/v1/aic_ai_systems"))
                .header("apikey", Konfig.supabaseSecretKey())
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString(sor)))
                .build();

        HttpResponse<String> valasz = HttpClient.newHttpClient().send(keres, HttpResponse.BodyHandlers.ofString());
        if (valasz.statusCode() >= 300) {
            throw new RuntimeException("Rendszer beszúrás hiba: " + valasz.statusCode() + " – " + valasz.body());
        }
    }

    public static void seedTorles() throws Exception {
        HttpRequest keres = HttpRequest.newBuilder()
                .uri(URI.create(Konfig.supabaseUrl() + "/rest/v1/aic_ai_systems?name=like.SEED-LAPOZAS*"))
                .header("apikey", Konfig.supabaseSecretKey())
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .DELETE()
                .build();

        HttpResponse<String> valasz = HttpClient.newHttpClient().send(keres, HttpResponse.BodyHandlers.ofString());
        if (valasz.statusCode() >= 300) {
            throw new RuntimeException("Seed törlés hiba: " + valasz.statusCode() + " – " + valasz.body());
        }
    }

    public static String tagsagSzerepkor(String userId) throws Exception {
        GsonFactory json = GsonFactory.getDefaultInstance();

        HttpRequest keres = HttpRequest.newBuilder()
                .uri(URI.create(Konfig.supabaseUrl()
                        + "/rest/v1/aic_organisation_members?user_id=eq." + userId
                        + "&select=member_role"))
                .header("apikey", Konfig.supabaseSecretKey())
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .header("Accept", "application/vnd.pgrst.object+json")
                .GET()
                .build();

        HttpResponse<String> valasz = HttpClient.newHttpClient().send(keres, HttpResponse.BodyHandlers.ofString());
        if (valasz.statusCode() >= 300) {
            throw new RuntimeException("Tagság lekérdezés hiba: " + valasz.statusCode() + " – " + valasz.body());
        }
        GenericJson sor = json.createJsonParser(valasz.body()).parse(GenericJson.class);
        return (String) sor.get("member_role");
    }

    public static void torolFelhasznalo(String userId) throws Exception {
        HttpRequest keres = HttpRequest.newBuilder()
                .uri(URI.create(Konfig.supabaseUrl() + "/auth/v1/admin/users/" + userId))
                .header("apikey", Konfig.supabaseSecretKey())
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .DELETE()
                .build();

        HttpResponse<String> valasz = HttpClient.newHttpClient().send(keres, HttpResponse.BodyHandlers.ofString());
        if (valasz.statusCode() >= 300) {
            throw new RuntimeException("Felhasználó törlés hiba: " + valasz.statusCode() + " – " + valasz.body());
        }
    }

    public static boolean rendszerLetezik(String nev) throws Exception {
        HttpRequest keres = HttpRequest.newBuilder()
                .uri(URI.create(Konfig.supabaseUrl()
                        + "/rest/v1/aic_ai_systems?name=eq." + nev.replace(" ", "%20")
                        + "&organisation_id=eq." + Konfig.szervezetId()
                        + "&inventory_status=eq.active&select=id"))
                .header("apikey", Konfig.supabaseSecretKey())
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .GET()
                .build();

        HttpResponse<String> valasz = HttpClient.newHttpClient().send(keres, HttpResponse.BodyHandlers.ofString());
        if (valasz.statusCode() >= 300) {
            throw new RuntimeException("Rendszer lekérdezés hiba: " + valasz.statusCode() + " – " + valasz.body());
        }
        return !valasz.body().trim().equals("[]");
    }

    public static void importTakaritas() throws Exception {
        HttpRequest keres = HttpRequest.newBuilder()
                .uri(URI.create(Konfig.supabaseUrl() + "/rest/v1/aic_ai_systems?name=like.IMPORT-TESZT*"))
                .header("apikey", Konfig.supabaseSecretKey())
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .DELETE()
                .build();

        HttpResponse<String> valasz = HttpClient.newHttpClient().send(keres, HttpResponse.BodyHandlers.ofString());
        if (valasz.statusCode() >= 300) {
            throw new RuntimeException("Import takarítás hiba: " + valasz.statusCode() + " – " + valasz.body());
        }
    }

    public static String rendszerId(String nev) throws Exception {
        HttpRequest keres = HttpRequest.newBuilder()
                .uri(URI.create(Konfig.supabaseUrl()
                        + "/rest/v1/aic_ai_systems?name=eq." + nev.replace(" ", "%20")
                        + "&organisation_id=eq." + Konfig.szervezetId()
                        + "&inventory_status=eq.active&select=id"))
                .header("apikey", Konfig.supabaseSecretKey())
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .header("Accept", "application/vnd.pgrst.object+json")
                .GET()
                .build();

        HttpResponse<String> valasz = HttpClient.newHttpClient().send(keres, HttpResponse.BodyHandlers.ofString());
        if (valasz.statusCode() >= 300) {
            throw new RuntimeException("Rendszer id lekérdezés hiba: " + valasz.statusCode() + " – " + valasz.body());
        }
        GsonFactory json = GsonFactory.getDefaultInstance();
        GenericJson sor = json.createJsonParser(valasz.body()).parse(GenericJson.class);
        return (String) sor.get("id");
    }
}