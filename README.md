# Java-filmorate
Template repository for Filmorate project.
![ER DIAGRAM](DB.png)

- **friends**: таблица отображения друзей друг у друга.
- **rating**: таблица mpa-рейтинга, у фильма может быть один рейтинг. 
- **likes**: таблица связввет id фильма с id пользователя, который поставил лайк. 
- **genre**: фильм можно отести к нескольким жанрам, но в каждому жанру может принадлежать множество фильмов. **film_genre** соединительная таблица.

Получить все фильмы:
```
SELECT *  
FROM film;
```
Получить всех пользователей:
```
SELECT *  
FROM user;
```
Получить топ 10 фильмов:
```
SELECT f.name, COUNT(l.user_id) AS count_like
FROM film AS f
LEFT JOIN likes AS l ON f.id = l.film_id
GROUP BY f.id
ORDER BY count_like DESC
LIMIT 10;
```