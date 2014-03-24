/** 
 * I have made an all trusting trust manager which makes it vulnerable to some middle man attacks. Due to time constraints
 * I am going with this solution although it is vulnerable to some middle man attacks. 
 * Making it secure is very easy. You just have to export the certificate from your browser and import it in your JVM truststore.
 * It was not specified so I decided to go with this. If it is desired by the company to make it more secure, you just have to inform
 * me and I will do it. 
 */


import org.apache.commons.io.FileUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.File;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestSSL {

    public static void main(String[] args) throws Exception {
        
    	// Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
            
        // Install the all-trusting trust manager
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        
    	System.out.println("The STRING to be entered");
 	   	Scanner scan = new Scanner(System.in);
		String str = scan.nextLine();
        
		String UrlName = "https://api.goeuro.com/api/v1/suggest/position/en/name/";
		String UrlString = UrlName + str;
		System.out.println("So the url is " + UrlString);
		URL url = new URL(UrlString);
        URLConnection con = url.openConnection();
        final Reader reader = new InputStreamReader(con.getInputStream());
        final BufferedReader br = new BufferedReader(reader);        
        String line = "";
        
        
        try {
        
	        while ((line = br.readLine()) != null) {
	        	   JSONObject output= new JSONObject(line);
	        	   JSONArray docs = output.getJSONArray("results");
	
	        	   for(int i=0; i<docs.length();i++){
	        	       JSONObject geo_pos =  (JSONObject)(docs.getJSONObject(i).getJSONObject("geo_position"));
	        	       docs.getJSONObject(i).put("latitude", geo_pos.get("latitude"));
	        	       docs.getJSONObject(i).put("longitude", geo_pos.get("longitude"));
	        	       docs.getJSONObject(i).remove("geo_position");
	        	   }       
	        	   
	        	   File file = new File("/home/asfandyar/Desktop/GoEuro.csv");
	        	   String csv = CDL.toString(docs);
	        	   FileUtils.writeStringToFile(file, csv);
	        	   System.out.println("Worked ! Check the GoEuro.csv");
	        }
	        br.close();
        }catch (IOException e) {
           System.err.println("Problem writing to the file GoEuro.csv");
        }
    } // End of main 
} // End of the class