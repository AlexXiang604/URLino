//package com.urlino.urlino.util;
//
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//import java.security.Key;
//import java.util.Base64;
//
//public class secretecodegen {
//        public static void main(String[] args) {
//            Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//            String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
//            System.out.println("Generated key: " + encoded);
//        }
//}
