# RideMate - Carpooling Application

A modern, full-stack carpooling/ride-sharing application built with Spring Boot and React, featuring OAuth2 authentication, MongoDB integration, and a beautiful, responsive UI.

## 🚀 Features

### Authentication & Security
- **OAuth 2.0 Integration**: Sign in with Google
- **Local Authentication**: Traditional email/password signup and login
- **Session-Based Security**: Secure session management with Spring Security
- **Protected Routes**: Authenticated access control

### Database Architecture
- **Dual Database System**:
  - **MySQL**: Core relational data (Users, Rides, Bookings, Ratings)
  - **MongoDB**: High-volume, document-based data (Messages, Notifications)

### Core Functionality
- **Ride Management**: Create, search, and book rides
- **Real-time Messaging**: WebSocket-powered chat between users
- **Notifications**: Real-time notifications for bookings and updates
- **Rating System**: Comprehensive driver and passenger ratings
- **User Profiles**: Manage profile information and view ratings

### Modern UI/UX
- **Responsive Design**: Mobile-first, works on all devices
- **Modern Aesthetics**: Clean, professional interface with smooth animations
- **Accessibility**: WCAG compliant color schemes and navigation

## 📋 Prerequisites

- **Java**: JDK 21 or higher
- **Node.js**: v16 or higher
- **MySQL**: 8.0 or higher
- **MongoDB**: 4.4 or higher
- **Maven**: 3.6 or higher
- **Google OAuth 2.0 Credentials** (for OAuth login)

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/chinmaykulkarnii/ridemate-app.git
cd ridemate-app
```

### 2. Database Setup

#### MySQL Setup
```sql
-- Create database
CREATE DATABASE ridemate_db;

-- Create user (optional, update credentials in application.properties)
CREATE USER 'ridemate_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ridemate_db.* TO 'ridemate_user'@'localhost';
FLUSH PRIVILEGES;
```

#### MongoDB Setup
```bash
# Start MongoDB service
mongod --dbpath /path/to/your/data/directory

# MongoDB will automatically create the 'ridemate_mongo' database when first accessed
```

### 3. OAuth 2.0 Configuration

#### Google OAuth Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable "Google+ API"
4. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client ID"
5. Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
6. Save the Client ID and Client Secret

### 4. Backend Configuration

Navigate to `ridemate-backend/src/main/resources/application.properties`:

```properties
spring.application.name=RideMate

# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ridemate_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/ridemate_mongo
spring.data.mongodb.database=ridemate_mongo

# Google OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET

# CORS Configuration
app.cors.allowed-origins=http://localhost:3000

# Server Configuration
server.port=8080
```

**Environment Variables (Recommended for Production):**
```bash
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret
```

### 5. Run Backend

```bash
cd ridemate-backend

# Using Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/ridemate-backend-0.0.1-SNAPSHOT.jar
```

Backend will start on `http://localhost:8080`

### 6. Run Frontend

```bash
cd ridemate-frontend

# Install dependencies
npm install

# Start development server
npm start
```

Frontend will start on `http://localhost:3000`

## 🗂️ Project Structure

```
ridemate-app/
├── ridemate-backend/           # Spring Boot Backend
│   ├── src/main/java/com/ridemate/
│   │   ├── config/             # Security & CORS configuration
│   │   ├── controller/         # REST API endpoints
│   │   ├── document/           # MongoDB document models
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── exception/          # Exception handling
│   │   ├── model/              # JPA entities (MySQL)
│   │   ├── repository/         # Data access layer
│   │   │   └── mongo/          # MongoDB repositories
│   │   ├── security/           # OAuth2 & Security components
│   │   └── service/            # Business logic
│   └── src/main/resources/
│       └── application.properties
│
└── ridemate-frontend/          # React Frontend
    ├── public/
    └── src/
        ├── components/         # React components
        │   ├── auth/           # Login, Signup
        │   ├── bookings/       # Booking management
        │   ├── common/         # Navbar, Footer, ProtectedRoute
        │   ├── messages/       # Real-time chat
        │   ├── notifications/  # Notification center
        │   ├── ratings/        # Rating system
        │   └── rides/          # Ride search & management
        ├── context/            # React Context (Auth, Notifications)
        ├── services/           # API service layer
        └── styles/             # Global & component styles
```

## 🔑 Key Technologies

### Backend
- **Spring Boot 3.2.0**: Application framework
- **Spring Security**: OAuth2 & session-based authentication
- **Spring Data JPA**: MySQL ORM
- **Spring Data MongoDB**: MongoDB integration
- **Spring WebSocket**: Real-time messaging
- **MySQL 8.0**: Relational database
- **MongoDB**: Document database

### Frontend
- **React 19.2.0**: UI library
- **React Router 7.9.5**: Client-side routing
- **Axios**: HTTP client
- **WebSocket (SockJS + STOMP)**: Real-time communication
- **Modern CSS**: Custom responsive design system

## 📊 Database Schema

### MySQL Tables
- **users**: User profiles, authentication, ratings
- **rides**: Ride information (origin, destination, price, seats)
- **bookings**: Ride bookings and status
- **ratings**: Driver and passenger ratings

### MongoDB Collections
- **messages**: Chat messages between users
- **notifications**: User notifications and alerts

## 🔐 Security Features

- **OAuth 2.0 Authentication**: Secure login with Google
- **Session Management**: HTTP-only session cookies
- **Password Encryption**: BCrypt hashing for local accounts
- **CORS Protection**: Configured for frontend origin
- **Protected API Endpoints**: Authenticated access only

## 🚦 API Endpoints

### Authentication
- `POST /api/auth/login` - Local login
- `POST /api/auth/signup` - User registration
- `POST /api/auth/logout` - Logout
- `GET /oauth2/authorization/{provider}` - OAuth login

### Rides
- `POST /api/rides` - Create ride
- `GET /api/rides/search` - Search rides
- `GET /api/rides/{id}` - Get ride details
- `PUT /api/rides/{id}` - Update ride
- `DELETE /api/rides/{id}` - Delete ride

### Bookings
- `POST /api/bookings` - Create booking
- `GET /api/bookings/passenger` - Get passenger bookings
- `GET /api/bookings/driver` - Get driver bookings
- `PUT /api/bookings/{id}/confirm` - Confirm booking
- `PUT /api/bookings/{id}/cancel` - Cancel booking

### Messages
- `GET /api/messages/conversation/{userId}` - Get conversation
- `GET /api/messages/unread` - Get unread messages
- `PUT /api/messages/{id}/read` - Mark message as read
- **WebSocket**: `/ws` - Real-time messaging

### Notifications
- `GET /api/notifications` - Get all notifications
- `GET /api/notifications/unread` - Get unread notifications
- `PUT /api/notifications/{id}/read` - Mark as read
- `PUT /api/notifications/read-all` - Mark all as read

## 🎨 UI Features

- **Modern Color Scheme**: Indigo primary, semantic colors
- **Responsive Grid System**: Mobile-first layout
- **Custom Components**: Buttons, cards, forms, badges
- **Smooth Animations**: Transitions and hover effects
- **Loading States**: Spinners and skeleton screens
- **Error Handling**: User-friendly error messages

## 🧪 Testing

```bash
# Backend tests
cd ridemate-backend
mvn test

# Frontend tests
cd ridemate-frontend
npm test
```

## 🚀 Production Deployment

### Backend
1. Update `application.properties` with production database credentials
2. Set OAuth redirect URIs to production URLs
3. Build JAR: `mvn clean package`
4. Deploy JAR to server
5. Use environment variables for sensitive data

### Frontend
1. Update API endpoint in `src/services/api.js`
2. Build production bundle: `npm run build`
3. Serve `build/` directory with nginx or hosting service

## 📝 Environment Variables

Create `.env` files for local development:

**Backend**: Set in system environment or IDE
```
GOOGLE_CLIENT_ID=your_client_id
GOOGLE_CLIENT_SECRET=your_client_secret
```

**Frontend** (`.env`):
```
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_WS_URL=http://localhost:8080/ws
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- **Chinmay Kulkarni** - Initial work

## 🙏 Acknowledgments

- Spring Boot and Spring Security teams
- React and React Router communities
- OAuth 2.0 provider (Google)
- MongoDB and MySQL teams

## 📞 Support

For support, email chinmay@example.com or open an issue in the GitHub repository.

---

**Happy Carpooling! 🚗💨**
