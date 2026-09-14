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

@Epic("API tesztek")
@Feature("Rendszer életciklus és módosításvédelem")
class ApiCrudTest {

    private static final String VEGPONT = "/rest/v1/aic_ai_systems";
    private static RequestSpecification alap;   // közös beállítás (újrahasznosítás)
    private String letrehozottId;               // a létrehozott rendszer id-ja (takarításhoz)

    @BeforeAll
    static void beallitas() {
        alap = new RequestSpecBuilder()
                .setBaseUri(Konfig.supabaseUrl())
                .addHeader("apikey", Konfig.supabaseSecretKey())
                .addHeader("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .addFilter(new AllureRestAssured())
                .build();
    }

    @Test
    @Story("A rendszer módosítását beépített adatbázis-szabály védi")
    @Description("""
            A teszt a rendszer életciklusát járja végig a Supabase REST API-n át.
            Létrehozás, olvasás és törlés a service_role kulccsal engedélyezett.

            A MÓDOSÍTÁS azonban NEGATÍV eset: a service_role kulcs 'admin', de
            NÉVTELEN (nincs auth.uid()), ezért a beépített adatbázis-szabály
            elutasítja. Ez NEM hiba, hanem a specifikáció szerinti szándékos
            viselkedés:
              - SEC-REQ-006: minden SECURITY DEFINER függvény ellenőrzi az auth.uid()-t;
              - API-REQ-002: jogosulatlan kérés ne módosítson adatot, adjon stabil hibakódot.
            Következmény: a valódi (pozitív) módosítást csak bejelentkezett felhasználó
            tokenjével szabad tesztelni (lásd API-REQ-005: a service_role nem való ide).
            """)
    @DisplayName("SEC-REQ-006 · API-REQ-002 – rendszer létrehozható/olvasható/törölhető, de azonosítatlan módosítása tiltott")
    void eletciklusEsModositasVedelem() {
        String nev = "API-CRUD-TESZT-" + System.currentTimeMillis();  // egyedi név

        // 1) CREATE (POST) – új rendszer létrehozása (POZITÍV: service_role létrehozhat)
        String ujAdat = """
                {
                  "organisation_id": "%s",
                  "created_by": "%s",
                  "name": "%s",
                  "intended_purpose": "Automatizált CRUD teszt.",
                  "system_type_id": "97fe63a3-cdca-47dc-8c3b-34730b2c119c",
                  "organisation_role": "deployer",
                  "industry_code": "energy",
                  "lifecycle_stage": "production"
                }
                """.formatted(Konfig.szervezetId(), Konfig.felhasznaloId(), nev);

        letrehozottId = given(alap)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .body(ujAdat)
                .when()
                .post(VEGPONT)
                .then()
                .statusCode(201)
                .body("[0].name", equalTo(nev))
                .extract().path("[0].id");

        // 2) READ (GET) – megtaláljuk id alapján (POZITÍV)
        given(alap)
                .queryParam("id", "eq." + letrehozottId)
                .queryParam("select", "id,name")
                .when()
                .get(VEGPONT)
                .then()
                .statusCode(200)
                .body("[0].name", equalTo(nev));

        // 3) UPDATE (PATCH) – NEGATÍV eset: azonosítatlan végrehajtó -> a szabály tiltja
        //    SEC-REQ-006 + API-REQ-002: elvárjuk a 400-at és a beépített hibaüzenetet.
        given(alap)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .queryParam("id", "eq." + letrehozottId)
                .body("{ \"name\": \"%s-MODOSITVA\" }".formatted(nev))
                .when()
                .patch(VEGPONT)
                .then()
                .statusCode(400)                                            // tiltás
                .body("code", equalTo("P0001"))                            // egyedi DB-hibakód
                .body("message", equalTo("A módosítás végrehajtója nem azonosítható."));

        // 4) DELETE – takarítás (POZITÍV: service_role törölhet)
        given(alap)
                .queryParam("id", "eq." + letrehozottId)
                .when()
                .delete(VEGPONT)
                .then()
                .statusCode(204);

        // 5) Ellenőrzés: tényleg nincs már ott
        given(alap)
                .queryParam("id", "eq." + letrehozottId)
                .queryParam("select", "id")
                .when()
                .get(VEGPONT)
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));

        letrehozottId = null;
    }

    @AfterEach
    void takaritas() {
        // Biztonsági háló: ha a teszt félúton elakadt, a létrehozott sort itt is töröljük.
        if (letrehozottId != null) {
            given(alap).queryParam("id", "eq." + letrehozottId).delete(VEGPONT);
        }
    }
}