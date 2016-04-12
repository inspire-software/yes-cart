export class Util {

  public static clone<T> (object:T):T {
    return JSON.parse(JSON.stringify(object));
  }
  /**
   * Remove from <code>arr</code> all items from itemsToRemove list.
   * @param arr
   * @param itemsToRemove
   * @returns {Array<any>}
   */
  public static remove(arr:Array<any>, itemsToRemove:Array<any>) : Array<any> {
    for (var i =0; i < itemsToRemove.length ; i++) {
      var index = arr.indexOf(itemsToRemove[i], 0);
      if (index > -1) {
        arr.splice(index, 1);
      }
    }
    return arr;
  }
}
