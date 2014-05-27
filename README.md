Druid [![Build Status](https://travis-ci.org/liuyang1204/druid.svg?branch=master)](https://travis-ci.org/liuyang1204/druid)
=====

Druid programming language

## Introduction

Druid is a **reactive** programming language.

### Key Feature

**Reactive Assignment**

In most traditional programming language, there is `assignment`.

```
a = 1;
b = 2;
c = a + b;
```

But all of them are one-time assignment, which means after executing this assignment, the value of the
assignment target(left side) will not change until a next assignment of it comes.

```
a = 1;
b = 2;
c = a + b;  //c is 3
a = 3;      //c is 3
b = 4;      //c is 3
```

This **Druid** programming language is trying to support a new kind of `assignment` called **Reactive Assignment**, which, the value of the assignment target will change if any value of the assignment source(right side) changed. This means:

```
a = 1;
b = 2;
c <- a + b; //c is 3, NOTICE we use the reactive assignment '<-'
a = 3;      //c is 5 now!
b = 4;      //hey! c is 7 now!
```

This brings native support of [Reactive Programming](http://en.wikipedia.org/wiki/Reactive_programming) into the language level.

To see the usage examples, check the [test cases](src/test/java/liuyang/druid/DruidTest.java)

## Installation

```bash
$ curl https://raw.githubusercontent.com/liuyang1204/druid/master/install.sh | bash
```
