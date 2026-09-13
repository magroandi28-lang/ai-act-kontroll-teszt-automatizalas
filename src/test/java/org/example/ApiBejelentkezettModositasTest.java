package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("API tesztek")
@Feature("Bejelentkezett módosítás")
class ApiBejelentkezettModositasTest {

    private static final String VEGPONT = "/rest/v1/aic_ai_systems";
    private static RequestSpecification alap;   // közös: cím + apikey + Allure filter
    private String letrehozottId;

    @BeforeAll
    static void beallitas() {
        alap = new RequestSpecBuilder()
                .setBaseUri(Konfig.supabaseUrl())
                .addHeader("apikey", Konfig.supabaseSecretKey())  // átjáró-kulcs (kötelező fejléc)
                .addFilter(new AllureRestAssured())
                .build();
    }

    @Test
    @Story("Azonosított (bejelentkezett) felhasználó módosíthat")
    @Description("""
            Ellenpárja a negatív tesztnek. Itt előbb BEJELENTKEZÜNK a teszt-userrel
            (owner) a Supabase Auth API-n, és megkapjuk az access_token-t (ebben van
            az auth.uid()). Ezzel a tokennel a módosítás MÁR SIKERES (200), mert a
            beépített szabály (SEC-REQ-006) most azonosítani tudja a végrehajtót.
            Így igazoljuk az EDIT-REQ-001-et, és az API-REQ-005-nek megfelelően a
            valódi módosítást felhasználói tokennel végezzük (nem service_role-lal).
            """)
    @DisplayName("EDIT-REQ-001 · SEC-REQ-006 · API-REQ-005 – bejelentkezett owner tokennel a módosítás sikeres")
    void bejelentkezettModositasSikeres() {

        // 1) LOGIN – bejelentkezés email+jelszóval, cserébe access_token
        String token = given(alap)
                .header("Content-Type", "application/json")
                .queryParam("grant_type", "password")
                .body("{ \"email\": \"%s\", \"password\": \"%s\" }"
                        .formatted(Konfig.email(), Konfig.jelszo()))
                .when()
                .post("/auth/v1/token")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue())
                .extract().path("access_token");

        // 2) CREATE – tesztadat előkészítése a mesterkulccsal (ez engedélyezett)
        String nev = "API-POZITIV-TESZT-" + System.currentTimeMillis();
        String ujAdat = """
                {
                  "organisation_id": "%s",
                  "created_by": "%s",
                  "name": "%s",
                  "intended_purpose": "Automatizált pozitív módosítás teszt.",
                  "system_type_id": "97fe63a3-cdca-47dc-8c3b-34730b2c119c",
                  "organisation_role": "deployer",
                  "industry_code": "energy",
                  "lifecycle_stage": "production"
                }
                """.formatted(Konfig.szervezetId(), Konfig.felhasznaloId(), nev);

        letrehozottId = given(alap)
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .body(ujAdat)
                .when()
                .post(VEGPONT)
                .then()
                .statusCode(201)
                .extract().path("[0].id");

        // 3) UPDATE – a BEJELENTKEZETT USER tokenjével -> most SIKERES (POZITÍV)
        String ujNev = nev + "-MODOSITVA";
        given(alap)
                .header("Authorization", "Bearer " + token)   // <-- a user token, nem a mesterkulcs
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .queryParam("id", "eq." + letrehozottId)
                .body("{ \"name\": \"%s\" }".formatted(ujNev))
                .when()
                .patch(VEGPONT)
                .then()
                .statusCode(200)
                .body("[0].name", equalTo(ujNev));            // a módosítás tényleg megtörtént

        // 4) DELETE – takarítás (mesterkulccsal)
        given(alap)
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .queryParam("id", "eq." + letrehozottId)
                .when()
                .delete(VEGPONT)
                .then()
                .statusCode(204);

        letrehozottId = null;
    }

    @AfterEach
    void takaritas() {
        if (letrehozottId != null) {
            given(alap)
                    .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                    .queryParam("id", "eq." + letrehozottId)
                    .delete(VEGPONT);
        }
    }
}
