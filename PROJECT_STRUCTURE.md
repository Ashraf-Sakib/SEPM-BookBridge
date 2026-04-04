# Spring Boot Project Structure

```
backend/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/bookbridge/BookBridge/
│   │   │   ├── BookBridgeApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── JwtConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AppPageController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── BookController.java
│   │   │   │   ├── BookPageController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── ChatController.java
│   │   │   │   ├── ConversationController.java
│   │   │   │   ├── ReviewController.java
│   │   │   │   ├── SellerBookPageController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── BookCreateRequest.java
│   │   │   │   │   ├── ConversationRequest.java
│   │   │   │   │   ├── ConversationResponseRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── MessageRequest.java
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── SellBookRequest.java
│   │   │   │   │   └── SendMessageRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── AuthResponse.java
│   │   │   │       ├── BookResponse.java
│   │   │   │       ├── CategoryResponse.java
│   │   │   │       ├── ChatThreadView.java
│   │   │   │       ├── ConversationResponse.java
│   │   │   │       ├── MessageResponse.java
│   │   │   │       ├── PageResponse.java
│   │   │   │       ├── PagedResponse.java
│   │   │   │       ├── TransactionResponse.java
│   │   │   │       ├── UserResponse.java
│   │   │   │       └── WishlistItemResponse.java
│   │   │   ├── entity/
│   │   │   │   ├── Book.java
│   │   │   │   ├── BookImage.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── Conversation.java
│   │   │   │   ├── Message.java
│   │   │   │   ├── Review.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── User.java
│   │   │   │   ├── Wishlist.java
│   │   │   │   └── enums/
│   │   │   │       ├── BookCondition.java
│   │   │   │       ├── ListingStatus.java
│   │   │   │       └── MessageStatus.java
│   │   │   ├── exception/
│   │   │   │   ├── ApiErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── UnauthorizedException.java
│   │   │   ├── repository/
│   │   │   │   ├── BookRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── ConversationRepository.java
│   │   │   │   ├── MessageRepository.java
│   │   │   │   ├── ReviewRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── WishlistRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtAuthFilter.java
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   └── service/
│   │   │       ├── AuthService.java
│   │   │       ├── BookService.java
│   │   │       ├── CategoryService.java
│   │   │       ├── ConversationService.java
│   │   │       ├── FileStorageService.java
│   │   │       ├── MessageService.java
│   │   │       ├── TransactionService.java
│   │   │       ├── UserService.java
│   │   │       └── WishlistService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── data.sql
│   │       ├── db/
│   │       │   └── init.sql
│   │       └── templates/
│   │           ├── admin.html
│   │           ├── auth/
│   │           │   ├── login.html
│   │           │   └── register.html
│   │           ├── books/
│   │           │   ├── edit.html
│   │           │   ├── list.html
│   │           │   └── sell.html
│   │           ├── chat.html
│   │           ├── dashboard.html
│   │           ├── fragments/
│   │           │   └── layout.html
│   │           ├── index.html
│   │           └── orders.html
│   └── test/
│       ├── java/com/bookbridge/BookBridge/
│       │   ├── BookBridgeApplicationTests.java
│       │   ├── controller/
│       │   └── service/
│       └── resources/
│           └── application-test.yml
```

## Key Dependencies (pom.xml)
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `postgresql` (runtime)
- `io.jsonwebtoken:jjwt-api` (JWT)
- `lombok` (optional)
