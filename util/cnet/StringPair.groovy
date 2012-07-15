/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */





package com.npa.cnetxml
/**
 * User: Gordon.Freeman (A) gordon-minus-freeman@ukr.net
 * Date: 10 серп 2010
 * Time: 13:04:30
 */
public class StringPair {

  String attributeName;

  String attributeValue;

  def StringPair(final attributeName, final attributeValue) {
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
  }

  public String toString() {
    return attributeName + '=' +  attributeValue;
  }


}
