package btcparserbtcj16;

import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.params.MainNetParams;

/**
 * classe per rappresentare e parsare uno script
 *
 * @author brodo
 */
public class ScriptParser {
    private static final int OP_DUP=118;
    private static final int OP_HASH160 = 169;
    private static final int OP_EQUALVERIFY= 136;
    private static final int OP_CHECKSIG= 172;
    private static final int OP_CHECKSIGVERIFY= 173;
    private static final int OP_EQUAL= 135;
    private static final int OP_RETURN=106;
            //da 1 a 75 è op_push_data0 cioè numero di byte sucecssivi da pushare sullo stack
    private static final int OP_PUSHDATA20=20;
    private static final int OP_PUSHDATA32=32;
    private static final int OP_PUSHDATA33=33;
    private static final int OP_PUSHDATA65=65;
    private static final int OP_0=0;

    private static boolean isOpCode(byte b, int opcode){
        //System.out.println(b+" - "+opcode+" equals "+(Utilities.readUnsignedByte(b)==opcode));
        return Utilities.readUnsignedByte(b)==opcode;
    }
    //leggo script come array di bytes
    public static String addrFromIn(byte[] script){
        return "";///////////////////////////////////////////
    }
    //leggo script nella forma P2PKH, P2PK e P2SH
    public static String addrFromOut(byte[] script){
        if((script==null)||(script.length<1)) return null;
        //non ho script vuoto
        if(isOpCode(script[0],OP_RETURN))
            return null;
        else if(isOpCode(script[0],OP_DUP)&&(script.length>=23)){
            //ho P2PBKH
            //System.out.println("P2PBKH");
            if(isOpCode(script[1],OP_HASH160)&&isOpCode(script[2],OP_PUSHDATA20)){
                //System.out.println("P2PBKH--in");
                byte[] res=new byte[20];
                System.arraycopy(script, 3, res, 0, 20);
                return getAddressFromPubHash(res);
            }else
                return null;
        }
        else if(isOpCode(script[0],OP_PUSHDATA65)&&(script.length>=66)){
            //ho P2PBK
            byte[] res=new byte[65];
            System.arraycopy(script, 1, res, 0, 65);
            return getAddressFromPubKey(res);
        }
        else if((script.length==66)&&((isOpCode(script[script.length-1],OP_CHECKSIG))||(isOpCode(script[script.length-1],OP_CHECKSIGVERIFY)))){
            //ho vecchia versione errata di P2PBK senza il byte di lenght iniziale
            byte[] res=new byte[65];
            System.arraycopy(script, 0, res, 0, 65);
            return getAddressFromPubKey(res);
        }else if(isOpCode(script[0],OP_HASH160)){
            if((script.length>=23)&&isOpCode(script[1],OP_PUSHDATA20)&&(isOpCode(script[script.length-1],OP_EQUAL)||isOpCode(script[script.length-1],OP_EQUALVERIFY))){
                //ho P2SH 160
                byte[] res=new byte[20];
                System.arraycopy(script, 2, res, 0, 20);
                return getAddressFromScriptHash(res);
            }else return null;
        } else if(isOpCode(script[0],OP_0)){
            //support for native segwit
            if(isOpCode(script[1],OP_PUSHDATA20)){
                //P2WPKH
                byte[] res=new byte[20];
                System.arraycopy(script, 2, res, 0, 20);
                NetworkParameters np = new MainNetParams();
                Context.getOrCreate(MainNetParams.get());
                return SegwitAddress.fromHash(np, res).toBech32();
            }else if(isOpCode(script[1],OP_PUSHDATA32)){
                //P2WSH
                byte[] res=new byte[32];
                System.arraycopy(script, 2, res, 0, 32);
                NetworkParameters np = new MainNetParams();
                Context.getOrCreate(MainNetParams.get());
                return SegwitAddress.fromHash(np, res).toBech32();
            }else{
                return null;
            }            
        } else
            return null;
            
    }
    //leggo script nella forma P2PKH, P2PK e P2SH
    public static int typeFromOut(byte[] script){
        if((script==null)||(script.length<1))
                return ScriptType.EMPTY;
        //non ho script vuoto
        if(isOpCode(script[0],OP_RETURN))
            return ScriptType.RETURN;
        else if(isOpCode(script[0],OP_DUP)&&(script.length>=23)){
            //ho P2PBKH
            //System.out.println("P2PBKH");
            if(isOpCode(script[1],OP_HASH160)&&isOpCode(script[2],OP_PUSHDATA20)){
                return ScriptType.P2PKH;
            }else
                return ScriptType.UNKNOWN;
        }
        else if(isOpCode(script[0],OP_PUSHDATA65)&&(script.length>=66)){
            return ScriptType.P2PK;
        }
        else if((script.length==66)&&((isOpCode(script[script.length-1],OP_CHECKSIG))||(isOpCode(script[script.length-1],OP_CHECKSIGVERIFY)))){
            //ho vecchia versione errata di P2PBK senza il byte di lenght iniziale
            return ScriptType.P2PK;
        }else if(isOpCode(script[0],OP_HASH160)){
            if((script.length>=23)&&isOpCode(script[1],OP_PUSHDATA20)&&(isOpCode(script[script.length-1],OP_EQUAL)||isOpCode(script[script.length-1],OP_EQUALVERIFY))){
                //ho P2SH 160
                return ScriptType.P2SH;
            }else return ScriptType.UNKNOWN;
        } else if(isOpCode(script[0],OP_0)){
            //support for native segwit
            if(isOpCode(script[1],OP_PUSHDATA20)){
                //P2WPKH
                return ScriptType.P2WPKH;
            }else if(isOpCode(script[1],OP_PUSHDATA32)){
                //P2WSH
                return ScriptType.P2WSH;
            }else{
                return ScriptType.UNKNOWN;
            }            
        } else
            return ScriptType.UNKNOWN;
            
    }
    //PRE:b è lungo 20
    public static String getAddressFromPubHash(byte[] b){
        //add version "00"
        byte[] version={0};
        //base58check encoding
        return Base58Check.bytesToBase58(Utilities.concatenateByteArrays(version,b));
    }
    //PRE:b è lungo 65
    public static String getAddressFromPubKey(byte[] b){
        //get hash160 from pubkey
          //perform sha256
          //perform ripemd160
        //encode hash160
        return getAddressFromPubHash(Ripemd160.getHash(Sha256.getHash(b).toBytes()));
    }
    //PRE:b è lungo 20
    public static String getAddressFromScriptHash(byte[] b){
        //add version "00"
        byte[] version={5};
        //base58check encoding
        return Base58Check.bytesToBase58(Utilities.concatenateByteArrays(version,b));
    }    
}
