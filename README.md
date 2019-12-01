[![Build Status](https://travis-ci.com/deltonvaz/sgit.svg?branch=master)](https://travis-ci.com/deltonvaz/sgit)

# SGIT

A Scala-based git-like code source manager

---

### Prerequisites

* openjdk8, 
* Scala 2.13.10+
* sbt version 1.3.2+

## Usage

1. Use `make` to generate `sgit` 
    1. This command will run `sbt assembly` to generate a sgit executable into sgit directory
    
    2. If you want to use `sgit` as any other command then you need to add it to your PATH using the following command `export PATH=$PATH:/path/to/sgit`
        
    3. If you want to use `sgit` as any other command then you need to add it to your PATH using the following command:

2. To use `sgit` commands:

    - Default `./sgit commandName commandParameter(s)`
    - Using java `java -jar ./sgit commandName commandParameter(s)`


## Available Commands

 - [x] sgit init
 - [x] sgit add \<filenames> .
 - [x] sgit status
 - [x] sgit diff
 - [x] sgit commit
 - [x] sgit log
 - [x] sgit log -p
 - [ ] sgit log -stat :construction_worker:
 - [x] sgit branch  \<branch>
 - [x] sgit branch -av -ALL
  ```diff
  -ALL is imperative
  ```
 - [ ] sgit checkout <branch\> :construction_worker:
 - [x] sgit tag <tag\> 
 - [ ] sgit merge <branch\> :construction_worker:
 - [ ] sgit rebase <branch\> :construction_worker:
 - [ ] sgit rebase -i :construction_worker:
 
 
:shipit:


