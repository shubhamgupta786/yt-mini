package com.example.youtube.application.config;
import java.io.File;
import java.io.*;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Value;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import org.springframework.core.io.Resource;

	@Configuration
	public class AppConfig {
		
		 @Value("classpath:client_secret.json")
		 public Resource clientSecret;
	    @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }   

	    private static final String APPLICATION_NAME = "YouTube MiniApp";
	    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/youtube.force-ssl");

	    // ✅ Specify the full path to client_secrets.json
	    private static final String CLIENT_SECRET_FILE_PATH = "C:/Shubham/SpringBootProjects/youtube/src/main/resources/client_secret.json";
	    
	    @Bean
	    public YouTube youTubeService() throws GeneralSecurityException, IOException {
	        // ✅ Load client secrets from file path
	        File file = new File(CLIENT_SECRET_FILE_PATH);
	        if (!file.exists()) {
	            throw new IllegalStateException("Client secrets file is missing at: " + file.getAbsolutePath());
	        }

	        // ✅ Read client secrets
	        InputStream inputStream = new FileInputStream(file);
	        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));

	        final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	        
	        System.out.println("Redirect URI: " + new LocalServerReceiver.Builder().setPort(8080).build().getRedirectUri());

	        Credential credential = authorize(httpTransport, clientSecrets);

	        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
	                .setApplicationName(APPLICATION_NAME)
	                .build();
	    }

	    private Credential authorize(final HttpTransport httpTransport, GoogleClientSecrets clientSecrets) throws IOException {
	        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
	                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
	               // .setAccessType("offline")
	                .build();

	        
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
	                .setPort(8081)  // Ensure this matches your app's port
                .setCallbackPath("/Callback")  // Fix the mismatch (lowercase "callback")
                .build();
        System.out.println(flow);
        System.out.println(receiver);
	        return new com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	    }
	}


