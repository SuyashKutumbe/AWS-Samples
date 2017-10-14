package com.example.cognito;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

public class CreateSignUpUser {
    private static final String USER_POOL_ID = "us-west-2_jDWuVObYf";
    private static final String EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY = "suyashkutumbe@gmail.com";
    private static final String EMAIL_ADDRESS_WITH_CLIENT_HASH_KEY = "suyashkutumbe@gmail.com";
    private static final String REGION = "us-west-2";
    private static final String CLIENT_ID = "6klok6ss9n2mh98gs75q5rhfjf";
    private static final String CLIENT_ID_WITH_HASH_KEY = "35pg0odhf2nmi7gnfrfnopdomb";

    private static final AWSCognitoIdentityProviderClient cognitoIdentityProviderClient =
            (AWSCognitoIdentityProviderClient) AWSCognitoIdentityProviderClientBuilder
                    .standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(REGION)
                    .build();

    public static void main(String[] args) throws Exception {
        /*String appSecretKey = "g08b8l9dtlp8l6r4u0r4kkl8f38o7meub0bo05vbrbsv6bb6vqo";
        Map<String, String> authParameters = new HashMap<>();
        authParameters.put("USERNAME", EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY);
        authParameters.put("PASSWORD", "itsAwesome");
        authParameters.put("SECRET_HASH", getSecretHash(EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY,
                CLIENT_ID_WITH_HASH_KEY, appSecretKey));

        AdminInitiateAuthRequest adminInitiateAuthRequest = new AdminInitiateAuthRequest();
        adminInitiateAuthRequest
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withUserPoolId(USER_POOL_ID)
                .withClientId(CLIENT_ID)
                .withAuthParameters(authParameters);

        AdminInitiateAuthResult adminInitiateAuthResult = cognitoIdentityProviderClient.adminInitiateAuth
                (adminInitiateAuthRequest);

        System.out.println(adminInitiateAuthResult.getAuthenticationResult().getIdToken());*/
        confirmUserWhenAppIdCreatedWithNoSecretHashKey();


    }

    private static String getSessionWithClientHashKey() throws Exception {
        String appSecretKey = "g08b8l9dtlp8l6r4u0r4kkl8f38o7meub0bo05vbrbsv6bb6vqo";
        Map<String, String> authParameters = new HashMap<>();
        authParameters.put("SECRET_HASH", getSecretHash(EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY,
                CLIENT_ID_WITH_HASH_KEY, appSecretKey));
        authParameters.put("USERNAME", EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY);
        authParameters.put("PASSWORD", "AdxwmTCP"); // Temp password


        AdminInitiateAuthRequest adminInitiateAuthRequest = new AdminInitiateAuthRequest();
        adminInitiateAuthRequest
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withUserPoolId(USER_POOL_ID)
                .withClientId(CLIENT_ID_WITH_HASH_KEY)
                .withAuthParameters(authParameters);

        return cognitoIdentityProviderClient.adminInitiateAuth(adminInitiateAuthRequest).getSession();
    }

    private static void getUserFromUserPool() {
        AdminGetUserRequest adminGetUserRequest = new AdminGetUserRequest()
                .withUserPoolId(USER_POOL_ID)
                .withUsername(EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY);

        AdminGetUserResult adminGetUserResult = cognitoIdentityProviderClient.adminGetUser(adminGetUserRequest);
        System.out.println(adminGetUserResult);
    }

    private static String getSessionToConfirmUser() {
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("USERNAME", EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY);
        userDetails.put("PASSWORD", "12345678"); // Temp password

        AdminInitiateAuthRequest adminInitiateAuthRequest = new AdminInitiateAuthRequest()
                .withUserPoolId(USER_POOL_ID)
                .withClientId(CLIENT_ID)
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withAuthParameters(userDetails);

        AdminInitiateAuthResult adminInitiateAuthResult =
                cognitoIdentityProviderClient.adminInitiateAuth(adminInitiateAuthRequest);
        System.out.println("adminInitiateAuthResult." + adminInitiateAuthResult);
        return adminInitiateAuthResult.getSession();
    }

    private static void confirmUserWhenAppIdCreatedWithNoSecretHashKey() {
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("USERNAME", EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY);
        userDetails.put("NEW_PASSWORD", "itsAwesome");

        AdminRespondToAuthChallengeRequest adminRespondToAuthChallengeRequest = new AdminRespondToAuthChallengeRequest()
                .withUserPoolId(USER_POOL_ID)
                .withClientId(CLIENT_ID)
                .withSession(getSessionToConfirmUser())
                .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .withChallengeResponses(userDetails);

        AdminRespondToAuthChallengeResult adminRespondToAuthChallengeResult =
                cognitoIdentityProviderClient.adminRespondToAuthChallenge(adminRespondToAuthChallengeRequest);

        System.out.println("adminRespondToAuthChallengeRequest." + adminRespondToAuthChallengeResult);
    }

    private static void confirmUserWhenAppIdCreatedWithSecretHashKey() throws Exception {
        String appSecretKey = "g08b8l9dtlp8l6r4u0r4kkl8f38o7meub0bo05vbrbsv6bb6vqo";
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("SECRET_HASH", getSecretHash(EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY,
                CLIENT_ID_WITH_HASH_KEY, appSecretKey));
        userDetails.put("USERNAME", EMAIL_ADDRESS_WITH_CLIENT_HASH_KEY);
        userDetails.put("NEW_PASSWORD", "itsAwesome");

        AdminRespondToAuthChallengeRequest adminRespondToAuthChallengeRequest = new AdminRespondToAuthChallengeRequest()
                .withUserPoolId(USER_POOL_ID)
                .withClientId(CLIENT_ID_WITH_HASH_KEY)
                .withSession(getSessionWithClientHashKey())
                .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .withChallengeResponses(userDetails);

        AdminRespondToAuthChallengeResult adminRespondToAuthChallengeResult =
                cognitoIdentityProviderClient.adminRespondToAuthChallenge(adminRespondToAuthChallengeRequest);

        System.out.println("adminRespondToAuthChallengeRequest." + adminRespondToAuthChallengeResult);
    }

    private static String getSecretHash(String email, String appClientId, String appSecretKey) throws Exception {
        byte[] data = (email + appClientId).getBytes("UTF-8");
        byte[] key = appSecretKey.getBytes("UTF-8");

        return Base64.encodeAsString(HmacSHA256(data, key));
    }

    static byte[] HmacSHA256(byte[] data, byte[] key) throws Exception {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data);
    }

    /* Unchecked */
    private static void confirmSignUp() {
        AdminConfirmSignUpRequest adminConfirmSignUpRequest = new AdminConfirmSignUpRequest()
                .withUserPoolId(USER_POOL_ID)
                .withUsername(EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY);

        AdminConfirmSignUpResult adminConfirmSignUpResult =
                cognitoIdentityProviderClient.adminConfirmSignUp(adminConfirmSignUpRequest);

        System.out.println("adminConfirmSignUpResult. " + adminConfirmSignUpResult);
    }

    private static void signUp(String email) {
        AdminCreateUserRequest adminCreateUserRequest = new AdminCreateUserRequest()
                .withUserPoolId(USER_POOL_ID)
                .withUsername(EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY)
                .withUserAttributes(
                        new AttributeType()
                                .withName("email")
                                .withValue(EMAIL_ADDRESS_WITHOUT_CLIENT_HASH_KEY),
                        new AttributeType()
                                .withName("email_verified")
                                .withValue("true"))
                .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .withForceAliasCreation(Boolean.FALSE);

        AdminCreateUserResult adminCreateUserResult =
                cognitoIdentityProviderClient.adminCreateUser(adminCreateUserRequest);
        System.out.println("Admin Create User Result." + adminCreateUserResult.getUser());
    }
}
