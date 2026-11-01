# Feature 3 : Convert the application from a REST API to a CLI batch job

`my-products-jobs` currently exposes jobs through a REST endpoint (`JobRest`): an HTTP call starts a job.

We want to turn it into a **command-line application**: it starts, runs one job named on the command line, then exits.

No web server stays running.

Before: `POST /jobs/{name}` → web server running → job starts
After:  `java -jar app.jar <jobName>` → job runs → JVM exits

This suits jobs launched by a scheduler (Kubernetes CronJob, cron, CI pipeline), because the process ends when the work
is done.
