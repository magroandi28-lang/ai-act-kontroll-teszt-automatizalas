# Vezetői tesztjelentés – AI Act Kontroll

**Projekt:** AI Act Kontroll – teszt-automatizálás
**Tesztelt alkalmazás:** https://energia-ai-kontroll.vercel.app
**Készítette:** Varga Andrea
**Dátum:** 2026-09-14
**Repó:** github.com/magroandi28-lang/ai-act-kontroll-teszt-automatizalas

## 1. Összefoglaló
Az AI Act Kontroll webalkalmazás fő felhasználói folyamataira automatizált teszt-suite készült
(felületi és API szinten). A tesztek a folyamatos integrációban (GitHub Actions) minden feltöltéskor
automatikusan lefutnak, és az eredmény Allure-riportban tekinthető meg. A stabil tesztek zölden futnak;
a külső, megbízhatatlan (flaky) eseteket a pipeline szándékosan kihagyja, helyben viszont futtathatók.

## 2. Teszt-terjedelem
- **Felületi (end-to-end) tesztek – Selenium:** belépés, regisztráció, adatkezelési nyilatkozat,
  vezérlőpult, rendszerek listázása és lapozása, új rendszer felvitele, tömeges import,
  módosítás, törlés, szabályzat mentése felületről, kijelentkezés, demó-izoláció.
- **API tesztek – RestAssured (Supabase REST/Auth):** jogosultsági kapu, szervezeti adatizoláció,
  teljes CRUD-életciklus, módosításvédelem (negatív + pozitív eset).

## 3. Lefedett kötelező funkciók
| Funkció | Lefedve |
|---|---|
| Regisztráció | Igen |
| Bejelentkezés | Igen |
| Adatkezelési nyilatkozat | Igen |
| Adatok listázása | Igen |
| Több oldalas lista bejárása | Igen |
| Új adat bevitel | Igen |
| Sorozatos adatbevitel adatforrásból (import) | Igen |
| Meglévő adat módosítása | Igen |
| Adat törlése | Igen |
| Adatok lementése felületről | Igen |
| Kijelentkezés | Igen |

## 4. Eredmények
- A CI-ban futtatott tesztek **sikeresen (zölden) lefutnak** (GitHub Actions, legutóbbi futás: sikeres).
- Az egyes tesztek pontos száma és részletei az **Allure-riportban** láthatók.
- Bukáskor a felületi teszteknél automatikus **képernyőkép** készül a hiba dokumentálásához.

## 5. Talált és dokumentált hibák (hibajegyek)
| Jegy | Terület | Leírás |
|---|---|---|
| KAN-4 | Demó jogosultság | A demó belépés csak editor jogot ad, nem owner-t |
| KAN-5 | Regisztráció-megerősítés | E-mailes megerősítés folyamata |
| KAN-6 | Import | Tömeges import ellenőrzése |

## 6. Szándékosan kihagyott (flaky) tesztek a CI-ban
| Teszt | Ok | Pótlás |
|---|---|---|
| Gmail-alapú megerősítés | Külső e-mail/OAuth függés, megbízhatatlan | Admin-linkes megerősítés (stabil) |
| Élő regisztráció (sikeresRegisztracio) | Élő Supabase signup, korlát/lassulás miatt flaky | Regisztrációs mezővalidációk + admin-linkes folyamat |

Ezek a tesztek **helyben futtathatók**, csak a pipeline hagyja ki őket a stabilitás érdekében.

## 7. Automatizálás és riport
- **CI:** GitHub Actions – minden `master`-re történő push/PR esetén lefut (JDK 21, fejléc nélküli Chrome).
- **Riport:** Allure – automatikusan generált, a kérések/válaszok és lépések részleteivel.
- **Követelmény-követhetőség:** minden teszt a specifikáció követelmény-azonosítójához kötött (pl. LOGIN-REQ-001, SEC-REQ-006).

## 8. Következtetés
A fő felhasználói folyamatok automatizált lefedettsége teljes, a tesztek a CI-ban stabilan futnak,
az eredmény riportban és hibajegyekben dokumentált. A megoldás megfelel az elvárt tesztelési
gyakorlatoknak (követhetőség, újrafuthatóság, automatizált jelentés).