# Project *Kuchnia Bartosza* 🍔

**Kuchnia Bartosza** is a web application for managing cooking recipes. The application is built with the Spring Framework and MySQL Database. The main purpose of creating this application was to learn Spring, as well as unit and integration testing.

---

## 🛠️ Technologies Used

- Java 21
- Spring Boot 3.4.4
- Spring Data JPA
- Spring Web MVC
- Spring Security
- OAuth2 Login with Google
- Docker & Docker Compose
- Maven 4.0.0
- MySQL
- H2 Database
- Hibernate
- Liquibase
- JUnit 5
- Mockito 2
- AssertJ 3.26.0
- HTML / CSS / JavaScript
- Thymeleaf
- IntelliJ IDEA

---

## ✅ Features

### 🧾 Recipe Management

- Browse and sort recipes by date or name with pagination
- Search for recipes by type, name, ingredients, description, or preparation steps
- View recipe details including:
   - Preparation & cooking time
   - Ingredients & instructions
   - Difficulty level
   - Average rating & comments
   - Image preview
- Download a recipe as a PDF file

### 🔐 Authentication & Authorization

- Register or log in via Google (OAuth2)
- Access restricted based on user roles
- Password reset link send to user email
- Activate new account by link send to user email

### 👤 User Panel (Authenticated Users)

- Rate and comment on recipes
- Add or remove recipes from favorites
- View list of:
   - Favorite recipes
   - Rated recipes
   - Comments
- Update user data and password
- Delete user account

### 🛠️ Admin Panel (Authenticated Admins)

- Add, update, and delete recipes
- Add, update, and delete meal types
- View and moderate comments (approve or delete)
- View full user details:
   - User’s comments
   - Rated recipes
   - Favorite recipes

---

## 👥 Who is it for?

Anyone who wants to browse or share cooking recipes. Great for foodies looking for new inspiration or cooks wanting to share their dishes.

---

## 🚀 How to Run the App

### Option 1: Using Docker (production profile)

1. Install Docker
2. Clone the project:
   ```bash
   git clone https://github.com/BartKempa/recipes.git
   cd recipes
   ```
3. Create a `.env` file in the root of the project with the following content:
   ```env
   DB_PORT=3306
   DB_NAME=recipes
   DB_URL=jdbc:mysql://mysql:${DB_PORT}/${DB_NAME}
   DB_USERNAME=root
   DB_PASSWORD=pass
   SPRING_PROFILES_ACTIVE=prod
   GOOGLE_CLIENT_ID=yourGoogleClientId
   GOOGLE_CLIENT_SECRET=yourGoogleClientSecret
   EMAIL_PASSWORD=password to your mailbox
   ```
4. Run the application:
   ```bash
   docker compose up
   ```
5. Visit the app at [http://localhost:8080](http://localhost:8080)

---

### Option 2: Using IDE (developer profile)

1. Open the project in your favorite IDE (e.g. IntelliJ)
2. Make sure the `dev` profile is active (H2 in-memory DB)
3. Create a `secret.properties` file in the resources directory with the following content:
```env
spring.mail.password=password to your mailbox
spring.security.oauth2.client.registration.google.client-id=yourGoogleClientId
spring.security.oauth2.client.registration.google.client-secret=yourGoogleClientSecre

```
4. Run the application from your IDE
5. Visit [http://localhost:8080](http://localhost:8080)

---

## 🧪 Testing

The application includes unit and integration tests using:

- JUnit 5
- Mockito
- AssertJ

Functionalities tested include:

- User registration
- Login
- Recipe interactions (create, read, update, delete)
- Feedback (comments and ratings)

---

## 🔮 Future Improvements

1. Email confirmation after user registration ✅
2. “Products I need” section
3. User blocking & moderation tools 
4. Hosting on AWS

---

## 📁 Project Structure

- `src/main/java` – Application source code
- `src/main/resources` – Static resources and configuration
- `src/test/java` – Unit and integration tests
- `Dockerfile` – Multi-stage Docker build
- `docker-compose.yml` – Services: MySQL and App container
- `.env` – Environment variables for production

---

## 🧷 Notes

- Google OAuth2 login requires setting up credentials via Google Cloud Console
- For development, the app uses an H2 in-memory database and local file storage
- In production, MySQL is used along with mounted volume for uploads

---

## 📄 License & Third-Party Libraries

This project uses the [OpenPDF](https://github.com/LibrePDF/OpenPDF) library for generating PDF files (planned feature: `pdfCreator`).

**OpenPDF** is licensed under a dual open source license: **LGPL** and **MPL**. This means you are free to use it in both open-source and proprietary applications, as long as you comply with the terms of one of the licenses.

As required, this project includes attribution to OpenPDF and provides a link to the source code and license:

- Library: OpenPDF
- Repository: https://github.com/LibrePDF/OpenPDF
- License: [LGPL 2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html) / [MPL 1.1](https://www.mozilla.org/MPL/1.1/)

The application does not modify the OpenPDF source code directly. If modifications are made in the future, they will be made available under the terms of the LGPL/MPL license.

---


Happy cooking! 🍽️
