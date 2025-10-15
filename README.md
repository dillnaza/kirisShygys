# Kiris Shyǵys

**Kiris Shyǵys** — backend-сервис для мобильного приложения, помогающего пользователям управлять своими финансами и развивать финансовую грамотность.  
Система позволяет вести учёт доходов и расходов, анализировать баланс, создавать категории и теги, экспортировать отчёты в PDF и поддерживает систему геймификации.

## Технологии
**Java 17 · Spring Boot 3.4.0 · Spring Security · JWT · JPA/Hibernate · PostgreSQL · iText 7 · MailSender · OAuth2 · Swagger**

## Основные возможности

### Аутентификация и безопасность
- Регистрация и вход с валидацией данных
- Подтверждение email через HTML-письмо и deep-link (`kirisShygys://confirm`)
- Восстановление пароля через email (`kirisShygys://reset-password`)
- JWT-аутентификация (access + refresh токены)
- OAuth2-авторизация (Google)

### Управление финансами
- Учёт доходов и расходов
- Категории и подкатегории
- Теги и счета с привязкой к пользователю
- Повторяющиеся транзакции (день, неделя, месяц, год)
- Расчёт баланса и фильтрация по периоду
- Экспорт транзакций в PDF

### Геймификация
- Система ежедневных «огоньков» активности (streak system)
- Автоматическое обновление streak при добавлении транзакции

### Email-интеграция
- HTML-письма для подтверждения аккаунта и восстановления пароля
- Deep-links для взаимодействия с мобильным приложением

## Архитектура
Многоуровневая структура с разделением ответственности:
- `controller/` — REST-контроллеры
- `service/` — бизнес-логика (Auth, Transaction, Category, Balance, Tag, Streak и др.)
- `repository/` — Spring Data JPA репозитории
- `entity/` — JPA-сущности (User, Transaction, Category, Tag, Account и др.)
- `dto/` — DTO для обмена данными между слоями
- `mapper/` — мапперы Entity ↔ DTO
- `util/` — JWT и вспомогательные классы
- `filter/` — JwtAuthenticationFilter
- `interceptor/` — AuthInterceptor
- `exception/` — кастомные исключения и глобальный обработчик
- `resources/` — статические файлы, конфигурации и шрифты для PDF

Основные сервисы:
- `AuthServiceImpl` — регистрация, логин, refresh-токен, email-подтверждение, сброс пароля
- `TransactionServiceImpl` — CRUD транзакций, экспорт PDF, streak-интеграция
- `CategoryServiceImpl` — категории, подкатегории, системные категории, soft delete
- `BalanceServiceImpl` — подсчёт доходов, расходов и общего баланса
- `StreakServiceImpl` — система ежедневных «огоньков»
- `EmailServiceImpl` — отправка текстовых и HTML-писем
- `TransactionEntityService` — базовый generic-сервис с soft delete и логированием