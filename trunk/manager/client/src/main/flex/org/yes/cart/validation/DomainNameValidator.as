package org.yes.cart.validation {
public class DomainNameValidator {

    private static var topLevelDomainNames:Array =
["aero","asia","biz","cat","com","coop","edu","gov","info","int","jobs","mil","mobi","museum","name","net","org","pro","tel"
,"travel","ac","ad","ae","af","ag","ai","al","am","an","ao","aq","ar","as","at","au","aw","ax","az","ba","bb","bd","be","bf"
,"bg","bh","bi","bj","bm","bn","bo","br","bs","bt","bv","bw","by","bz","ca","cc","cd","cf","cg","ch","ci","ck","cl","cm","cn"
,"co","cr","cu","cv","cx","cy","cz","de","dj","dk","dm","do","dz","ec","ee","eg","er","es","et","eu","fi","fj","fk","fm","fo"
,"fr","ga","gb","gd","ge","gf","gg","gh","gi","gl","gm","gn","gp","gq","gr","gs","gt","gu","gw","gy","hk","hm","hn","hr","ht"
,"hu","id","ie","il","im","in","io","iq","ir","is","it","je","jm","jo","jp","ke","kg","kh","ki","km","kn","kp","kr","kw","ky"
,"kz","la","lb","lc","li","lk","lr","ls","lt","lu","lv","ly","ma","mc","md","me","mg","mh","mk","ml","mm","mn","mo","mp","mq"
,"mr","ms","mt","mu","mv","mw","mx","my","mz","na","nc","ne","nf","ng","ni","nl","no","np","nr","nu","nz","om","pa","pe","pf"
,"pg","ph","pk","pl","pm","pn","pr","ps","pt","pw","py","qa","re","ro","rs","ru","rw","sa","sb","sc","sd","se","sg","sh","si"
,"sj","sk","sl","sm","sn","so","sr","st","su","sv","sy","sz","tc","td","tf","tg","th","tj","tk","tl","tm","tn","to","tp","tr"
,"tt","tv","tw","tz","ua","ug","uk","us","uy","uz","va","vc","ve","vg","vi","vn","vu","wf","ws","ye","yt","za","zm","zw"];

    public static function validate(url:String):Boolean {
        if (url == 'localhost') {
            return true;
        }
        return (url.length > 5) && (validateDomain(url) || validateIp(url));
    }

    public static function validateDomain(url:String):Boolean {
        var dn:String;
        for (var i:int=0; i<topLevelDomainNames.length; i++) {
            dn = "."+topLevelDomainNames[i];
            if (url.lastIndexOf(dn) == (url.length - dn.length)) {
                return true;
            }
        }
        return false;
    }

    public static function validateIp(url:String):Boolean {
        var regex:RegExp = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;
        return regex.test(url);
    }


}
}