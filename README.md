Druid [![Build Status](https://travis-ci.org/liuyang1204/druid.svg?branch=master)](https://travis-ci.org/liuyang1204/druid)
=====

Druid programming language

## Introduction

Druid is a **reactive** programming language.

### Key Feature

#### Reactive Assignment

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

**OK, you think the above is not that cool?**

```
a = [1, 2, 3];
b = [4, 5, 6];
c <- a + b; //we can join arrays on change
print(c); //[1,2,3,4,5,6]
a = [7, 8, 9];
print(c); //[7,8,9,4,5,6]

a = [1, 2, 3];
b <- reverse(a); //we can apply functions
print(b); //[3,2,1]
a = [4, 5, 6];
print(b); //[6,5,4]
```

This brings native support of [Reactive Programming](http://en.wikipedia.org/wiki/Reactive_programming) into the language level.

#### Signal

**Signal** is a value binding to an external event, such as file changing, socket arriving, mouse/keyborad event.
It has the syntax `@[signalType](arg1, arg2, ...)`

With **Reactive  Assignment**, we can do the magic.

```
a <- @file('a.txt'); //a file signal listening on the file 'a.txt'
```

once the file `a.txt` is changed on the disk, for example by using `echo "new" > a.txt` in the shell command, the value of `a` will be updated to `'new'` automatically.

With the combination of **Reactive Assignment**, this can bring us magic!

## Installation

Make sure you have **Git**, **jdk8** and **maven** installed.

```bash
$ curl https://raw.githubusercontent.com/liuyang1204/druid/master/install.sh | bash
```

## Usage

After installation, type:
```bash
$ druid
```
to get hint. Typically:
```bash
$ druid run file.druid
```

## Syntax

A detailed language syntax specification is coming. For now, you can check the [test cases](src/test/java/liuyang/druid) for syntax example.
