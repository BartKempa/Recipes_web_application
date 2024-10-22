# Project *Kuchnia Bartosza* 🍔 


**Kuchnia Bartosza** is a web application for managing cooking recipes. The application is built with the Spring Framework and MySQL Database. The main purpose of creating this application was to learn Spring, as well as unit and integration testing.

## Used Tools

For the implementation of the web application, the following tools and technologies are used:

* Java 17
* Spring Boot v3.2.5
* Spring Data JPA
* Spring Web MVC
* Spring Security
* Maven v4.0.0
* MySQL
* H2 Database
* Hibernate
* Liquibase
* JUnit 5
* Mockito 2
* AssertJ v3.26.0
* HTML
* CSS
* JavaScript
* Thymeleaf
* IntelliJ IDEA

## Functionalities

The application provides the following functionalities, allowing the user to easily manage their activity:

* Sort all recipes by date or name with pagination.
* Search for cooking recipes by type of meal and sort results.
* Search for cooking recipes by name, ingredients, description, preparation steps, or other keywords and sort results.
* After choosing a recipe, the user can open the recipe page with all details about it: preparation time, cooking time, description, servings, difficulty level, ingredients, direction steps, image, average rating, rating count, and comments.

### Authenticated users have additional functionalities on the recipe page:

* Submit feedback under the recipe.
* Rate the recipe and update the rating.
* Add the recipe to user's favorites and remove the recipe from user's favorites.
* Browse user's favorite recipes.

### Authenticated users also have access to a user panel with the following functionalities:

* Display user's favorite recipes.
* Display user's rated recipes.
* Display user's comments.
* Update user data.
* Update user credentials.
* Delete user account.

### Authenticated admins have access to an admin panel with the following functionalities:

* Add a new recipe.
* Update a recipe.
* Delete a recipe.
* Add a new type of meal.
* Update a type of meal.
* Delete a type of meal.
* Display all comments, including information on whether they are approved.
* Approve or delete comments.
* Display a list of users with the option to view all details about a user, including a list of comments, rated recipes, and favorite recipes.

## Who can use it?

This web application can be used by anyone who wants to get ideas for cooking or help other users by sharing their own recipes.

## How it can be used?

The site has not been hosted or deployed yet. It is launched and run on localhost for now. To use it:

1. Install MySQL.
2. Open Git Bash.
3. Change the current working directory to the location where you want the cloned directory.
4. Execute the command:
git clone https://github.com/BartKempa/recipes.git
5. Press Enter to create your local clone.
6. To use the production profile, configure the datasource in `src/main/resources/application-prod.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/recipes
    username: root
    password: password
```
7. The server is running on localhost:8080.

## Testing

The application includes unit and integration tests using JUnit, Mockito, and AssertJ. Tests cover various functionalities such as user registration, recipe management, and feedback submission.

## For future work/Improvements

The application includes unit and integration tests using JUnit, Mockito, and AssertJ. Tests cover various functionalities such as user registration, recipe management, and feedback submission.
1. User account activation after registration via email.
2. Register/Login via social networks.
3. A section for "Products that I need."
4. User blocking functionality.


