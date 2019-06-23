REF: YC-977 Update lucene core to 8.x

OVERVIEW:
=========

Upgrade for Lucene 8.1.1


DEVELOPMENT:
============

Major changes are:

- TopFieldDocs.totalHits is now a long
- Replaced DocumentBoostFieldsScoreQuery with FunctionScoreQuery (requires reindexing after update)
- boost field are now indexed as DoubleDocValuesField
- standard analyser has explicitly set stop words StandardAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET)

PRODUCTION:
===========

Old indexes will need to be removed before starting up system due to incompatibilities between Lucene
index structure.

If this is not done symptoms are:
Caused by: java.lang.IllegalArgumentException: An SPI class of type org.apache.lucene.codecs.Codec with name 'Lucene62' does not exist.
You need to add the corresponding JAR file supporting this SPI to your classpath.  The current classpath supports the following names: [Lucene70]
