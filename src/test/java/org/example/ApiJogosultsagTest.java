package org.example;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@Epic("API tesztek")
@Feature("Jogosultsági kapu")
class ApiJogosultsagTest {

    // Egyszer lefut minden teszt előtt: beállítja az alap URL-t és bekapcsolja
    // az Allure riportba írást (minden API-hívás megjelenik majd a riportban).
    @BeforeAll
    static void beallitas() {
        RestAssured.baseURI = Konfig.supabaseUrl();
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    @DisplayName("SEC-REQ-002 – érvényes service_role kulccsal 200 OK érkezik")
    void ervenyesKulccsal200() {
        given()                                                        // adott: a kérés összeállítása
                .header("apikey", Konfig.supabaseSecretKey())              // Supabase kulcs
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .queryParam("select", "id")                               // csak az id oszlopot kérjük
                .queryParam("limit", 1)                                   // elég 1 sor
                .when()                                                        // amikor: elküldjük
                .get("/rest/v1/aic_ai_systems")
                .then()                                                        // akkor: ellenőrzés
                .statusCode(200);                                          // sikeres hozzáférés
    }

    @Test
    @DisplayName("API-REQ-002 · SEC-REQ-002 – kulcs nélkül 401 elutasítás érkezik")
    void kulcsNelkul401() {
        given()
                .queryParam("select", "id")
                .when()
                .get("/rest/v1/aic_ai_systems")                          // apikey nélkül hívunk
                .then()
                .statusCode(401);                                         // a Supabase visszautasít
    }
}