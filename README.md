# YouTube API Integration Frontend Documentation

This Angular frontend application integrates with a Spring Boot backend to interact with YouTube's API. Below are the implemented endpoints with usage examples and expected responses.

## API Endpoints Overview

### 1. Add Comment to Video
**Endpoint**  
`POST /api/youtube/comment`

**Request Example**
{
"videoId": "ABC123xyz",
"comment": "Great tutorial content!"
}

text

**Success Response**
{
"message": "Comment added successfully",
"commentId": "UgzX6789PQRS1TUVWXYZ"
}

text

**Error Responses**
// Missing videoId
{
"error": "Error: videoId is required."
}

// Empty comment
{
"error": "Error: comment text cannot be empty."
}

text

### 2. Update Video Title
**Endpoint**  
`PUT /api/youtube/update-title`

**Request Example**
{
"videoId": "XYZ456abc",
"title": "Updated Video Title - 2025 Edition"
}

text

**Success Response**  
`Video title updated successfully: Updated Video Title - 2025 Edition`

**Error Responses**  
- `404 Video not found with ID: [videoId]`
- `400 Failed to update video title: [error details]`

### 3. Delete Comment
**Endpoint**  
`DELETE /api/youtube/comment/{commentId}`

**Success Response**  
`Comment deleted successfully: UgzX6789PQRS1TUVWXYZ`

**Error Response**  
`400 Failed to delete comment: [error details]`

### 4. Get Video Details
**Endpoint**  
`GET /api/youtube/details/{videoId}`

**Success Response**
{
"id": "XYZ456abc",
"snippet": {
"title": "Spring Boot OpenAPI Tutorial",
"description": "Comprehensive guide to API documentation...",
"categoryId": "27",
"tags": ["spring", "openapi"]
},
"statistics": {
"viewCount": "15000",
"likeCount": "850"
}
}
