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

package org.yes.cart.util {


public class FileNameUtils {

    /**
     * Get the file name without full path and file extension.
     * Example tempfile.tmp -> tempfile
     * @param fileName fiven file name.
     * @return file name without full path and file extension.
     */
    public static function getFileNameWithoutExtension(fileName:String):String {
        var dotIndex:int = fileName.lastIndexOf(".");
        return fileName.substring(0, dotIndex);
    }

    /**
     * Get the file name  extension.
     * Example tempfile.tmp -> tmp
     * @param fileName fiven file name.
     * @return file name  extension.
     */
    public static function getFileExtension(fileName:String):String {
        var dotIndex:int = fileName.lastIndexOf(".") + 1;
        return fileName.substring(dotIndex);
    }
}
}