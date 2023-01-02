# Ignite

*Ignite is part of [AppForge](https://github.com/bitshifted/appforge) platform.*

Ignite is a GUI tools for generating deployment configuration files. These files are consumed by [Ignite Maven Plugin](https://github.com/bitshifted/ignite-maven-plugin) 
and are used to perform actual deployment of the application.

Deployment files are in  plain YAML format, and they can also be edited by hand. This tools simplifies the process for developers in a 
sense that they don't need to remember specific configuration options. Rather, using the tool configuration files can be generated quickly
and efficiently.

# License

Ignite is released under Mozilla Public License 2.0. for details, see [LICENSE](./LICENSE) file.

# Building and running

Ignite is written in Java and uses JavaFX for user interface. Build requirements are:

* Java 17 or higher
* Maven 3

To build the application, run `mvn install` command.

To run it from command line, run

```shell
mvn javafx:run@run
```

This will execute JavaFX Maven plugin and launch the application

## Debugging

For debugging purposes, process is the following:

1. Run `mvn javafx:run@debug`
2. Attach the debugger to the running process


