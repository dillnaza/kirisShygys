version: '3.9'
services:
  app:
    build: .
    container_name: kiris-app
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/kirisdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: 12345
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT: org.hibernate.dialect.PostgreSQLDialect
    networks:
      - kiris_network

  postgres:
    image: postgres:15
    container_name: kiris-postgres
    restart: always
    environment:
      POSTGRES_DB: kirisdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: 12345
    volumes:
      - db_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    networks:
      - kiris_network

volumes:
  db_data:

networks:
  kiris_network:
    driver: bridge
