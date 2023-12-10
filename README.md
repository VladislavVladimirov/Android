# NetologyAndroid
Дипломный проект профессии "Android-разработчик"
Приложение представляет с собой небольшую социальную сеть с лентой постов, лентой событий и профилем пользователя.
Проект реализован на архитектуре MVVM с использованием ViewModel, LiveData, Flow, Repository, Entity, Dao. Данные хранятся в локальной базе данных Room и подгружаются с удаленного сервера с помощью OkHttp.

В приложении используется подход Single Activity, применяется библиотека Fragment и библиотека Navigation.
Для инъекции зависимостей применятся библиотека Dagger

Приложение поддерживает регистрацию и аутентификацию с использование логина, пароля и аватара (он необязателен),
без регистрации и аутентификации нельзя ставить лайки, принимать участие в событиях и редактировать чужие посты и события.

Для отображения ленты постов используется RecyclerView с загрузочными header и footer, а также с помощью RemoteMediator из библиотеки Paging 3



Реализованы следующие возможности по взаимодействию с постами:
- Создание
- Изменение
- Удаление
- Можно отправить текст поста в любым доступным способом
- Лайк поста
- Отображение и просмотр вложений: аудио, фото и видео
- Отображение превью для видео на YouTube, при наличии ссылки в тексте поста
- Переход по внешним ссылкам
  
 Реализованы следующие возможности по взаимодействию с событиями:
- Создание
- Изменение
- Удаление
- Можно отправить текст события в любым доступным способом
- Лайк события
- Принять участие в событии
- Отображение и просмотр вложений: аудио, фото и видео
- Отображение превью для видео на YouTube, при наличии ссылки в тексте события
- Переход по внешним ссылкам

  Для отображения и просмотра медиавложений применяются такие библиотеки как:
 - ImagePicker
 - MediaPlayer
 - TouchImageView
 - Glide

  В профиле пользователя отображаются его имя, аватар, список мест работы пользователя, который можно редактировать, а также список созданных им постов.

  
