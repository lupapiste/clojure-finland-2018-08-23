# clojure-finland-2018-08-23

Sample application to demonstrate how you can extract I/O from commands. Built
for [Clojure Finland 2018-08-23 meetup](https://www.meetup.com/Helsinki-Clojure-Meetup/events/253676481/).

## Usage

Start MongoDB docker container:

```bash
cd docker
docker-compose up
```

Run tests:

```bash
lein eftest
```

Run REPL:

```bash
lein repl
```

## Speaker notes

```
http post :3300/api/apply-fixture

http post :3300/api/find-by-email email=sonja@example.com 'done?':=false

http post :3300/api/create email=sonja@example.com todo=Hullo

http post :3300/api/find-by-email email=sonja@example.com 'done?':=false

http post :3300/api/mark-done _id=3d9ad500-a4a0-11e8-b269-51aa698ce291

http post :3300/api/find-by-email email=sonja@example.com 'done?':=false

http post :3300/api/find-by-email email=sonja@example.com 'done?':=true
```

## License

Copyright © 2018 [Evolta Ltd](http://evolta.fi)

Distributed under the Eclipse Public License either version 2.0 or (at your option) any later version.
