# A fast event based JSON parser for Java

[![Build Status](https://travis-ci.org/markenwerk/java-utils-json-parser.svg?branch=master)](https://travis-ci.org/markenwerk/java-utils-json-parser)
[![Coverage Status](https://coveralls.io/repos/github/markenwerk/java-utils-json-parser/badge.svg?branch=master)](https://coveralls.io/github/markenwerk/java-utils-json-parser?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/57100ec5fcd19a0039f16fa4/badge.svg)](https://www.versioneye.com/user/projects/57100ec5fcd19a0039f16fa4)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-json-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-json-parser)
[![Issues](https://img.shields.io/github/issues/markenwerk/java-utils-json-parser.svg)](https://github.com/markenwerk/java-utils-json-parser/issues)
[![MIT License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/markenwerk/java-utils-json-parser/blob/master/LICENSE)

## Overview

This library provides a pair of lightweight event based JSON parsers (a pull parser and a push parser) for JSON as specified in [RFC 7159](https://tools.ietf.org/html/rfc7159) and [ECMA-404](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf). An event based parser just emits a stream of events and doesn't create a document model for the parsed JSON document.

Consult the [documentation](http://markenwerk.github.io/java-utils-json-parser/javadoc/index.html) and [usage description](#usage) for further information:

- [Sources of JSON text](#sources-of-json-text)
- [Push parser](#push-parser)
    - [Creating a document model](#creating-a-document-model)
- [Pull parser](#pull-parser)
    - [Creating a document model](#creating-a-document-model-1)
    - [Skipping values](#skipping-values)
    - [Reading strings efficiently](#reading-strings-efficiently)
- [Performance comparison](#performance-comparison)


## Maven

This library is hosted in the [Maven Central Repository](https://maven-badges.herokuapp.com/maven-central/net.markenwerk/utils-json-parser). You can use it with the following coordinates:

```xml
<dependency>
	<groupId>net.markenwerk</groupId>
	<artifactId>utils-json-parser</artifactId>
	<version>2.0.0</version>
</dependency>
```


## Motivation

The original intention was, to create a pure Java implementation of an event based JSON parser, that  (like Android's [`JsonReader`](https://developer.android.com/reference/android/util/JsonReader.html)) has the ability, to stream through a character stream and process the contained JSON document, without the need to read the whole character stream into a string first.

As a pure Java implementation, this library is available on all Java based execution environments, not just on Android.

An event base parser that (unlike most JSON libraries) dosn't create a document model, may be used to

 - efficiently process huge JSON documents on the fly,
 - create a document model from another JSON library, if that library can only process a string (like the version of the [JSON library](http://mvnrepository.com/artifact/org.json/json/20080701), that comes with Android) and thus, removing the need to create a string, 
 - or to automagically create Java objects (using some reflective witchcraft) and thus, removing the need to create a document model.
 
This library is intended to be as lightweight as possible and make no assumptions about the tasks, it may be used for.
 
## Usage

### Sources of JSON text

Both parsers consume a [`JsonSource`][JsonSource] which represents a character stream, which contains a JSON text, and the necessary methods to consume it.

This library provides the [`ReaderJsonSource`][ReaderJsonSource] that processes the characters that are yielded by a given [`Reader`][Reader].

```java
Reader reader = ...

// create a new json source for reader
JsonSource jsonSource = new ReaderJsonSource(reader);
```

Additionally, this library provides the [`StringJsonSource`][StringJsonSource] and the [`CharacterArrayJsonSource`][CharacterArrayJsonSource] which process the characters from an existing string or an existing `char[]` respectively.

```java
String string = ...
char[] chars = ...

// create new json sources for string and chars
JsonSource jsonSource1 = new StringJsonSource(string);
JsonSource jsonSource2 = new CharacterArrayJsonSource(chars);
```

> It's usually not necessary to create a [`JsonSource`][JsonSource] directly, because both parsers have convenient constructors, that create an appropriate [`JsonSource`][JsonSource].

### Push parser

A [`JsonPushParser`][JsonPushParser] takes a [`JsonHandler`][JsonHandler] and calls the appropriate callback methods while processing a JSON text.

This library provides the default implementation [`DefaultJsonPushParser`][DefaultJsonPushParser], which consumes a [`JsonSource`][JsonSource] to process JSON text.

```java
JsonSource jsonSource = ...
JsonHandler jsonHandler = ...

// creates a new parser for jsonSource
JsonPushParser jsonPushParser = new DefaultJsonPushParser(jsonSource);

// consumes jsonSource and reports events to jsonHandler
jsonPushParser.handle(jsonSource)
```

#### Creating a document model

This [gist](https://gist.github.com/toKrause/ef6b3f3f060640f8620c) a pair of simple [`JsonHandlers`][JsonHandler], which can be used to create a document model that consists of the well known [`JSONArrays`][JSONArray] and [`JSONObjects`][JSONObject] from the reference [JSON library](http://mvnrepository.com/artifact/org.json/json/20160212).

If the root structure of the JSON document is a JSON array:

```java
Reader reader = ...;

// create a new json array from the file content
JSONArray jsonArray = new JsonPushParser(reader).handle(new ArrayHandler());
```

If the root structure of the JSON document is a JSON object:

```java
Reader reader = ...;

// create a new json object from the file content
JSONObject jsonObject = new JsonPushParser(reader).handle(new ObjectHandler());
```

### Pull parser

A [`JsonPullParser`][JsonPullParser], when asked, reports it's [`JsonState`][JsonState], which reflects the immediate future (`ARRAY_BEGIN`, `NULL`, `BOOLEN`, ..., `ARRAY_END`, ...) of the processed JSON text and determines the appropriate method to be called on the [`JsonPullParser`][JsonPullParser].

This library provides the default implementation [`DefaultJsonPullParser`][DefaultJsonPullParser], which consumes a [`JsonSource`][JsonSource] to process a JSON text.

```java
JsonSource jsonSource = ...

// creates a new parser for jsonSource
JsonPullParser jsonPullParser = new DefaultJsonPullParser(jsonSource);

// consumes jsonSource and events
loop: while(true) {

    // examine current state
	switch(jsonPullParser.currentState()) {
		case ARRAY_BEGIN:
			jsonPullParser.beginArray();
			...
			break;

		...
		
	   case SOURCE_END:
			break loop;
		
	}
}
```

Assuming the structure of the JSON document is known beforehand (e.g a JSON array containing JSON strings), it isn't necessary to call `currentState()`:

```java
// consumes a jsonarray of strings
jsonPullParser.beginDocument();
jsonPullParser.beginArray();
while(jsonPullParser.hasNextElement()) {
    tags.add(jsonPullParser.nextString());
}
jsonPullParser.endArray();
jsonPullParser.endDocument();
```

#### Creating a document model

This [gist](https://gist.github.com/toKrause/ef6b3f3f060640f8620c) a pair of simple [`JsonHandlers`][JsonHandler], which can be used to create a document model that consists of the well known [`JSONArrays`][JSONArray] and [`JSONObjects`][JSONObject] from the reference [JSON library](http://mvnrepository.com/artifact/org.json/json/20160212).

This [gist](https://gist.github.com/toKrause/95944d3069b5422ee45f) shows a pair of simple helper methods, that can be used to create a document model that consists of the well known [`JSONArrays`][JSONArray] and [`JSONObjects`][JSONObject] from the reference [JSON library](http://mvnrepository.com/artifact/org.json/json/20160212).

If the root structure of the JSON document is a JSON array:

```java
Reader reader = ...;

// create a new json array from the file content
JSONArray jsonArray = JsonUtil.readArray(reader);
```

If the root structure of the JSON document is a JSON object:

```java
Reader reader = ...;

// create a new json object from the file content
JSONArray jsonArray = JsonUtil.readObject(reader);
```

#### Skipping values

A [`JsonPullParser`][JsonPullParser] can be instructed to skip the current value. If the current value state is `ARRAY_BEGIN` or `OBJECT_BEGIN` the whole JSON array or JSON object is skipped. Inside of an JSON object, a value may be skipped before or after the name has been pulled.

```java
while(jsonPullParser.hasNext()) {
	switch(jsonPullParser.currentState()) {
		case NAME:
            String name = jsonPullParser.nextName();
			if(name.equals("optional_content") {
			    // ignore optional content 
                jsonPullParser.skipValue();
			}
			...
			break;

		...
		
	}
}
```

#### Reading strings efficiently

A [`JsonPullParser`][JsonPullParser] can be used to create a [`Reader`][Reader] for a JSON string. This may yield considerable performance improvements if a JSON document contains large strings that need to be further processed (e.g. a Base64-encoded image), because no [`String`][String] object has to be created for the intermediate value.

```java
while(jsonPullParser.hasNext()) {
	switch(jsonPullParser.currentState()) {
		case NAME:
            String name = jsonPullParser.nextName();
			if(name.equals("image") {
    			Reader imageReader = jsonPullParser.readString();
			    image = ImageIO.read(new Base64InputStream(new ReaderInputStream(imageReader)));
			}
			...
			break;

		...
		
	}
}
```

### Performance comparison

The following table shows the results of a [performance test](https://gist.github.com/toKrause/2227e1d4d2d2557db60b#file-test-java) with 1000 iterations that compares multiple methods of creating a document model:

- Using a [`DefaultJsonPushParser`][DefaultJsonPushParser] with a [`ReaderJsonSource`][ReaderJsonSource] for a [`FileReader`][FileReader] and the abovementioned [`ArrayHandler`][ArrayHandler].
- Using a [`DefaultJsonPushParser`][DefaultJsonPushParser] with a [`StringJsonSource`][StringJsonSource] for a preloaded `String`.
- Using a [`DefaultJsonPullParser`][DefaultJsonPullParser] with a [`ReaderJsonSource`][ReaderJsonSource] for a [`FileReader`][FileReader] and the abovementioned [`JsonUtil`][JsonUtil].
- Using a [`DefaultJsonPullParser`][DefaultJsonPullParser] with a [`StringJsonSource`][StringJsonSource] for a preloaded `String`.
- Using the constructor for a [`JSONArray`][JSONArray] with a [`JSONTokener`][JSONTokener] for a [`FileReader`][FileReader].
- Using the constructor for a [`JSONArray`][JSONArray] with a [`JSONTokener`][JSONTokener] for a preloaded `String`.

| Parser | Source | [`test0.json`](https://gist.githubusercontent.com/toKrause/2227e1d4d2d2557db60b/raw/7571076fc88cb2880e466b8f92fd6169a0800841/test0.json) (~2.5kB) | [`test1.json`](https://gist.githubusercontent.com/toKrause/2227e1d4d2d2557db60b/raw/7571076fc88cb2880e466b8f92fd6169a0800841/test1.json) (~25.0kB) | [`test2.json`](https://gist.githubusercontent.com/toKrause/2227e1d4d2d2557db60b/raw/7571076fc88cb2880e466b8f92fd6169a0800841/test2.json) (~250.0kB) | [`test3.json`](https://gist.githubusercontent.com/toKrause/2227e1d4d2d2557db60b/raw/7571076fc88cb2880e466b8f92fd6169a0800841/test3.json) (~2.5MB) |
|:------------------------------------------------:|:------------------:|:----:|:----:|:----:|:-----:|
| [`DefaultJsonPushParser`][DefaultJsonPushParser] | [`Reader`][Reader] | 0.15 | 0.37 | 3.36 | 33.18 |
| [`DefaultJsonPushParser`][DefaultJsonPushParser] | `String`           | 0.05 | 0.34 | 3.15 | 31.51 |
| [`DefaultJsonPullParser`][DefaultJsonPullParser] | [`Reader`][Reader] | 0.09 | 0.39 | 3.56 | 35.57 |
| [`DefaultJsonPullParser`][DefaultJsonPullParser] | `String`           | 0.05 | 0.36 | 3.46 | 34.71 |
| [`JSONArray`][JSONArray]                         | [`Reader`][Reader] | 0.08 | 0.59 | 5.74 | 57.88 |
| [`JSONArray`][JSONArray]                         | `String`           | 0.06 | 0.54 | 5.31 | 53.48 |

The four JSON files of different size have been created with a [random JSON generator](http://www.json-generator.com/).

Both parsers provided by this library are, for sufficiently large JSON documents, about 42% faster than the reference implementation. The relation between the size of the JSON input and the parsing duration is linear. Preloading the content of a JSON file into a `String` instead of using a [`FileReader`][FileReader] yield no significant performance improvement.


[CharacterArrayJsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/CharacterArrayJsonSource.html
[JsonPullParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonPullParser.html
[JsonPushParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonPushParser.html
[JsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonSource.html
[DefaultJsonPullParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/DefaultJsonPullParser.html
[DefaultJsonPushParser]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/DefaultJsonPushParser.html
[JsonState]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/JsonState.html
[ReaderJsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/ReaderJsonSource.html
[StringJsonSource]: https://markenwerk.github.io/java-utils-json-parser/index.html?net/markenwerk/utils/json/parser/StringJsonSource.html

[JsonHandler]: https://markenwerk.github.io/java-utils-json-commons/index.html?net/markenwerk/utils/json/commons/interfaces/JsonHandler.html

[JSONArray]: https://stleary.github.io/JSON-java/index.html?org/json/JSONArray.html
[JSONObject]: https://stleary.github.io/JSON-java/index.html?org/json/JSONObject.html
[JSONTokener]: https://stleary.github.io/JSON-java/index.html?org/json/JSONTokener.html

[FileReader]: http://docs.oracle.com/javase/8/docs/api/index.html?java/io/FileReader.html
[Reader]: http://docs.oracle.com/javase/8/docs/api/index.html?java/io/Reader.html
[String]: http://docs.oracle.com/javase/8/docs/api/index.html?java/lang/String.html

[ArrayHandler]: https://gist.github.com/toKrause/ef6b3f3f060640f8620c#file-arrayhandler-java
[JsonUtil]: https://gist.github.com/toKrause/95944d3069b5422ee45f#file-jsonutil-java
