version: '3.9'
services:
  app:
    build: .
    container_name: kiris_app
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/kiris
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
  db:
    image: postgres:15
    container_name: kiris_db
    environment:
      POSTGRES_DB: kiris
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - kiris_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

volumes:
  kiris_data:
