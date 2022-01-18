package com.trashparadise.lifemanager.service;

import com.trashparadise.lifemanager.util.RequestInterface;

import okhttp3.tls.Certificates;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestService {
    private static final String[] certPem = new String[]{"" +
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIFajCCA1KgAwIBAgIUQv2vT1EQ4Bl2xSKk3wSzJqUS2vkwDQYJKoZIhvcNAQEN\n" +
            "BQAwTTELMAkGA1UEBhMCQ04xETAPBgNVBAoMCEt1cmF5dXJpMRAwDgYDVQQLDAdS\n" +
            "b290IENBMRkwFwYDVQQDDBBLdXJheXVyaSBSb290IENBMB4XDTIyMDExODE5NDgy\n" +
            "M1oXDTQyMDExMzE5NDgyM1owTTELMAkGA1UEBhMCQ04xETAPBgNVBAoMCEt1cmF5\n" +
            "dXJpMRAwDgYDVQQLDAdSb290IENBMRkwFwYDVQQDDBBLdXJheXVyaSBSb290IENB\n" +
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAnws21mGktDIHh9iL0kAY\n" +
            "jvviCEnRsriwRf9XCR4/97Zhr1nYqLCfxPGbQn4YcYsWe+iKUx9ZgIX2rHWxmRLh\n" +
            "FywCt6M2Nif4IciUB6eSRrHLkC8mHQISNKeZOaJF15pNDvFHyHBDbNbdmbP1JAPH\n" +
            "CBUh1eCwF7AbSBxIVg5jr8gSDft7uLiKONHDY5Q9fUxZWhLmyS63XX7gA/faQ+Uy\n" +
            "2NgIqgVCxWibLJ2IF6aU6iYKcBfRs7c0Eelwh/f5IdcD/HmsUVoiEYQPEh7jmW/Z\n" +
            "nx9CbGb+CDB6BCD+NT0eWyX1icv/rWxJ4mpAfjqZFLbEfKtkD3mmxtNQeF043hsS\n" +
            "1Y5sbKlym+70s7fUO6DLVU27CKNu1eayZxihepqavzT4I4mvgK9YQ53f8OF5gsLP\n" +
            "5doLz6KevJrNbEfaaibKJdcnoffxvcOoztCbF58xMQyC82MXlURU2QRIQfX+kAYe\n" +
            "1lH4EDQpB4ReYi6cpX/ZwY+3haS2DqJtJW8Y3kVx4qIWkn9p8WZuu1AH6XAx932w\n" +
            "0kdAF6npe6r+XvIydVTK3OIrq5cyQGxw7vUD7rFtbbQ62raKXvnYzI4pB13JCFFY\n" +
            "vokV8LypHl9C0ZogUr4ed55jhaWRt/iYQEI/i8DzBGwSNE6z1R1/gCyX29RDxdsE\n" +
            "TwzYtUAA8qQC9n7B9vL+hUUCAwEAAaNCMEAwHQYDVR0OBBYEFCywnSZ/TdR/dmJh\n" +
            "9TZPfx8iN6ImMA8GA1UdEwEB/wQFMAMBAf8wDgYDVR0PAQH/BAQDAgGGMA0GCSqG\n" +
            "SIb3DQEBDQUAA4ICAQAIja7NnQBD4ZZk6Oi/u3cCU3WjkJuK41m6f1/HpvM5sQ2a\n" +
            "ABfFCvwux1Ftubk7R65K5yc8j7Bl7mg8F/m6SAQXIubFobrbB5mwZlIm60jsVKi/\n" +
            "uKKRaXT0n+yIQ5SvUuso/SXejQkkJdw0RfH22mojOUZS7FFJWDdHFSS0zVO/YuTQ\n" +
            "EfsqOQKM6AUGfvDrErGsqPZbUQDKswbYL5RDtCh2EJqjp3Qb4ocEUyT8ZD73lo+d\n" +
            "gNuP8bqIrTF9usbMZyzvL8MGuCv8Gpof4QglRLTcEv3G6fofkgyHFIOAOz7KQhty\n" +
            "YEUpGZtj+rShWQ/YqWzs5ZWCXhEvXt/16DYjyDyRBb2KBiESITPCEMftZdXnETGh\n" +
            "IyJfUMC7BMotTNdGqy9mzIKFHinKJLoz32iLxtzA+DX2lNj+dwR7+8z1p8a2VXcY\n" +
            "grOev/JkVqoQF5vFzJ/ibhxZh0CXW1ZF0nWNAY9+j7U+fBWMPGInqZVi7uB5TyZY\n" +
            "OynNqrD0jKgZy6o/LCKQKjYEKBVBe/ErI23w6zxrCBEgXg/Lw6PIP6aEjDkV1bLJ\n" +
            "X+SMekWSfwaNTo97btr0y5A9NGfFlYvp28W8Vo00Z33pFN3jTD0Bz5g457dbJF1a\n" +
            "1bTjdebby9BYDJuVKKw4yen7JmpV7I1aWlSQM0VMD2UjI6/sbyCkQPfkhD5snw==\n" +
            "-----END CERTIFICATE-----\n"
    };


    private static OkHttpClient getOkHttpClient() {
        HandshakeCertificates.Builder builder = new HandshakeCertificates.Builder();
        for (String cert : certPem) {
            builder.addTrustedCertificate(Certificates.decodeCertificatePem(cert));
        }
        HandshakeCertificates certificates = builder.build();
        return new OkHttpClient.Builder()
                .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager())
                .build();
    }


    public static String address = "https://10.0.2.2:8080/";
    public static RequestInterface API;

    static {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(address)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build();
        API = retrofit.create(RequestInterface.class);
    }
}


