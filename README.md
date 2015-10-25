# Spring Social

[Spring Social] is an extension of the [Spring Framework] that helps you connect your applications with Software-as-a-Service (SaaS) providers such as Facebook and Twitter.


## Features

- An extensible service provider framework that greatly simplifies the process of connecting local user accounts to hosted provider accounts.

- A connect controller that handles the authorization flow between your Java/Spring web application, a service provider, and your users.

- Java bindings to popular service provider APIs such as Facebook, Twitter, LinkedIn, TripIt, and GitHub.

- A sign-in controller that enables users to authenticate with your application by signing in through a service provider.

- Much more...


## Download Artifacts

See [downloading Spring artifacts] for Maven repository information. Unable to use Maven or other transitive dependency management tools? See [building a distribution with dependencies].

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.social</groupId>
    <artifactId>spring-social-core</artifactId>
    <version>${org.springframework.social-version}</version>
</dependency>

<dependency>
    <groupId>org.springframework.social</groupId>
    <artifactId>spring-social-web</artifactId>
    <version>${org.springframework.social-version}</version>
</dependency>
```

### Repositories

```xml
<repository>
    <id>spring-repo</id>
    <name>Spring Repository</name>
    <url>http://repo.spring.io/release</url>
</repository>   
    
<repository>
    <id>spring-milestone</id>
    <name>Spring Milestone Repository</name>
    <url>http://repo.spring.io/milestone</url>
</repository>

<repository>
    <id>spring-snapshot</id>
    <name>Spring Snapshot Repository</name>
    <url>http://repo.spring.io/snapshot</url>
</repository>
```


## Spring Social Modules

The core [Spring Social] project does not contain provider modules. Each of the provider modules is in its own project. This enables those modules to progress and release on a separate schedule than Spring Social and be able to react more quickly to changes in the provider's API without the need to wait for a Spring Social release. The SaaS provider-specific projects can be cloned from the following GitHub URLs:

- Twitter: [GitHub][twitter-gh] | [Reference][twitter-ref] | [API][twitter-api]

- Facebook: [GitHub][facebook-gh] | [Reference][facebook-ref] | [API][facebook-api]

- LinkedIn: [GitHub][linkedin-gh] | [Reference][linkedin-ref] | [API][linkedin-api]

- TripIt: [GitHub][tripit-gh]

- GitHub: [GitHub][github-gh]


## Documentation

See the current [Javadoc] and [reference docs]. To get up and running quickly using the project, see the [Quick Start] guide.


## Sample Applications

Several example projects are available in the [samples repository].


## Issue Tracking

Report issues via the [Spring Social JIRA]. While JIRA is preferred, [GitHub issues] are also welcome. Understand our issue management process by reading about [the lifecycle of an issue].


## Build from Source

1. Clone the repository from GitHub:

    ```sh
    $ git clone https://github.com/spring-projects/spring-social.git
    ```

2. Navigate into the cloned repository directory:

    ```sh
    $ cd spring-social
    ```

3. The project uses [Gradle] to build:

    ```sh
    $ ./gradlew build
    ```
        
4. Install jars into your local Maven cache (optional)

    ```sh
    $ ./gradlew install
    ```


## Import Source into your IDE

### Eclipse

1. To generate Eclipse metadata (.classpath and .project files):

    ```sh
    $ ./gradlew eclipse
    ```

2. Once complete, you may then import the projects into Eclipse as usual:

   ```
   File -> Import -> Existing projects into workspace
   ```

> **Note**: [Spring Tool Suite][sts] has built in support for [Gradle], and you can simply import as Gradle projects.

### IDEA

Generate IDEA metadata (.iml and .ipr files):

```sh
$ ./gradlew idea
```


## Contributing

[Pull requests] are welcome. See the [contributor guidelines] for details.


## License

[Spring Social] is released under version 2.0 of the [Apache License].


[Spring Social]: http://projects.spring.io/spring-social
[Spring Framework]: http://projects.spring.io/spring-framework
[downloading Spring artifacts]: https://github.com/spring-projects/spring-framework/wiki/Downloading-Spring-artifacts
[building a distribution with dependencies]: https://github.com/spring-projects/spring-framework/wiki/Building-a-distribution-with-dependencies
[twitter-gh]: https://github.com/spring-projects/spring-social-twitter
[twitter-ref]: http://docs.spring.io/spring-social-twitter/docs/current/reference/htmlsingle/
[twitter-api]: http://docs.spring.io/spring-social-twitter/docs/current/apidocs/
[facebook-gh]: https://github.com/spring-projects/spring-social-facebook
[facebook-ref]: http://docs.spring.io/spring-social-facebook/docs/current/reference/htmlsingle/
[facebook-api]: http://docs.spring.io/spring-social-facebook/docs/current/apidocs/
[linkedin-gh]: https://github.com/spring-projects/spring-social-linkedin
[linkedin-ref]: http://docs.spring.io/spring-social-linkedin/docs/1.0.x/reference/htmlsingle/
[linkedin-api]: http://docs.spring.io/spring-social-linkedin/docs/1.0.x/api/
[tripit-gh]: https://github.com/spring-projects/spring-social-tripit
[github-gh]: https://github.com/spring-projects/spring-social-github
[Javadoc]: http://docs.spring.io/spring-social/docs/current/api/
[reference docs]: http://docs.spring.io/spring-social/docs/current/reference/html/
[samples repository]: https://github.com/spring-projects/spring-social-samples
[Quick Start]: https://github.com/spring-projects/spring-social/wiki/Quick-Start
[Spring Social JIRA]: http://jira.springsource.org/browse/SOCIAL
[GitHub issues]: https://github.com/spring-projects/spring-social/issues
[the lifecycle of an issue]: https://github.com/spring-projects/spring-framework/wiki/The-Lifecycle-of-an-Issue
[Gradle]: http://gradle.org
[sts]: https://spring.io/tools
[Pull requests]: http://help.github.com/send-pull-requests
[contributor guidelines]: https://github.com/spring-projects/spring-framework/blob/master/CONTRIBUTING.md
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
