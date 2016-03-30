# An event based JSON parser for Java

[![Build Status](https://travis-ci.org/markenwerk/java-utils-json-parser.svg?branch=master)](https://travis-ci.org/markenwerk/java-utils-json-parser)
[![Coverage Status](https://coveralls.io/repos/github/markenwerk/java-utils-data-fetcher/badge.svg?branch=master)](https://coveralls.io/github/markenwerk/java-utils-data-fetcher?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-json-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-json-parser)
[![Issues](https://img.shields.io/github/issues/markenwerk/java-utils-json-parser.svg)](https://github.com/markenwerk/java-utils-json-parser/issues)
[![MIT License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/markenwerk/java-utils-json-parser/blob/master/LICENSE)

This library provides a pair of lightweight event based JSON parsers (a pull parser and a push parser) for JSON as specified in [RFC 7159](https://tools.ietf.org/html/rfc7159) and [ECMA-404](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf). An event based parser just emits a stream of events and doesn't create a document model for the parsed JSON document.

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
 
# Usage

## Sources

Both parsers consume a [`JsonSource`][JsonSource], which represents a character stream and the necessary methods to it.

This library provides the [`ReaderJsonSource`][ReaderJsonSource], which buffers and consumes the characters that are yielded by a given [`Reader`][Reader].

```java
Reader reader = ...

// create a new json source for reader
JsonSource jsonSource = new ReaderJsonSource(reader);
```

Additionally, this library provides the [`StringJsonSource`][StringJsonSource] and the [`CharacterArrayJsonSource`][CharacterArrayJsonSource] that can be used to parse the characters from an existing string or an existing `char[]`.

```java
String string = ...
char[] chars = ...

// create new json sources for string and chars
JsonSource jsonSource1 = new StringJsonSource(string);
JsonSource jsonSource2 = new CharacterArrayJsonSource(chars);
```

*It is usually not necessary to create a [`JsonSource`][JsonSource] directly, because both parsers have convenient constructors, that create an appropriate [`JsonSource`][JsonSource].*

## Push parser

A [`JsonPushParser`][JsonPushParser] takes a [`JsonHandler`][JsonHandler] and calls the appropriate callback methods of a [`JsonHandler`][JsonHandler], when processing a JSON document.

This library provides the default implementation [`JsonSourcePushParser`][JsonSourcePushParser], which consumes a [`JsonSource`][JsonSource] to process a JSON document.

```java
JsonSource jsonSource = ...
JsonHandler jsonHandler = ...

// creates a new parser for jsonSource
JsonPushParser jsonPushParser = new JsonSourcePushParser(jsonSource);

// consumes jsonSource and reports events to jsonHandler
jsonPushParser.handle(jsonSource)
```

### Creating a document model

This [gist](https://gist.github.com/toKrause/ef6b3f3f060640f8620c) shows a pair of simple [`JsonHandlers`][JsonHandler] for a [`JsonPushParser`][JsonPushParser], that can be used to create a document model for the reference [JSON library](http://mvnrepository.com/artifact/org.json/json/20160212) (that consists of the well known [`JSONArrays`][JSONArray] and [`JSONObjects`][JSONObject])

If the root structure of the JSON document is a JSON array:

```java
File file = ...;

// create a new json array from the file content
JSONArray jsonArray = new JsonPushParser(new FileReader(file)).handle(new ArrayHandler());
```

If the root structure of the JSON document is a JSON object:

```java
File file = ...;

// create a new json object from the file content
JSONObject jsonObject = new JsonPushParser(new FileReader(file)).handle(new ObjectHandler());
```

## Pull parser

A [`JsonPullParser`][JsonPullParser], when asked, reports it's [`JsonState`][JsonState], which reflects the immediate future (`ARRAY_BEGIN`, `NULL`, `BOOLEN`, ..., `ARRAY_END`, ...) of the processed JSON document and determines the appropriate method to be called on the [`JsonPullParser`][JsonPullParser].

This library provides the default implementation [`JsonSourcePullParser`][JsonSourcePullParser], which consumes a [`JsonSource`][JsonSource] to process a JSON document.

```java
JsonSource jsonSource = ...

// creates a new parser for jsonSource
JsonPullParser jsonPullParser = new JsonSourcePullParser(jsonSource);

// consumes jsonSource and events
while(jsonPullParser.hasNext()) {
	switch(jsonPullParser.currentState()) {
		case ARRAY_BEGIN:
			jsonPullParser.beginArray();
			...
			break;

		...
		
	}
}
```

### Creating a document model

This [gist](https://gist.github.com/toKrause/95944d3069b5422ee45f) shows a pair of simple helper methods using a [`JsonPullParser`][JsonPullParser], that can be used to create a document model for the reference [JSON library](http://mvnrepository.com/artifact/org.json/json/20160212) (that consists of the well known [`JSONArrays`][JSONArray] and [`JSONObjects`][JSONObject])

If the root structure of the JSON document is a JSON array:

```java
File file = ...;

// create a new json array from the file content
JSONArray jsonArray = JsonUtil.readArray(new FileReader(file));
```

If the root structure of the JSON document is a JSON object:

```java
File file = ...;

// create a new json object from the file content
JSONArray jsonArray = JsonUtil.readObject(new FileReader(file));
```

## Performance comparison

The following table shows the results of a [performance test](https://gist.github.com/toKrause/2227e1d4d2d2557db60b#file-test-java) with 1000 iterations that compares multiple methods of creating a document model:

- Using a [`JsonSourcePushParser`][JsonSourcePushParser] with a [`ReaderJsonSource`][ReaderJsonSource] for a [`FileReader`][FileReader] and the abovementioned [`ArrayHandler`][ArrayHandler].
- Using a [`JsonSourcePushParser`][JsonSourcePushParser] with a [`StringJsonSource`][StringJsonSource] for a preloaded `String`.
- Using a [`JsonSourcePullParser`][JsonSourcePullParser] with a [`ReaderJsonSource`][ReaderJsonSource] for a [`FileReader`][FileReader] and the abovementioned [`JsonUtil`][JsonUtil].
- Using a [`JsonSourcePullParser`][JsonSourcePullParser] with a [`StringJsonSource`][StringJsonSource] for a preloaded `String`.
- Using the constructor for a [`JSONArray`][JSONArray] with a [`JSONTokener`][JSONTokener] for a [`FileReader`][FileReader].
- Using the constructor for a [`JSONArray`][JSONArray] with a [`JSONTokener`][JSONTokener] for a preloaded `String`.

| Parser | Source | [`test0.json`](https://gist.githubusercontent.com/toKrause/2227e1d4d2d2557db60b/raw/7571076fc88cb2880e466b8f92fd6169a0800841/test0.json) (~2.5kB) | [`test1.json`](https://gist.githubusercontent.com/toKrause/2227e1d4d2d2557db60b/raw/7571076fc88cb2880e466b8f92fd6169a0800841/test1.json) (~25.0kB) | [`test2.json`](https://gist.githubusercontent.com/toKrause/2227e1d4d2d2557db60b/raw/7571076fc88cb2880e466b8f92fd6169a0800841/test2.json) (~250.0kB) | [`test3.json`](https://gist.githubusercontent.com/toKrause/2227e1d4d2d2557db60b/raw/7571076fc88cb2880e466b8f92fd6169a0800841/test3.json) (~2.5MB) |
|:----------------------------------------------:|:------------------:|:----:|:----:|:----:|:-----:|
| [`JsonSourcePushParser`][JsonSourcePushParser] | [`Reader`][Reader] | 0.15 | 0.37 | 3.36 | 33.18 |
| [`JsonSourcePushParser`][JsonSourcePushParser] | `String`           | 0.05 | 0.34 | 3.15 | 31.51 |
| [`JsonSourcePullParser`][JsonSourcePullParser] | [`Reader`][Reader] | 0.09 | 0.39 | 3.56 | 35.57 |
| [`JsonSourcePullParser`][JsonSourcePullParser] | `String`           | 0.05 | 0.36 | 3.46 | 34.71 |
| [`JSONArray`][JSONArray]                       | [`Reader`][Reader] | 0.08 | 0.59 | 5.74 | 57.88 |
| [`JSONArray`][JSONArray]                       | `String`           | 0.06 | 0.54 | 5.31 | 53.48 |

The four JSON files of different size have been created with a [random JSON generator](http://www.json-generator.com/).

### Conclusions

Both parsers provided by this library are, for sufficiently large JSON documents, about 42% faster than the reference implementation.

The relation between the size of the JSON input and the parsing duration is linear.

Preloading the content of a JSON file into a `String` instead of using a [`FileReader`][FileReader] yield no significant performance improvement.


[CharacterArrayJsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/CharacterArrayJsonSource.html
[JsonHandler]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonHandler.html
[JsonPullParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonPullParser.html
[JsonPushParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonPushParser.html
[JsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonSource.html
[JsonSourcePullParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonSourcePullParser.html
[JsonSourcePushParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonSourcePushParser.html
[JsonState]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonState.html
[ReaderJsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/ReaderJsonSource.html
[StringJsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/StringJsonSource.html

[JSONArray]: https://stleary.github.io/JSON-java/index.html?org/json/JSONArray.html
[JSONObject]: https://stleary.github.io/JSON-java/index.html?org/json/JSONObject.html
[JSONTokener]: https://stleary.github.io/JSON-java/index.html?org/json/JSONTokener.html

[FileReader]: http://docs.oracle.com/javase/8/docs/api/index.html?java/io/FileReader.html
[Reader]: http://docs.oracle.com/javase/8/docs/api/index.html?java/io/Reader.html

[ArrayHandler]: https://gist.github.com/toKrause/ef6b3f3f060640f8620c#file-arrayhandler-java
[JsonUtil]: https://gist.github.com/toKrause/95944d3069b5422ee45f#file-jsonutil-java