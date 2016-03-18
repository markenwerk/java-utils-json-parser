# An event based JSON parser for Java

[![Build Status](https://travis-ci.org/markenwerk/java-utils-json-parser.svg?branch=master)](https://travis-ci.org/markenwerk/java-utils-json-parser)
[![Coverage Status](https://coveralls.io/repos/github/markenwerk/java-utils-data-fetcher/badge.svg?branch=master)](https://coveralls.io/github/markenwerk/java-utils-data-fetcher?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-json-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-json-parser)
[![Issues](https://img.shields.io/github/issues/markenwerk/java-utils-json-parser.svg)](https://github.com/markenwerk/java-utils-json-parser/issues)
[![MIT License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/markenwerk/java-utils-json-parser/blob/master/LICENSE)

This library provides a pair of lightweight event based JSON parsers (a pull parser and a push parser). An event based parser just emits a stream of events and doesn't create a document model for the parsed JSON document.

## Overview

This library is hosted in the [Maven Central Repository](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-json-parser). You can use it with the following coordinates:

```xml
<dependency>
	<groupId>net.markenwerk</groupId>
	<artifactId>utils-json-parser</artifactId>
	<version>1.0.0</version>
</dependency>
```

Consult the [usage description](#usage) and [Javadoc](http://markenwerk.github.io/java-utils-json-parser/javadoc/index.html) for further information.

# Motivation

The original intention was, to create a pure JAVA implementation of an event based JSON parser, that  (like Android's [`JsonReader`](https://developer.android.com/reference/android/util/JsonReader.html)) has the ability, to stream through a character stream and process the contained JSON document, without the need to read the whole character stream into a string first.

As a pure JAVA implementation, this library is available on all Java based execution environments, not just on Android.

An event base parser that (unlike most JSON libraries) dosn't create a document model, may be used to

 - efficiently process huge JSON documents on the fly,
 - create a document model from another JSON library, if that library can only process a string (like the version of the [JSON library](http://mvnrepository.com/artifact/org.json/json/20080701), that comes with Android) and thus, removing the need to create a string, 
 - or to automagically create Java objects (using some reflective witchcraft) and thus, removing the need to create a document model.
 
This library is intended to be as lightweight as possible and make no assumptions about the tasks, it may be used for.

This library actually provides two parser: A pull parser and a push parser.

The pull parser, when asked, reports it's state, which reflects what kind of JSON token (ARRAY_BEGIN, NULL, BOOLEN, ..., ARRAY_END, ...) comes next in the character stream. Users of a pull parser have to repeatedly ask for the current state and the call the appropriate methods, to consume the token. 
 
The push parser takes a handler, automatically consumes the JSON tokens and calls the appropriate callback methods on the handler.
 
# Usage

## Sources

Both parsers consume a [`JsonSource`][JsonSource], which represents a character stream and the necessary methods to it.

This library provides the [`ReaderJsonStream`][ReaderJsonStream], which buffers and consumes the characters that are yielded by a given [`Reader`]. Additionally, this library provides the [`StringJsonSource`][StringJsonSource] and the [`CharacterArrayJsonSource`][CharacterArrayJsonSource] that can be used to parse the characters from an existing string or an existing `char[]`.

It is usually not necessary to create a [`JsonSource`][JsonSource] directly, because both parsers have convenient constructors, that create an appropriate [`JsonSource`][JsonSource].

## Push parser

The [example](https://gist.github.com/toKrause/ef6b3f3f060640f8620c) shows a pair of simple [`JsonHandlers`][JsonHandler] for a [`JsonPushParser`][JsonPushParser], that can be used to create a document model for the reference [JSON library](http://mvnrepository.com/artifact/org.json/json/20160212) (that consists of the well known [`JSONArrays`][JSONArray] and [`JSONObjects`][JSONObject])

If the root structure of the JSON document is a JSON array:

```java
File file = null;

// create a new json array from the file content
JSONArray jsonArray = new JsonPushParser(new FileReader(file)).handle(new ArrayHandler());
```

If the root structure of the JSON document is a JSON object:

```java
File file = null;

// create a new json object from the file content
JSONObject jsonObject = new JsonPushParser(new FileReader(file)).handle(new ObjectHandler());
```

## Pull parser

The [example](https://gist.github.com/toKrause/95944d3069b5422ee45f) shows a pair of simple helper methods using a [`JsonPullParser`][JsonPullParser], that can be used to create a document model for the reference [JSON library](http://mvnrepository.com/artifact/org.json/json/20160212) (that consists of the well known [`JSONArrays`][JSONArray] and [`JSONObjects`][JSONObject])

If the root structure of the JSON document is a JSON array:

```java
File file = null;

// create a new json array from the file content
JSONArray jsonArray = JsonUtil.readArray(new FileReader(file));
```

If the root structure of the JSON document is a JSON object:

```java
File file = null;

// create a new json object from the file content
JSONArray jsonArray = JsonUtil.readObject(new FileReader(file));
```


[CharacterArrayJsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/CharacterArrayJsonSource.html
[JsonHandler]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonHandler.html
[JsonPullParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonPullParser.html
[JsonPushParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonPushParser.html
[JsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonSource.html
[ReaderJsonStream]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/ReaderJsonStream.html
[StringJsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/StringJsonSource.html

[JSONArray]: https://stleary.github.io/JSON-java/index.html?org/json/JSONArray.html
[JSONObject]: https://stleary.github.io/JSON-java/index.html?org/json/JSONObject.html

[Reader]: http://docs.oracle.com/javase/8/docs/api/index.html?java/io/Reader.html