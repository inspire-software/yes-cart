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