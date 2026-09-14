package org.example;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@Epic("API tesztek")
@Feature("Rendszerek lekérdezése")
class ApiRendszerekTest {

    @BeforeAll
    static void beallitas() {
        RestAssured.baseURI = Konfig.supabaseUrl();
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    @DisplayName("GEN-REQ-002 · SYSTEM-REQ-001 · SEC-REQ-009 – a szervezet rendszerei tényleg a saját szervezethez tartoznak")
    void sajatSzervezetRendszerei() {
        given()
                .header("apikey", Konfig.supabaseSecretKey())
                .header("Authorization", "Bearer " + Konfig.supabaseSecretKey())
                .queryParam("organisation_id", "eq." + Konfig.szervezetId())  // szűrés a saját szervezetre
                .queryParam("select", "id,name,organisation_id,aic_organisations(name)")                    // csak ID-k kellenek
                .when()
                .get("/rest/v1/aic_ai_systems")
                .then()
                .statusCode(200)                                               // 1) sikeres
                .body("size()", greaterThan(0))                               // 2) van legalább 1 találat
                .body("organisation_id", everyItem(equalTo(Konfig.szervezetId()))); // 3) MIND a saját szervezeté
    }
}