# BookBridge Endpoints

Base URLs:
- Local: `http://localhost:8082`
- Docker: `http://localhost:8080`

## Web Pages / Thymeleaf Routes
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/` | Home page | No |
| GET | `/login` | Login page | No |
| GET | `/register` | Registration page | No |
| POST | `/register` | Register new user | No |
| GET | `/dashboard` | Dashboard page | Yes |
| GET | `/books` | Browse books page | No |
| GET | `/chat` | Chat page | Yes |
| GET | `/chat/{conversationId}` | Open a specific chat thread | Yes |
| POST | `/chat/start` | Start or open a conversation for a book | Yes |
| POST | `/chat/{conversationId}/message` | Send a chat message | Yes |
| GET | `/seller/books/new` | Seller listing form | Yes (ADMIN, SELLER) |
| POST | `/seller/books/new` | Submit new listing | Yes (ADMIN, SELLER) |
| GET | `/seller/books/{bookId}/edit` | Edit listing form | Yes (ADMIN, SELLER) |
| POST | `/seller/books/{bookId}/edit` | Update listing | Yes (ADMIN, SELLER) |
| GET | `/orders` | Orders page | Yes |
| GET | `/admin` | Admin page | Yes (ADMIN) |

## Books API
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/books` | List books (paginated, supports title/author filters) | No |
| GET | `/api/books/{bookId}` | Get book details | No |
| POST | `/api/books?userId=` | Create a new listing | Yes (ADMIN, SELLER) |
| PUT | `/api/books/{bookId}` | Update listing | Yes (ADMIN, SELLER) |
| DELETE | `/api/books/{bookId}` | Delete listing | Yes (ADMIN, SELLER) |

Query parameters for `GET /api/books`:
- `page` — page number (default `0`)
- `size` — page size (default `10`)
- `title` — search by title
- `author` — search by author

## Users API
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/users/{userId}` | Get user by ID | Yes (ADMIN, BUYER, SELLER) |
| GET | `/api/users/username/{username}` | Get user by username | Yes (ADMIN, BUYER, SELLER) |
| PUT | `/api/users/{userId}` | Update a user | Yes (ADMIN, BUYER, SELLER) |
| DELETE | `/api/users/{userId}` | Delete a user | Yes (ADMIN) |
| GET | `/api/users/me/wishlist` | Get current user's wishlist | Yes |
| POST | `/api/users/me/wishlist/{bookId}` | Add a book to wishlist | Yes |
| DELETE | `/api/users/me/wishlist/{bookId}` | Remove a book from wishlist | Yes |

## Conversations & Messages API
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/conversations?bookId=&userId=` | List conversations filtered by book or user | Yes |
| POST | `/api/conversations` | Start a conversation | Yes |
| GET | `/api/conversations/{conversationId}` | Get a conversation by ID | Yes |
| PATCH | `/api/conversations/{conversationId}/response` | Update seller response | Yes (ADMIN, SELLER) |
| DELETE | `/api/conversations/{conversationId}` | Delete conversation | Yes (ADMIN, SELLER) |
| GET | `/api/conversations/{conversationId}/messages` | Get messages in a conversation | Yes |
| POST | `/api/conversations/{conversationId}/messages` | Send a message in a conversation | Yes |

## Reviews API
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/reviews` | List reviews, optionally filter by user or book | No |
| POST | `/api/reviews` | Leave a review | Yes (ADMIN, BUYER, SELLER) |
| DELETE | `/api/reviews/{reviewId}` | Delete a review | Yes (ADMIN, BUYER, SELLER) |

## Categories API
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/categories` | List all categories | No |

## Transactions API
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/transactions/sell?sellerId=` | Mark a book as sold | Yes (SELLER, ADMIN) |
| GET | `/api/transactions` | List all transactions | No |
| GET | `/api/transactions/{transactionId}` | Get transaction by ID | No |
| GET | `/api/transactions/seller/{sellerId}` | Get seller transactions | No |
| GET | `/api/transactions/buyer/{buyerId}` | Get buyer transactions | No |
| GET | `/api/transactions/user/{userId}` | Get transactions for a user | No |
| GET | `/api/transactions/book/{bookId}` | Get transaction history for a book | No |

## Authentication Notes
- Authentication is currently form-based via Spring Security and Thymeleaf pages.
- The project does not expose the JWT auth endpoints that were listed in the earlier draft.
