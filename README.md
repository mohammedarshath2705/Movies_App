
# MovieApp

**MovieApp** is a movie information management application built with Spring Boot, PostgreSQL, and Redis. The application fetches top-rated movies from the TMDb API and stores them in a PostgreSQL database for easy access. It provides multiple API endpoints that allow users to fetch movie details based on various parameters such as movie ID, release date, IMDb rating, and more.






## Features

- **Pagination**: Implements pagination to handle large datasets effectively and optimize API responses.
- **Redis Caching**: Utilizes Redis caching for faster retrieval of frequently accessed movie data.
- **Multiple Endpoints**: Provides various API endpoints to fetch movie details.
- **Cron Job**: Automatically fetches newly released movies every day to keep the database up-to-date.
- **Deployment on Render**: The application is deployed on Render, ensuring scalability and availability.
## Technology Used

- Java (Spring Boot)
- PostgreSQL (Database)
- Redis (Caching)
- TMDb API (Movie Data)
- Cron Jobs (Automated Daily Fetch)
- Render (Deployment)
- Git installed on your machine.
## Database Schema

### `Movie`    Table

| Column | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `UUID(PK)` |  Unique Movie ID
| `title` | `String` | Movie Title
| `genre` | `String` | Movie Genre
| `language` | `String` | Movie Language
| `overview` | `String` | Short Description
| `release_date` | `String` | Release Date
| `imdb_rating` | `Double` | Imdb Rating
| `director` | `String` | Movie Director
| `poster` | `String` |  Poster Image URL



## API Endpoints

### `Movie`    Table

| Method | Endpoint     | Description                |
| :-------- | :------- | :------------------------- |
| `GET` | `/movies/{id}` |  Get a movie by ID
| `GET` | `/movies/all` | Get all movies
| `GET` | `/movies/byReleaseDate` | 	Get movies by release date
| `GET` | `/movies/byImdbRating` | Get movies by IMDb rating
| `GET` | `/movies/moviesByDate?date={date}` | Get movies released on a specific date



## Example EndPoints

#### 1 . Get a Movie by id

```http
  GET /movies/{id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `movieid` | `UUID` | **Required**. The Unique ID of Movie .

#### Example: 

```bash
 GET http://localhost:8080/movies/7a00e6d6-e306-455e-9abd-3d0e68590509
```


#### 2. Get a Movies by ImdbRating(with Pagination)

```http
  GET /movies/byImdbRating?page={page}&size={size}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `page` | `int` | **Required**. The page number (starting from 0).
| `size` | `int` | **Required**.  Number of movies to retrieve per page.

#### Example: 

```bash
GET http://localhost:8080/movies/byImdbRating?page=0&size=20
```

#### 3. Get Movie by Release Date(with Pagination)

```http
 GET /movies/byReleaseDate?page={page}&size={size}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `page` | `int` | **Required**. The page number (starting from 0).
| `size` | `int` | **Required**.  Number of movies to retrieve per page.

#### Example: 

```bash
 GET http://localhost:8080/movies/byReleaseDate?page=0&size=33
```

#### 4. Get Movie by Specific Date

```http
 GET /movies/moviesByDate?date={date}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `date` | `String` | **Required**. The release date in YYYY-MM-DD format.

#### Example: 

```bash
 GET http://localhost:8080/movies/moviesByDate?date=2025-01-30
```

#### 5. List All Movies(with Pagination)

```http
 GET /movies/all?page={page}&size={size}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `page` | `int` | **Required**. The page number (starting from 0).
| `size` | `int` | **Required**.  Number of movies to retrieve per page.

#### Example: 

```bash
 GET http://localhost:8080/movies/all?page=0&size=21
```



## Deployment

You can deploy the application to **Render** for cloud hosting.

- Connect your GitHub repository.
- Create a new service on Render.
- Set up the PostgreSQL and Redis services on Render.
- Set up Cron jobs in render.
- Test the Deployment in Browser or Postman.






