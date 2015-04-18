package network;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

//Used for communication with DB, sort of deprecated.

public class GetGrooveSharkSessionCountry {

    public GetGrooveSharkSessionCountry(){
        String key = "recs_mood";
        String secret = "3429d6acacbfa24de4f2e5af4008ca41";
        String encode = "HmacMD5";
        String digest1 = null;
        String digest2 = null;
        JSONObject msg1 = new JSONObject();
        JSONObject msg2 = new JSONObject();
        try{
            SecretKeySpec keyresult = new SecretKeySpec((secret).getBytes("UTF-8"), encode);
            Mac mac = Mac.getInstance(encode);
            mac.init(keyresult);
            try {
                msg1 = new JSONObject().put("method", "getCountry").put("parameters",new JSONObject()).put("header", new JSONObject().put("wsKey",key));
                msg2 = new JSONObject().put("method", "startSession").put("header", new JSONObject().put("wsKey",key));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            byte[] bytes = mac.doFinal(msg1.toString().getBytes("ASCII"));
            byte[] bytes2 = mac.doFinal(msg2.toString().getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest1 = hash.toString();
            hash = new StringBuffer();
            for (int i = 0; i < bytes2.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes2[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest2 = hash.toString();

        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        makeRequest(msg1,msg2,digest1,digest2);
    }

    private void makeRequest(JSONObject countryJSON,  JSONObject sessionJSON, String digest1, String digest2){

        String url1 = "https://api.grooveshark.com/ws3.php?sig="+digest1;
        String url2 = "https://api.grooveshark.com/ws3.php?sig="+digest2;

        try {
            System.out.println("executing grooveshark");

            HttpPost request = new HttpPost(url1);
            System.out.println("SENT: " + countryJSON.toString());
            GroovesharkRequest req = new GroovesharkRequest(url1, "POST", countryJSON.toString(), "country");


            HttpPost request2 = new HttpPost(url2);
            System.out.println("SENT: " + sessionJSON.toString());
            GroovesharkRequest req2 = new GroovesharkRequest(url2, "POST", sessionJSON.toString(), "session");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
