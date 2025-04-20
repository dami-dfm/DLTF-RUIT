package btcparserbtcj16;

/**
 *
 * @author brodo
 */
public class ScriptType {
    //type of script used 1=P2PK 2=P2PKH 3=P2SH 4=P2MS 5=other
    public static final int UNKNOWN=0;
    public static final int P2PK=1;
    public static final int P2PKH=2;
    public static final int P2SH=3;
    public static final int RETURN=4;
    public static final int EMPTY=5;
    public static final int P2WPKH=6;
    public static final int P2WSH=7;
    
    public static final int SUPPORTEDSCRIPTTYPES=8;
    
    //multisig example: txhash da738e29f64e90ae46dcc3e6b4154041d6324abbe7919e722d486a4a3148b7dc
    
    public static String typeName(int code){
        switch (code) {
            case UNKNOWN:
                return "UNKNOWN";
            case P2PK:
                return "P2PK";
            case P2PKH:
                return "P2PKH";
            case P2SH:
                return "P2SH";
            case RETURN:
                return "PROVABLY UNSPENDABLE";
            case EMPTY:
                return "ANYONE CAN SPEND";
            case P2WPKH:
                return "P2WPKH";
            case P2WSH:
                return "P2WSH";
            default:
                return "ERROR - UNRECOGNIZED SCRIPT CODE";
        }
    }
}
