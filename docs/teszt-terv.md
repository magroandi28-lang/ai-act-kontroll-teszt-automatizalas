# Tesztterv — AI Act Kontroll

**Projekt:** AI Act Kontroll – vállalati MI-megfelelőségi alkalmazás tesztelése
**Tesztelt rendszer (SUT):** https://energia-ai-kontroll.vercel.app
**Készítette:** Varga Andrea
**Dokumentum típusa:** Tesztterv (a tesztelés előzetes, tervező dokumentuma)

---

## 1. Bevezetés és cél

A dokumentum az AI Act Kontroll webalkalmazás tesztelésének tervét rögzíti: mit, miért és hogyan
tesztelünk, milyen környezetben és eszközökkel, valamint mikor tekintjük a tesztelést késznek.

A tesztelés **két pilléren** áll:
- **manuális tesztelés** – a teszteseteket emberi tesztelő hajtja végre és értékeli;
- **automatizált tesztelés** – a visszatérő ellenőrzéseket kódolt tesztek futtatják, folyamatos integrációban.

A cél annak igazolása, hogy az alkalmazás a specifikáció funkcionális és biztonsági követelményeinek
megfelelően működik, és a hibák a kiadás előtt kiderülnek.

## 2. Tesztelendő rendszer

- **Frontend:** Next.js alkalmazás a Vercelen (energia-ai-kontroll.vercel.app)
- **Backend:** Supabase (PostgreSQL, Auth, Row Level Security), REST/Auth API
- **Fő funkciók:** regisztráció és e-mailes megerősítés, bejelentkezés, adatkezelési nyilatkozat,
  vezérlőpult, MI-rendszerek listázása és lapozása, új rendszer felvitele, módosítás, törlés,
  Excel-import, felületről mentés, kijelentkezés, szerepkör- és jogosultságkezelés.

## 3. Hatókör

**Tesztelés tárgya (in scope):**
- Funkcionális felhasználói folyamatok végponttól végpontig (manuálisan és automatizáltan).
- Az adatréteg (Supabase REST/Auth) API-szintű ellenőrzése.
- Jogosultság- és hozzáférés-kezelés (azonosítatlan/idegen módosítás tiltása).
- Pozitív és negatív esetek, adatvezérelt és ismételt bevitel, lapozás.

**Hatókörön kívül (out of scope):**
- Terheléses/teljesítmény- és penetrációs biztonsági tesztelés.
- A Supabase és a Vercel infrastruktúrájának tesztelése (külső, megbízhatónak tekintett szolgáltatás).
- Böngésző-kompatibilitási mátrix (a futtatás Chrome/Chromium alapú).

## 4. Tesztelési megközelítés

**Módszertan:** specifikáció-alapú tesztelés, követelmény-azonosítókkal (pl. `LOGIN-REQ`, `AUTH-REQ`,
`CREATE-REQ`, `EDIT-REQ`, `IMPORT-REQ`, `SESSION-REQ`, `SEC-REQ`, `API-REQ`). Minden teszteset
visszavezethető egy követelményre; a megfeleltetést a [`teszt-dokumentacio`](teszt-dokumentacio) tartalmazza.

**Teszttípusok (mindkét pillérre):** funkcionális, regressziós, pozitív és negatív, adatvezérelt/ismételt
bevitel, valamint jogosultsági (biztonsági jellegű funkcionális) tesztelés.

### 4.1 Manuális tesztelés

- **Cél:** a felhasználói folyamatok emberi végigjátszása, a specifikációhoz mért ellenőrzése, és olyan
  esetek feltárása, amelyekre az automatizálás nem tér ki.
- **Módszer:**
  - **Teszteset-alapú végrehajtás:** a manuális tesztesetek (MT-… azonosítók) lépésről lépésre,
    a várt és a tapasztalt eredmény összevetésével.
  - **Exploratív tesztelés:** irányított, de nem előre szkriptelt kipróbálás a peremesetek és a
    használhatóság feltárására.
- **Lefedett folyamatok:** regisztráció és megerősítés, bejelentkezés, adatkezelési nyilatkozat,
  vezérlőpult-navigáció, rendszer felvitele/módosítása/törlése, import, lapozás, kijelentkezés.
- **Dokumentálás:** a teszteset azonosítója, lépései, várt és tapasztalt eredmény, státusz (megfelelt/hibás),
  hiba esetén a reprodukció leírása. Az eredmények a teszt-dokumentációban és a vezetői jelentésben összegződnek.
- **Környezet:** valós böngésző (Chrome), a SUT éles Vercel-példánya.

### 4.2 Automatizált tesztelés

- **Cél:** a visszatérő, jól definiált ellenőrzések gyors, ismételhető futtatása – regresszió kiszűrése.
- **Tesztszintek:**
  - **Rendszer / E2E (UI):** valós böngészőben, **Selenium WebDriver**rel, a felhasználói folyamatok szintjén.
  - **Integráció (API):** a Supabase REST/Auth végpontok közvetlen ellenőrzése **RestAssured**-del.
- **Keretrendszer:** **JUnit 5**; a tesztek `@DisplayName`-je tartalmazza a lefedett követelmény és a
  kapcsolódó manuális teszteset (MT-…) azonosítóját, így a manuális és az automata oldal összekapcsolódik.
- **Futtatás:** helyben Mavennel, valamint **GitHub Actions CI**-ben minden `master`-re történő push és
  pull request esetén (headless, `xvfb`; a `@Tag("gmail")` és `@Tag("flaky")` tesztek kizárva).
- **Riport:** **Allure** tesztriport.

## 5. Tesztelt funkcióterületek

| Terület | Példa követelmény-ID | Manuális | Automata |
|---|---|:--:|:--:|
| Bejelentkezés, adatkezelési nyilatkozat | LOGIN-REQ, PRIVACY-REQ | ✔ | ✔ (UI) |
| Regisztráció + megerősítés | AUTH-REQ | ✔ | ✔ (UI) |
| Vezérlőpult | DASH-REQ | ✔ | ✔ (UI) |
| Rendszerek listája, lapozás | SYSTEM-REQ | ✔ | ✔ (UI) |
| Új rendszer felvitele | CREATE-REQ | ✔ | ✔ (UI) |
| Módosítás | EDIT-REQ | ✔ | ✔ (UI) |
| Törlés | EDIT-REQ | ✔ | ✔ (UI) |
| Import | IMPORT-REQ | ✔ | ✔ (UI) |
| Felületről mentés | POLICY-REQ | ✔ | ✔ (UI) |
| Kijelentkezés / munkamenet | SESSION-REQ | ✔ | ✔ (UI) |
| Jogosultság / adatszeparáció | SEC-REQ, API-REQ | ✔ | ✔ (API) |

## 6. Környezet és eszközök

- **Nyelv/build:** Java 21, Maven
- **Tesztkeret:** JUnit 5
- **UI-automatizálás:** Selenium WebDriver (Chrome/Chromium)
- **API-tesztelés:** RestAssured
- **Riport:** Allure
- **CI:** GitHub Actions (headless futtatás `xvfb`-vel; `@Tag("gmail")` és `@Tag("flaky")` kizárva)
- **Manuális futtatás:** valós böngésző (Chrome), a SUT éles példánya
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
- A build lefordul, a függőségek rendelkezésre állnak (automata futáshoz).

## 9. Kilépési kritériumok

- A tervezett manuális és automata tesztesetek lefutottak.
- A CI zöld: minden nem kizárt (`gmail`, `flaky`) automata teszt sikeres.
- Az Allure riport elkészült; a manuális futás eredménye dokumentált.
- A hibák dokumentáltak, a követelmény-lefedettség a traceability táblában igazolt.

## 10. Kockázatok és kezelésük

| Kockázat | Kezelés |
|---|---|
| Külső e-mail (Gmail API) lassú/instabil | `@Tag("gmail")` – CI-ben kizárva; admin-linkes megerősítés stabil alternatívaként |
| Időzítéstől függő, ingadozó teszt | `@Tag("flaky")` – CI-ben kizárva, külön futtatható |
| Külső szolgáltatás (Supabase/Vercel) kiesése | belépési kritérium ellenőrzése; a hiba egyértelműen jelződik |
| Titkos kulcs kiszivárgása | kizárólag titokként; a riport publikálás előtt szűrve |

## 11. Leszállítandók

- Manuális teszteset-végrehajtás dokumentálva (eredmények).
- Automatizált tesztkód (UI + API), a repóban.
- Allure tesztriport.
- Teszt-dokumentáció (teszteset–követelmény megfeleltetés).
- Vezetői tesztjelentés.
- GitHub Actions CI-konfiguráció és -futások.

## 12. Ütemezés és felelős

- **Felelős:** Varga Andrea (a tesztek tervezése, manuális végrehajtása, automatizálása, kiértékelése).
- **Ütemezés:** a manuális ellenőrzés és az automatizálás a fejlesztéssel párhuzamosan készült; a
  regressziós automata suite minden `master`-re történő push és pull request esetén automatikusan lefut.

## 13. Kapcsolódó dokumentumok

- [`teszt-dokumentacio`](teszt-dokumentacio) – teszteset–követelmény megfeleltetés (traceability)
- [`vezetoi-tesztjelentes.md`](vezetoi-tesztjelentes.md) – vezetői tesztjelentés
- `README.md` – telepítés, futtatás, riport
