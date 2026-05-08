# Modular monolith

Projekt je rozdeleny na jeden spustitelny Micronaut modul a nekolik knihovnich modulu.

## Moduly

`apps/service` je runtime aplikace. Obsahuje `Application`, konfiguraci, logovani a zavislosti na vsechny vnitrni moduly.

`modules/cqrs` obsahuje spolecne CQRS kontrakty, synchronni command bus a query bus. API vytvari commandy nebo query, implementacni moduly registruji handlery.

`modules/catalog` bude drzet model zdrojoveho projektu, cest k `.md` souborum a metadat stranek.

`modules/downloader` bude obsahovat porty a adaptery pro stazeni Markdown obsahu ze zdroje.

`modules/storage` bude obsahovat porty a adaptery pro ulozeni stazenych stranek.

`modules/scheduler` bude ridit pravidelne nebo manualni stahovani.

`modules/api` bude vystavovat HTTP rozhrani pro stav, konfiguraci a manualni spusteni.

## Pravidla zavislosti

`apps/service` muze zaviset na vsech modulech.

`modules/api` muze volat `modules/scheduler` a cist modely z `modules/catalog`.

`modules/api` muze dispatchovat commandy a query pres `modules/cqrs`, ale nema obsahovat implementaci stahovani, ukladani ani planovani.

`modules/scheduler` muze skladat `modules/downloader`, `modules/storage` a `modules/catalog`.

`modules/downloader` a `modules/storage` nemaji zaviset na `modules/api`.

`modules/downloader` muze implementovat handlery query nebo commandu z `modules/cqrs`.
