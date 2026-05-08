# downloader

Modul pro stahování Markdown obsahu ze zdrojového projektu.

Aktuálně registruje handler pro `DownloadMarkdownPageCommand`. Handler přes `MarkdownPageDownloader` stáhne vzdálený obsah pomocí HTTP GET, připraví bezpečný název `.md` souboru a vrátí `DownloadedMarkdownPage` zpět přes command bus.
