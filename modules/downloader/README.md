# downloader

Modul pro stahování Markdown obsahu ze zdrojového projektu.

Aktuálně registruje handler pro `DownloadMarkdownPageQuery`. Handler přes `MarkdownPageDownloader` stáhne vzdálený obsah pomocí HTTP GET, připraví bezpečný název `.md` souboru a vrátí `DownloadedMarkdownPage` zpět přes query bus.
