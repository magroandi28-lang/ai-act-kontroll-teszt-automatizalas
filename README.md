# AI Act Kontroll – Teszt-automatizálás

[![CI](https://github.com/magroandi28-lang/ai-act-kontroll-teszt-automatizalas/actions/workflows/ci.yml/badge.svg)](https://github.com/magroandi28-lang/ai-act-kontroll-teszt-automatizalas/actions/workflows/ci.yml)

📊 **Élő tesztriport (Allure):** https://magroandi28-lang.github.io/ai-act-kontroll-teszt-automatizalas/

Automatizált teszt-suite az **AI Act Kontroll** webalkalmazáshoz.
Felületi (Selenium) és API (RestAssured) tesztek JUnit 5 alapon, Allure riporttal és GitHub Actions CI-vel.

Tesztelt alkalmazás: https://energia-ai-kontroll.vercel.app

## Technológiák
- Java 21, Maven
- Selenium 4 – felületi (end-to-end) tesztek
- RestAssured 5 – API tesztek (Supabase REST + Auth)
- JUnit 5 (Jupiter)
- Allure 2.29 – tesztriport
- Google Gmail API – e-mailes megerősítés ellenőrzése
- GitHub Actions – folyamatos integráció (CI)

## Architektúra (tesztelt rendszer)
- Frontend: Next.js alkalmazás a Vercelen (energia-ai-kontroll.vercel.app)
- Backend/DB/API: Supabase (PostgreSQL + RLS-sel védett REST API + Auth)

## Előfeltételek
- JDK 21
- Maven (vagy az IntelliJ beépített Maven-je)
- Google Chrome (a Selenium tesztekhez)

## Konfiguráció
A tesztek először környezeti változóból, ha az nincs, a `src/test/resources/test.properties` fájlból olvasnak.
Titkos adat nem kerül a repóba (a `test.properties` és a `tokens/` a `.gitignore`-ban van).

Szükséges beállítások (test.properties kulcs = környezeti változó):
- alapUrl = TEST_ALAP_URL – az alkalmazás címe
- tesztEmail = TEST_EMAIL – teszt-felhasználó e-mail
- tesztJelszo = TEST_JELSZO – teszt-felhasználó jelszó
- tesztSzervezetId = TEST_SZERVEZET_ID – szervezet azonosító
- tesztFelhasznaloId = TEST_FELHASZNALO_ID – felhasználó azonosító
- supabaseUrl = SUPABASE_URL – Supabase projekt URL
- supabaseSecretKey = SUPABASE_SECRET_KEY – Supabase service_role kulcs

## Tesztek futtatása

Összes teszt:

    mvn clean test

Csak az API tesztek (böngésző nélkül, gyors):

    mvn test -Dtest=ApiJogosultsagTest,ApiRendszerekTest,ApiCrudTest,ApiBejelentkezettModositasTest

A Gmail-függő tesztek kihagyása (ezt használja a CI is):

    mvn clean test -DexcludedGroups=gmail

## Allure riport

    mvn allure:serve

Az Allure-eszközt a Maven automatikusan letölti; a riport a böngészőben nyílik meg.

## Folyamatos integráció (CI)
A `.github/workflows/ci.yml` minden `master`-re történő push/PR esetén lefut:
JDK 21 beállítása, tesztek futtatása virtuális kijelzővel (xvfb) a Chrome-hoz,
a `@Tag("gmail")` tesztek kihagyva, végül az Allure-eredmény artifactként feltöltve.
A titkos értékek GitHub Secrets-ből jönnek.

## Teszt-lefedettség

Felületi (Selenium) tesztek:
- BejelentkezesTest – belépő oldal elemei, nyelvváltó, demó, adatkezelés (LOGIN-REQ, DEMO-REQ, PRIVACY-REQ)
- RegisztracioTest – regisztrációs űrlap validációi (AUTH-REQ-001)
- RegisztracioAdminLinkkelTest – megerősítés admin linkkel, e-mail nélkül, stabil (MT-AUTH-006)
- RegisztracioMegerositesTest – e-mailes megerősítés Gmail API-val (KAN-5) [gmail]
- VezerlopultTest – vezérlőpult kártyák és navigáció (DASH-REQ-001/002)
- RendszerListaTest – lapozás, oldalanként max 5 (SYSTEM-REQ-003)
- UjRendszeradatFelvitelTest – új MI-rendszer létrehozása (CREATE-REQ-006/007)
- RendszerModositasTest – rendszer módosítása (EDIT-REQ-001)
- RendszerTorlesTest – rendszer törlése/archiválása (EDIT-REQ-004)
- RendszerImportTest – tömeges import (IMPORT-REQ-001/007, KAN-6)
- AdatMentesFeluletrolTest – szabályzat mentése felületről (POLICY-REQ-005)
- KijelentkezesTest – kijelentkezés és védett oldal elérhetetlensége (SESSION-REQ-001)
- DemoIzolacioTest – demó jogosultság-izoláció (KAN-4)

API (RestAssured) tesztek:
- ApiJogosultsagTest – jogosultsági kapu: érvényes kulcs 200, kulcs nélkül 401 (SEC-REQ-002, API-REQ-002)
- ApiRendszerekTest – szervezeti adatizoláció (GEN-REQ-002, SYSTEM-REQ-001, SEC-REQ-009)
- ApiCrudTest – teljes CRUD életciklus + negatív módosításvédelem, 400/P0001 (SEC-REQ-006, API-REQ-002)
- ApiBejelentkezettModositasTest – pozitív módosítás felhasználói tokennel (EDIT-REQ-001, SEC-REQ-006, API-REQ-005)

Segédosztályok: BelepesPom (Page Object), Konfig (konfiguráció), SupabaseAdmin (service_role REST/seed),
UjrahasznosithatoLepesek (közös lépések), GmailKliens (Gmail kapcsolat), képernyőkép-mentő bukáskor.

## Bizonyított hibajegyek
- KAN-4 – demó belépés csak editor jogot ad (nem owner)
- KAN-5 – regisztráció e-mailes megerősítése
- KAN-6 – tömeges import

## Megjegyzés a tesztadatokról
Az automata tesztek csak saját, egyedileg jelölt tesztadatot hoznak létre és takarítanak el;
felhasználói vagy más rendszerhez tartozó adatot nem érintenek.
