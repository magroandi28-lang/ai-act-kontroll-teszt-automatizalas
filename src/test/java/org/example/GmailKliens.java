package org.example;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GmailKliens {

    public static Gmail letrehoz() throws Exception {
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory json = GsonFactory.getDefaultInstance();

        InputStream in = GmailKliens.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new IllegalStateException(
                    "Nem található a credentials.json a classpath gyökerén (src/main/resources/credentials.json).");
        }
        GoogleClientSecrets secrets = GoogleClientSecrets.load(json, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                transport, json, secrets, List.of(GmailScopes.GMAIL_READONLY))
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Gmail.Builder(transport, json, credential)
                .setApplicationName("ai-act-kontroll-teszt")
                .build();
    }

    // Megvárja (max 60 mp) és visszaadja a megadott címre érkezett megerősítő link URL-jét.
    public static String megerositoLink(String cimzett) throws Exception {
        Gmail gmail = letrehoz();
        long hatarido = System.currentTimeMillis() + 60_000;

        while (System.currentTimeMillis() < hatarido) {
            List<Message> uzenetek = gmail.users().messages().list("me")
                    .setQ("to:" + cimzett + " newer_than:1h")
                    .setMaxResults(1L)
                    .execute()
                    .getMessages();

            if (uzenetek != null && !uzenetek.isEmpty()) {
                Message uzenet = gmail.users().messages()
                        .get("me", uzenetek.get(0).getId())
                        .setFormat("full")
                        .execute();

                String szoveg = kiolvasSzoveg(uzenet.getPayload());
                Matcher talalat = Pattern.compile("https?://[^\\s\"'<>]+").matcher(szoveg);
                while (talalat.find()) {
                    String url = talalat.group();
                    if (url.contains("verify") || url.contains("confirm") || url.contains("token")) {
                        return url;
                    }
                }
            }
            Thread.sleep(3000);
        }
        throw new RuntimeException("Nem érkezett megerősítő e-mail 60 mp alatt ide: " + cimzett);
    }

    // Kiszedi a levél szövegét a többrészes levél összes darabjából.
    private static String kiolvasSzoveg(MessagePart resz) {
        StringBuilder sb = new StringBuilder();
        if (resz.getBody() != null && resz.getBody().getData() != null) {
            sb.append(new String(Base64.getUrlDecoder().decode(resz.getBody().getData())));
        }
        if (resz.getParts() != null) {
            for (MessagePart alresz : resz.getParts()) {
                sb.append(kiolvasSzoveg(alresz));
            }
        }
        return sb.toString();
    }
}