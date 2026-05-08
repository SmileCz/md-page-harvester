# md-page-harvester

Micronaut modular monolith pro budoucí stahování `.md` stránek z jednoho projektu.

Aktuální stav je záměrně jen kostra: aplikace má startovací bod, rozdělení do modulů, Docker build a Portainer stack. Logika stahování, ukládání a plánování se doplní později.

## Struktura

```text
apps/service              Micronaut runtime a kompozice celé aplikace
modules/cqrs              společné CQRS kontrakty, command bus a query bus
modules/api               HTTP rozhraní a budoucí DTO/controllery
modules/catalog           model zdrojového projektu a metadat Markdown stránek
modules/downloader        hranice pro stahování Markdown souborů
modules/storage           hranice pro perzistenci stažených stránek
modules/scheduler         orchestrace a budoucí joby
deploy/portainer          Portainer stack
docs                      architektonické a provozní poznámky
```

## Lokální spuštění přes Docker

```bash
docker compose up --build
```

Aplikace běží na Java 26 a poslouchá na portu `8080`. Health endpoint je připravený na `/health`.

## Portainer

Použij `deploy/portainer/stack.yml`. Stack očekává image `md-page-harvester:latest`, případně nastav proměnnou `IMAGE_NAME` na image z registry.

## Další kroky

1. Definovat zdroj projektu, ze kterého se budou `.md` soubory stahovat.
2. Doplnit downloader port/adaptér podle typu zdroje.
3. Rozhodnout, jestli storage bude filesystem, databáze, nebo kombinace.
4. Doplnit API a scheduler.
