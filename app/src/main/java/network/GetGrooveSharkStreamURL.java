package network;

import com.example.app.MainActivity;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

//Used for communication with DB, sort of deprecated.

public class GetGrooveSharkStreamURL {

    public GetGrooveSharkStreamURL(String songID){
        String key = "recs_mood";
        String secret = "3429d6acacbfa24de4f2e5af4008ca41";
        String encode = "HmacMD5";
        String digest = null;
        JSONObject msg = new JSONObject();
        try{
            SecretKeySpec keyresult = new SecretKeySpec((secret).getBytes("UTF-8"), encode);
            Mac mac = Mac.getInstance(encode);
            mac.init(keyresult);
            try {
                msg = new JSONObject().put("method", "getStreamKeyStreamServer")
                        .put("parameters",new JSONObject().put("songID",songID).put("country", new JSONObject(MainActivity.groovesharkCountryID)))
                        .put("header", new JSONObject().put("wsKey",key).put("sessionID", MainActivity.groovesharkSessionID));
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            byte[] bytes = mac.doFinal(msg.toString().getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();

        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        makeRequest(msg,digest);
    }

    private void makeRequest(JSONObject streamJSON,  String digest){

        String url1 = "https://api.grooveshark.com/ws3.php?sig="+digest;

        try {
            System.out.println("executing grooveshark");

            HttpPost request = new HttpPost(url1);
            System.out.println("SENT: " + streamJSON.toString());
            AsyncGroovesharkRequest req = new AsyncGroovesharkRequest(url1, "POST", streamJSON.toString(), "stream");
            req.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
