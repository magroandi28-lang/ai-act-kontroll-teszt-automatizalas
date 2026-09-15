# Tesztterv — AI Act Kontroll

**Projekt:** AI Act Kontroll – vállalati MI-megfelelőségi alkalmazás tesztautomatizálása
**Tesztelt rendszer (SUT):** https://energia-ai-kontroll.vercel.app
**Készítette:** Varga Andrea
**Dokumentum típusa:** Tesztterv (a tesztelés előzetes, tervező dokumentuma)

---

## 1. Bevezetés és cél

A dokumentum az AI Act Kontroll webalkalmazás tesztelésének tervét rögzíti: mit, miért és hogyan
tesztelünk, milyen környezetben és eszközökkel, valamint mikor tekintjük a tesztelést késznek.

A tesztelés célja annak igazolása, hogy az alkalmazás a specifikációban rögzített funkcionális és
biztonsági követelményeknek megfelelően működik, és a hibák a fejlesztés/kiadás előtt kiderülnek.

## 2. Tesztelendő rendszer

- **Frontend:** Next.js alkalmazás a Vercelen (energia-ai-kontroll.vercel.app)
- **Backend:** Supabase (PostgreSQL, Auth, Row Level Security), REST/Auth API
- **Fő funkciók:** regisztráció és e-mailes megerősítés, bejelentkezés, adatkezelési nyilatkozat,
  vezérlőpult, MI-rendszerek listázása és lapozása, új rendszer felvitele, módosítás, törlés,
  Excel-import, felületről mentés, kijelentkezés, szerepkör- és jogosultságkezelés.

## 3. Hatókör

**Tesztelés tárgya (in scope):**
- Funkcionális felületi (UI) folyamatok végponttól végpontig.
- Az adatréteg (Supabase REST/Auth) API-szintű ellenőrzése.
- Jogosultság- és hozzáférés-kezelés (azonosítatlan/idegen módosítás tiltása).
- Pozitív és negatív esetek, adatvezérelt és ismételt bevitel, lapozás.

**Hatókörön kívül (out of scope):**
- Terheléses/teljesítmény- és biztonsági penetrációs tesztelés.
- A Supabase és a Vercel infrastruktúrájának tesztelése (külső, megbízhatónak tekintett szolgáltatás).
- Böngésző-kompatibilitási mátrix (a futtatás Chrome/Chromium alapú).

## 4. Tesztelési megközelítés

**Tesztszintek:**
- **Rendszer / E2E (UI):** valós böngészőben, Selenium WebDriverrel, a felhasználói folyamatok szintjén.
- **Integráció (API):** a Supabase REST/Auth végpontok közvetlen ellenőrzése RestAssured-del.

**Teszttípusok:**
- Funkcionális tesztelés (a követelmények szerinti működés).
- Regressziós tesztelés (a teljes suite minden CI-futáskor lefut).
- Pozitív és negatív tesztek (helyes és hibás bemenetek).
- Adatvezérelt és ismételt bevitel (paraméterezett esetek).
- Jogosultsági/hozzáférési ellenőrzés (biztonsági jellegű funkcionális tesztek).

**Módszertan:** specifikáció-alapú tesztelés, követelmény-azonosítókkal (pl. `LOGIN-REQ`, `AUTH-REQ`,
`CREATE-REQ`, `EDIT-REQ`, `IMPORT-REQ`, `SESSION-REQ`, `SEC-REQ`, `API-REQ`). Minden teszt `@DisplayName`-je
tartalmazza a lefedett követelmény és a manuális teszteset (MT-…) azonosítóját, így a teszt visszakövethető.

## 5. Tesztelt funkcióterületek

| Terület | Példa követelmény-ID | Fő ellenőrzés |
|---|---|---|
| Bejelentkezés, adatkezelési nyilatkozat | LOGIN-REQ, PRIVACY-REQ | mezők, belépés, nyilatkozat, nyelvváltó |
| Regisztráció + megerősítés | AUTH-REQ | regisztráció, e-mailes/admin-linkes megerősítés |
| Vezérlőpult | DASH-REQ | modulkártyák láthatósága és navigáció |
| Rendszerek listája, lapozás | SYSTEM-REQ | oldalanként max 5, nincs duplikáció/kihagyás |
| Új rendszer felvitele | CREATE-REQ | létrehozás és mentés |
| Módosítás | EDIT-REQ | átnevezés és a valódi változás ellenőrzése |
| Törlés | EDIT-REQ | törlés és a tényleges eltűnés |
| Import | IMPORT-REQ | sablon letöltése és importálása |
| Felületről mentés | POLICY-REQ | szabályzat PDF/nyomtatás |
| Kijelentkezés / munkamenet | SESSION-REQ | kilépés, védett oldal elérhetetlensége |
| Jogosultság / adatszeparáció | SEC-REQ, API-REQ | idegen/azonosítatlan módosítás tiltott; szervezeti elkülönítés |

A teljes teszteset–követelmény megfeleltetés a [`teszt-dokumentacio`](teszt-dokumentacio) fájlban található.

## 6. Környezet és eszközök

- **Nyelv/build:** Java 21, Maven
- **Tesztkeret:** JUnit 5
- **UI-automatizálás:** Selenium WebDriver (Chrome/Chromium)
- **API-tesztelés:** RestAssured
- **Riport:** Allure
- **CI:** GitHub Actions (headless futtatás `xvfb`-vel; a `@Tag("gmail")` és `@Tag("flaky")` tesztek kizárva)
- **Backend hozzáférés a teszthez:** Supabase REST/Auth (seed és ellenőrzés)

## 7. Tesztadatok

- Helyi futáshoz `test.properties`, CI-hez környezeti változók (URL, teszt e-mail/jelszó, szervezet- és
  felhasználó-azonosító, Supabase URL és kulcs).
- Egyedi, időbélyeggel képzett e-mail címek a regisztrációs tesztekhez (ütközésmentesség).
- Supabase-seed a lapozási és import tesztek adataihoz; a teszt saját maga után takarít.
- Titkos kulcs kizárólag környezeti változóból/titokként; a kódba és a riportba nem kerül be.

## 8. Belépési kritériumok

- A SUT elérhető és stabil (Vercel deploy él).
- A Supabase backend elérhető, a teszthez szükséges hozzáférés beállítva.
- A build lefordul, a függőségek rendelkezésre állnak.

## 9. Kilépési kritériumok

- A tervezett tesztesetek lefutottak.
- A CI zöld: minden nem kizárt (`gmail`, `flaky`) teszt sikeres.
- Az Allure riport elkészült, a hibák dokumentáltak.
- A követelmény-lefedettség a traceability táblában igazolt.

## 10. Kockázatok és kezelésük

| Kockázat | Kezelés |
|---|---|
| Külső e-mail (Gmail API) lassú/instabil | `@Tag("gmail")` – CI-ben kizárva; admin-linkes megerősítés stabil alternatívaként |
| Időzítéstől függő, ingadozó teszt | `@Tag("flaky")` – CI-ben kizárva, külön futtatható |
| Külső szolgáltatás (Supabase/Vercel) kiesése | belépési kritérium ellenőrzése; a hiba egyértelműen jelződik |
| Titkos kulcs kiszivárgása | kizárólag titokként; a riport publikálás előtt szűrve |

## 11. Leszállítandók

- Automatizált tesztkód (UI + API), a repóban.
- Allure tesztriport.
- Teszt-dokumentáció (teszteset–követelmény megfeleltetés).
- Vezetői tesztjelentés.
- GitHub Actions CI-konfiguráció és -futások.

## 12. Ütemezés és felelős

- **Felelős:** Varga Andrea (tesztek tervezése, fejlesztése, kiértékelése).
- **Ütemezés:** a tesztek a fejlesztéssel párhuzamosan készültek; a regressziós suite minden
  `master`-re történő push és pull request esetén automatikusan lefut a CI-ben.

## 13. Kapcsolódó dokumentumok

- [`teszt-dokumentacio`](teszt-dokumentacio) – teszteset–követelmény megfeleltetés (traceability)
- [`vezetoi-tesztjelentes.md`](vezetoi-tesztjelentes.md) – vezetői tesztjelentés
- `README.md` – telepítés, futtatás, riport
