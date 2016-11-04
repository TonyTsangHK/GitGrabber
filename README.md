GitGrabber v0.4.6
===

Prerequisite:
  - Gradle
  - JDK 1.8

Setup
---

For initial setup download customScripts/initialSetup.sh.

Make sure gradle path is setup properly

run initialSetup.sh, e.g. ./initialSetup.sh /path/to/target/parent/folder

unzip the distribution zip & setup your path variable to include the bin folder

Or download the [Latest release](../../releases/latest)

Command line usage of GitGrabber
---

```
    -v, --version
            Show GitGrabber version
    -h, --help
            show help message
    -c, --config
            Config file path

    Example:
    GitGrabber -c dependency.json

    The following command options must be used together to grab remote repository then its dependencies!

    -t, --target
            Main project target path
    -r, --remote, --remoteUrl
            Remote repository URL
    -b, --branch (optional, default to master)
            default branch of main project
    -d, --dependency (optional, default to dependency.json)
            Dependency config file relative to main project path

    Example:
    GitGrabber -r https://github.com/TonyTsangHK/GitGrabber.git -t /path/to/local/GitGrabber -b master -d dependency.json
    
    Or omitting branch and dependency parameter (default to master branch and dependency.json as dependency config file)
    
    GitGrabber -r https://github.com/TonyTsangHK/GitGrabber.git -t /path/to/local/GitGrabber
```

GitGrabber config can be a json file or embedded within start tag (@->) and end tag (<-@) pattern in other file.
Embedded example:
Embedding within settings.gradle:

```
rootProject.name = 'GitGrabber'

/*@->
{
    ... configs goes here
}
<-@*/
```

Config format
---
```JSON
{
    "protocol": "(protocol, ssh / http / https, optional default to ssh)",
    "host": "(host, required)",
    "user": "(user, required for ssh)",
    "port": "(port, integer, optional default to 22 (ssh), 443 (https), 80 (http))",
    "parentFolder": "(parent folder for all projects, optional default to parent folder of config file, relative path to this config file or absolute path is accepted)",
    "mainProject": "(main project config, required)",
    "requiredProjects": [
        "(project configs ...)"
    ]
}
```

Project config:
---
```JSON
{
    "name": "(project name)",
    "local": "(local folder relative to config file's parent folder)",
    "remoteUrl": "(relative remote repository url without protocol, host, user, port)",
    "fullRemoteUrl": "(full remote repository url, this will override remoteUrl if both are present)",
    "defaultBranch": "(default branch, optional default to empty string)",
    "updateWhenExists": "(pull when project exists, optional default to false)"
}
```