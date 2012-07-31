<!--
  ~ Copyright 2009 Igor Azarnyi, Denys Pavlov
  ~
  ~    Licensed under the Apache License, Version 2.0 (the "License");
  ~    you may not use this file except in compliance with the License.
  ~    You may obtain a copy of the License at
  ~
  ~        http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~    Unless required by applicable law or agreed to in writing, software
  ~    distributed under the License is distributed on an "AS IS" BASIS,
  ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~    See the License for the specific language governing permissions and
  ~    limitations under the License.
  -->

To build this archetype use maven and then run the Transform class:
mvn archetype:generate -DgroupId=org.yes.cart.icecat.transform -DartifactId=icecattransformer -DarchetypeGroupId=org.codehaus.gmaven.archetypes -DarchetypeArtifactId=gmaven-archetype-basic -DarchetypeVersion=1.1


HOW TO:

1. Create directory structure:
[ROOT]
  +- export
    +- freexml.int
      +- EN
      +- RU
      ...

2. Download refs.xml from icecat.biz into $ROOT/export/freexml.int/:
wget --user=[username] --ask-password http://data.icecat.biz/export/freexml.int/refs.xml

3. Download index.html for each desired language into $ROOT/export/freexml.int/[LANG]:
wget --user=[username] --ask-password http://data.icecat.biz/export/freexml.int/EN/
wget --user=[username] --ask-password http://data.icecat.biz/export/freexml.int/RU/
etc...

4. Run the Transform class
# Put this jar and groovy on classpath e.g.
export CLASSPATH=./icecat-downloader-1.0.0-SNAPSHOT.jar:/Users/denispavlov/.m3/repository/org/codehaus/groovy/groovy-all/2.0.0-beta-2/groovy-all-2.0.0-beta-2.jar:.
# run the Transform
java org.yes.cart.icecat.transform.Transform

5. Follow instructions, which should result in:
* all product data being downloaded into $ROOT/export/freexml.int/xmlcache/[product lang]
* all pictures being downloaded into $ROOT/export/freexml.int/pictcache/
* generation of csv files in $ROOT/export/freexml.int/csvresult/

6. Zip up the contents of pictcache and csvresult and use import section in YUM (Yes cart Update Manager) to import the data.
