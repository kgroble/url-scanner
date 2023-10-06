# URL Scanner

API for scanning and extracting metadata from web URLs

## Setup

### Dependencies

* Java 17
* PostgreSQL
* Maven packages as specified in `pom.xml`

### Running

* Run the `init-db.sql` script with superuser access
* Connect to the `url_scanner` database and run the `init-schema.sql` script
* Run `Main.main`

## Usage
* `GET /scan`
  * required query param `target`: the URL to scan
  * returns metadata about the URL and download link to a screenshot
* `GET /screenshot/:id`
  * returns screenshot as a PNG binary

Example:
```
[12:26:19] [~]
  ↬ http GET localhost:8080/scan target=='https://example.com'
HTTP/1.1 200 OK
Content-Type: application/json
Date: Fri, 06 Oct 2023 19:37:52 GMT
Server: Jetty(9.4.48.v20220622)
Transfer-Encoding: chunked

{
    "asn": "AS15133 Edgecast Inc.",
    "ipAddress": "93.184.216.34",
    "pageSource": "<!DOCTYPE html><html><head>\n    <title>Example Domain</title>\n\n    <meta charset=\"utf-8\">\n    <meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\">\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n    <style type=\"text/css\">\n    body {\n        background-color: #f0f0f2;\n        margin: 0;\n        padding: 0;\n        font-family: -apple-system, system-ui, BlinkMacSystemFont, \"Segoe UI\", \"Open Sans\", \"Helvetica Neue\", Helvetica, Arial, sans-serif;\n        \n    }\n    div {\n        width: 600px;\n        margin: 5em auto;\n        padding: 2em;\n        background-color: #fdfdff;\n        border-radius: 0.5em;\n        box-shadow: 2px 3px 7px 2px rgba(0,0,0,0.02);\n    }\n    a:link, a:visited {\n        color: #38488f;\n        text-decoration: none;\n    }\n    @media (max-width: 700px) {\n        div {\n            margin: 0 auto;\n            width: auto;\n        }\n    }\n    </style>    \n</head>\n\n<body>\n<div>\n    <h1>Example Domain</h1>\n    <p>This domain is for use in illustrative examples in documents. You may use this\n    domain in literature without prior coordination or asking for permission.</p>\n    <p><a href=\"https://www.iana.org/domains/example\">More information...</a></p>\n</div>\n\n\n</body></html>",
    "redirects": [
        "https://example.com/"
    ],
    "screenshotUrl": "localhost:8080/screenshot/d203d0ae-6196-4b8c-bc69-ac7dde5d4394",
    "sslInfo": {
        "issuer": "DigiCert TLS RSA SHA256 2020 CA1",
        "protocol": "TLS 1.3",
        "subjectName": "www.example.org",
        "validFrom": 1673568000.0,
        "validTo": 1707868799.0
    }
}

```

## License


Copyright 2023 Kieran Groble

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
