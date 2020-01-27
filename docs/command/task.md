Task
====
> Gradle wrapper is recommended for no other reason

# 1. :api

> `gradlew api:detekt`
> > Static check api source

> `gradlew api:bootRun`
> > Run Api application

> `gradlew api:build`
> > Build Api application -> .jar application

> `gradlew api:docker`
> > Build Api application -> docker image
>
> `docker save -o smartCradle.tar com.choistec.smartcradle/api`
> > Extract docker image -> .tar File

# 2. :mqtt:publisher

> `gradlew publisher:detekt`
> > Static check publisher source

> `gradlew mqtt:publisher:run`
> > Run publisher application. This task don't use yml configuration

> `gradlew mqtt:publisher:build`
> > Build publisher application -> .jar application

# 3. :mqtt:subscriber

> `gradlew subscriber:detekt`
> > Static check subscriber source

> `gradlew mqtt:subscriber:run`
> > Run subscriber application. This task don't use option.yml

> `gradlew mqtt:subscriber:build`
> > Build subscriber application -> .jar application
>
> `SmartCradleSubscriber.bat install`
> > Install subscribers with Windows Services
>
> `SmartCradleSubscriber.bat remove`
> > Remove subscribers with Windows Services
>
> `docker build -t subscriber mqtt/subscriber/archive`
> > Remove subscribers with Windows Services
>
> `docker save -o subscriber.tar subscriber`
> > Extract docker image -> .tar File
