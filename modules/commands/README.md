# commands

Společné command kontrakty a synchronní command bus pro moduly aplikace.

Modul neobsahuje HTTP ani konkrétní implementace use-casů. API z něj používá command typy a `CommandBus`; implementační moduly registrují `CommandHandler`.
