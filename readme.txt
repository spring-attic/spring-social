================================ Spring Social ===============================
To check out the project and build from source, do the following:

 git clone --recursive git://git.springsource.org/spring-social/spring-social.git
 cd spring-social
 ./gradlew build

Note: the --recursive switch above is important, as spring-social uses
git submodules, which must themselves be cloned and initialized. If --recursive
is omitted, doing so becomes a multi-step process. 

If you've already cloned the Spring-Social repository without the --recursive
switch, you'll need to pull the submodules with the following steps:

 git submodule update --init

If you encounter heap space errors during the build, increase the heap size for
gradle:
 GRADLE_OPTS="-Xmx1024m"

-------------------------------------------------------------------------------
To generate Eclipse metadata (.classpath and .project files), do the following:

 ./gradlew eclipse

Once complete, you may then import the projects into Eclipse as usual:

 File -> Import -> Existing projects into workspace

Browse to the 'spring-social' root directory. All projects should import
free of errors.

-------------------------------------------------------------------------------
To generate IDEA metadata (.iml and .ipr files), do the following:

 ./gradlew idea

-------------------------------------------------------------------------------
To build the JavaDoc, do the following from within the root directory:

 ./gradlew :docs:api

The result will be available in 'docs/build/api'.

===============================================================================
