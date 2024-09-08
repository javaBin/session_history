# JavaZone History

What is this? TBD :)


## Elastic

Development running - user elastic, password elastic.

Start elastic in docker - `docker compose up -d`

Notes - at present - indexing takes about 4 mins on dev laptop. Comment out some endpoints in application.conf if you don't want to wait.

Calls to API will return 503 until the index is ready.

You can check state with /api/state (searching will only work when state is INDEXED).

## Bruno

REST client for testing.

Available from [Use Bruno](https://www.usebruno.com/)

Import the collection in ./bruno
