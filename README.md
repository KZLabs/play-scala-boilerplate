# Play + Scala boilerplate

## Prerequisites

You will need the following things properly installed on your computer.

* [Git](http://git-scm.com/)
* [SBT](http://www.scala-sbt.org/download.html)
* [Docker](https://www.docker.com/)
* [Docker Compose](https://docs.docker.com/compose/)

## Installation / Initial setup

* `git clone git@github.com:{repo}`
* change into the new directory
* `rm -rf .git && git init` (optional, make it yours)
* `docker-compose up`(may take a while)
* `sbt`
* `db migrate`
* `project api` and then `run` or `s` or from main console `sbt s`
* Visit your api at [http://localhost:9000](http://localhost:9000).
