# Frontend

## Requirements

This is currently written in vue - but that's just to get something quick up and running. There are two requirements:

* Builds with gradle. It should be an npm based project that builds with npm build (standard).
* Proxy - it should proxy any calls to /api/* to http://localhost:8080/api/* when run in dev mode.

All client to server calls should be relative and start "/api".
