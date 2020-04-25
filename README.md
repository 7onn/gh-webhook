# starting with clojure
The project uses [Midje](https://github.com/marick/Midje/).

## How to run the tests
- `lein midje` will run all tests.
- `lein midje :autotest` will run all the tests indefinitely. It sets up a
watcher on the code files. If they change, only the relevant tests will be
run again.

## How to start the program
- `lein run` will run the core namespace set at `project.clj`

## Available on docker
- `docker build -t gh-webhook:latest .`
- `docker run -p 3000:3000 gh-webhook:latest`

## Http Availability
- http://localhost:3000/