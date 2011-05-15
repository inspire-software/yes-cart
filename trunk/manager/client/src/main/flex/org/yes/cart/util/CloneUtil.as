package org.yes.cart.util {
import flash.utils.ByteArray;

/**
 * Clone object util
 */
public class CloneUtil {

    public static function clone(orig:Object):Object {
        var buffer:ByteArray = new ByteArray();
        buffer.writeObject(orig);
        buffer.position = 0;
        var result:Object = buffer.readObject();
        return result;
    }

    public function CloneUtil() {

    }
}
}