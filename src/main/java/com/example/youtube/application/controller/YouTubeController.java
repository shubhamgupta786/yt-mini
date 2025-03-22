package com.example.youtube.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.youtube.application.service.EventLogService;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
//import com.google.api.services.youtube.*;
import java.util.Map;

@RestController
@RequestMapping("/api/youtube")
@CrossOrigin(origins = "*")
public class YouTubeController {
    private final YouTube youTubeService;
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private final EventLogService eventLogService;
    
    private static final Logger logger = LoggerFactory.getLogger(YouTubeController.class);
    public YouTubeController(YouTube youTubeService,RestTemplate restTemplate, EventLogService eventLogService) {
        this.youTubeService = youTubeService;
        this.restTemplate = restTemplate;
        this.eventLogService = eventLogService;
    }
    	
    @PostMapping("/comment")
    public ResponseEntity<Map<String, String>> addComment(@RequestBody Map<String, String> payload) {    
    	Map<String, String> responseBody = new HashMap<>();
    	try {
    		 
            String videoId = payload.get("videoId");
            String commentText = payload.get("comment");

            if (videoId == null || videoId.isEmpty()) {
            	responseBody.put("error", "Error: videoId is required.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
            	//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: videoId is required.");
            }

            if (commentText == null || commentText.trim().isEmpty()) {
            	responseBody.put("error", "Error: comment text cannot be empty.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
            	//  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: comment text cannot be empty.");
            }

            // Create snippet for the comment
            CommentSnippet commentSnippet = new CommentSnippet();
            commentSnippet.setTextOriginal(commentText);

            // Create top-level comment
            Comment topLevelComment = new Comment();
            topLevelComment.setSnippet(commentSnippet);

            // Create snippet for comment thread
            CommentThreadSnippet threadSnippet = new CommentThreadSnippet();
            threadSnippet.setVideoId(videoId);
            threadSnippet.setTopLevelComment(topLevelComment);

            // Attach snippet to comment thread
            CommentThread commentThread = new CommentThread();
            commentThread.setSnippet(threadSnippet);
            
         // Use List<String> for the part parameter
          List<String> part = Arrays.asList("snippet");

            // Insert comment thread via YouTube API
            YouTube.CommentThreads.Insert request = youTubeService.commentThreads()
                    .insert(part, commentThread);

            CommentThread response = request.execute();
            eventLogService.logEvent("Added comment: " + payload.get("comment"));
            //Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Comment added successfully");
            responseBody.put("commentId", response.getId()); // ✅ Return comment ID

            return ResponseEntity.ok(responseBody);
          //  return ResponseEntity.ok("Comment added successfully: " + response.getId());
        } catch (Exception e) {
            e.printStackTrace();
            responseBody.put("error", "Failed to add comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add comment: " + e.getMessage());
        }
    }
    @PutMapping("/update-title")
    public ResponseEntity<String> updateVideoTitle(@RequestBody Map<String, String> payload) {
        try {
            String videoId = payload.get("videoId");
            String newTitle = payload.get("title");
            
            // Fetch existing video details
            YouTube.Videos.List listRequest = youTubeService.videos()
                .list(Collections.singletonList("snippet")); // Pass the part parameter as a String
            listRequest.setId(Collections.singletonList(videoId)); // The setId method expects a List<String>

            VideoListResponse listResponse = listRequest.execute();
            if (listResponse.getItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found with ID: " + videoId);
            }

            Video video = listResponse.getItems().get(0);

            // Update the title
            VideoSnippet snippet = video.getSnippet();
            snippet.setTitle(newTitle);

            // Preserve categoryId (required by YouTube API)
            snippet.setCategoryId(video.getSnippet().getCategoryId());

            // Prepare update request
            Video updatedVideo = new Video();
            updatedVideo.setId(videoId);
            updatedVideo.setSnippet(snippet);

            YouTube.Videos.Update updateRequest = youTubeService.videos()
                .update(Collections.singletonList("snippet"), updatedVideo); // Pass the part parameter as a String

            Video response = updateRequest.execute();

            return ResponseEntity.ok("Video title updated successfully: " + response.getSnippet().getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update video title: " + e.getMessage());
        }
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable String commentId) {
        try {
            if (commentId == null || commentId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: commentId is required.");
            }

            // Call YouTube API to delete the comment
            YouTube.Comments.Delete request = youTubeService.comments().delete(commentId);
            request.execute();
            
            
            eventLogService.logEvent("Deleted comment with ID: " + commentId);
            return ResponseEntity.ok("Comment deleted successfully: " + commentId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete comment: " + e.getMessage());
        }
    }
    
    @GetMapping("/details/{videoId}")
    public ResponseEntity<?> getVideoDetails(@PathVariable String videoId) {
        try {
            // Fetch video details from YouTube API
            YouTube.Videos.List listRequest = youTubeService.videos()
                    .list(Collections.singletonList("snippet,contentDetails,statistics")); // Fetch required details
                   // .setId(videoId); // Set the video ID
            listRequest.setId(Collections.singletonList(videoId)); 

            VideoListResponse listResponse = listRequest.execute();
            if (listResponse.getItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found with ID: " + videoId);
            }

            Video video = listResponse.getItems().get(0);
            return ResponseEntity.ok(video); // Return video details as JSON
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to fetch video details: " + e.getMessage());
        }
    }

}