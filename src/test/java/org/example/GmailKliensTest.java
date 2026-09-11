package org.example;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class GmailKliensTest {

    @Test
    void kapcsolodikAGmailhez() throws Exception {
        Gmail gmail = GmailKliens.letrehoz();
        Profile profil = gmail.users().getProfile("me").execute();

        System.out.println("Csatlakozva ehhez: " + profil.getEmailAddress());
        Assertions.assertNotNull(profil.getEmailAddress());
    }
}
