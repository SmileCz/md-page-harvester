# cqrs

Společné CQRS kontrakty aplikace.

Command strana je určena pro operace, které mění stav, a nevrací aplikační data. Query strana je určena pro čtecí operace a vrací výsledek.

## CommandBus

`DefaultCommandBus` není jen přímý dispatcher. Při `dispatch` command projde těmito kroky:

1. validation přes `CommandValidator`
2. authorization přes `CommandAuthorizer`
3. idempotency key z `CommandDispatchOptions`, případně z `IdempotentCommand`
4. uložení do `CommandStore`
5. execution handleru uvnitř `CommandTransactionBoundary`
6. audit/logging přes `CommandAuditListener`
7. retry rozhodnutí přes `RetryPolicy`
8. error mapping přes `CommandErrorMapper`

Výchozí Micronaut bean `FileCommandStore` ukládá command journal do souboru
`md-page-harvester.command-bus.store-file`. Lokálně je výchozí cesta
`build/command-bus/commands.bin`; v Docker/Portainer konfiguraci je nastavená na
`/data/command-bus/commands.bin`, tedy do persistentního volume.

Po výpadku aplikace zavolej `CommandBus.recoverPendingCommands()`. Zpracuje commandy ve stavech
`PENDING`, `PROCESSING` a retry-ready `FAILED`.

Když command nemá explicitní idempotency key, bus mu dá unikátní intent id a nebude slučovat dvě
stejně vypadající operace.

Balíčky jsou rozdělené podle odpovědnosti:

- `command` - veřejné command kontrakty
- `command.bus` - implementace busu a chyby registrace handlerů
- `command.validation` - validation hooky
- `command.authorization` - authorization hooky
- `command.store` - persistentní journal a uložené commandy
- `command.retry` - retry policy a retry decision
- `command.transaction` - transaction boundary
- `command.audit` - audit/logging listener
- `command.error` - error mapping a dispatch výjimky
- `command.idempotency` - interní resolver idempotency key
