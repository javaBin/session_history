# JavaZone History

What is this? TBD :)

## Elastic

Development running - user elastic, password elastic.

Start elastic in docker - `docker compose up -d`

Notes - at present - indexing takes about 4 mins on dev laptop. Comment out some endpoints in application.conf if you don't want to wait.

If you have a valid complete index from an earlier run - you can start the app with `ELASTIC_SKIP_INDEX=true` in the environment and it will skip the indexing and use the existing data.

Calls to API will return 503 until the index is ready.

You can check state with /api/state (searching will only work when state is INDEXED).

## Bruno

REST client for testing.

Available from [Use Bruno](https://www.usebruno.com/)

Open the collection in [the bruno folder](./bruno)

## API

### GET /api/search/videos

returns title, video and year for all sessions that have a video link

### GET /api/search/state

returns the indexing state - `NEW`/`MAPPED`/`INDEXED`

### POST /api/search 

`{ "query": "searchString" }`

simple text search

returns matches and some aggregate information

A search of `{ "query": "*" }` returns aggregate for all documents but no actual hits (used for initial page load)

## Local running

Start the ktor Application.kt - if you've a valid index and don't need to update - set `ELASTIC_SKIP_INDEX=true` in the env.

Start the frontend - `npm run dev` - this starts on port 3000 and proxies /api to the ktor app on port 8080.

## Building

Gradle build of the system calls npm run build then copies the frontend build into the /static directory of the ktor app. This gets served on the root URL so avoids any need for CORS.