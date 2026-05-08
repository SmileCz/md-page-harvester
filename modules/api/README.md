# api

HTTP hranice aplikace.

Aktuálně obsahuje základní endpoint pro stažení vzdálené Markdown stránky jako `.md` souboru:

```http
POST /api/pages/download
Content-Type: application/json
Accept: text/markdown
```

```json
{
  "uri": "https://example.com/docs/api-guide.md",
  "title": "Api Guide",
  "fileName": "api-guide.md"
}
```

Odpověď je `text/markdown` s hlavičkou `Content-Disposition: attachment`. Kontroler drží jen HTTP mapování a vytvoření query; samotné stažení i pojmenování výsledného souboru řeší `modules/downloader` přes společný `QueryBus`.
