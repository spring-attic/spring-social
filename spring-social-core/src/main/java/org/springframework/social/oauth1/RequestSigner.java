package org.springframework.social.oauth1;


public interface RequestSigner {

    public String getSignatureMethod();

    public String calculateSignature(String baseString, String consumerSecret, String tokenSecret);

    public boolean verifySignature(String baseString, String consumerSecret, String tokenSecret, String signature);

}
