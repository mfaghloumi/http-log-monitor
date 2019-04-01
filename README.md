# Challenge

## HTTP log monitoring console program

Write a simple console program that monitors HTTP traffic on your machine. Consume an actively written-to [w3c-formatted](https://www.w3.org/Daemon/User/Config/Logging.html) HTTP access log . It should default to reading /tmp/access.log and be overrideable

Example log lines:

```
127.0.0.1 - james [09/May/2018:16:00:39 +0000] "GET /report HTTP/1.0" 200 123
127.0.0.1 - jill [09/May/2018:16:00:41 +0000] "GET /api/user HTTP/1.0" 200 234
127.0.0.1 - frank [09/May/2018:16:00:42 +0000] "POST /api/user HTTP/1.0" 200 34
127.0.0.1 - mary [09/May/2018:16:00:42 +0000] "POST /api/user HTTP/1.0" 503 12
```

- Display stats every 10s about the traffic during those 10s: the sections of the web site with the most hits, as well as interesting summary statistics on the traffic as a whole. A section is defined as being what's before the second '/' in the resource section of the log line. For example, the section for "/pages/create" is "/pages"
- Make sure a user can keep the app running and monitor the log file continuously
- Whenever total traffic for the past 2 minutes exceeds a certain number on average, add a message saying that “High traffic generated an alert - hits = {value}, triggered at {time}”. The default threshold should be 10 requests per second, and should be overridable.
- Whenever the total traffic drops again below that value on average for the past 2 minutes, add another message detailing when the alert recovered.
- Make sure all messages showing when alerting thresholds are crossed remain visible on the page for historical reasons.
- Write a test for the alerting logic.
- Explain how you’d improve on this application design.
- If you have access to a linux docker environment, we'd love to be able to docker build and run your project! If you don't though, don't sweat it. As an example for a solution based on python 3:

``` dockerfile
FROM python:3
RUN touch /var/log/access.log  # since the program will read this by default
WORKDIR /usr/src
ADD . /usr/src
ENTRYPOINT ["python", "main.py"] # this is an example for a python program, pick the language of your choice
```

# Solution

## Design

The purpose of the challenge is to provide a tool that can tail an access log file, provide statistics and send alerts. Which is similar to a teeny tiny datadog.

Since the tool will be installed and downloaded on multiple machines I felt important that it stays as small as possible.

As of now the tool gives only one data producer and two consumers. But tomorrow it may be much more. So not only it needed to be extensible, but also scalable (Much more data to process it if more sources).

So, I started with the language I'm confortable Java. And since I wanted something small, I tried to avoid frameworks as much as possible with the time I was given. So instead of using `Spring integration` or `Akka Actors` I kept focused on Java SE.

Regarding the second part, extensibility, we have a data source which is the log file and multiple consumers the alerting, the statistics, and maybe tomorrow others. So the choice of separating the consumers from the producers seems legit, and therefore I choose to implement a simple pubsub pattern.

The other handy advantage of the pubsub pattern is that we have a message broker which is now an in memory `Blocking queue` and can be tomorrow a `Kafka` or a `RabbitMQ`, if needed.

## Calculations

Regarding the calculations, my main purpose was to not keep all the hits in memory so I can aggregate them. But instead process the data on the fly and only keep the minimum data.

* Statistics : Hits per sections

So for the statistics part on sections I choose to use a thread safe map that maintain a lock per bucket and use CAS operations for updating. On each request on a specific section I updated the related statistics (the hitcount is one of them) but also the content delivered per section and most frequent users per section.

Each 10 seconds this map get ordered and the most visited sections get printed with their respective statistics.

* Alerting : Hits count

Regarding the alerting we should calculate a moving average, to do so, I aggregated the hits per second and stored them in a FIFO queue. And each second the average is calculated on this queue. And then alert if needed.

## Improvements

* In the continuity of having a smaller and more performant tool, we could use graalvm and docker's multistage build.

* As of it is now I use some libraries like guava and apache commons, the second point of improvement would be to get rid of those.

* Also the tailing library used `commons-io` uses polling behind the scenes to check for new updates on the file. We could image a more performant way of doing this either by using the native tail command or the watch service from Java 7.

* We could also separate each of the producer and publishers in smaller modules that can be assembled together depending on the use case, as a plug-in system.

* And finally some BDD tests and a CI/CD are more than welcome

## How to use

Prerequisites: Java 8+

```bash
git clone https://github.com/mfaghloumi/http-log-monitor.git
cd http-log-monitor
./mvnw clean install
cd target
touch access.log # You can pipe the logs to this file
java -jar  -f access.log -t 10
```

Using docker : 

Prerequisites: Docker 17.06.0+

```bash
git clone https://github.com/mfaghloumi/http-log-monitor.git
cd http-log-monitor
touch access.log # You can pipe the logs to this file
docker-compose up
```

## Misc

I left the code with nearly no comment, as I believe the code should describe itself.