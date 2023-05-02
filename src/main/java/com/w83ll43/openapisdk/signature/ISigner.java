package com.w83ll43.openapisdk.signature;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ISigner {

    String sign(String strToSign, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException;
}
