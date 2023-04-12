package com.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netology.MyApp.R
import com.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 1L
    private var posts = listOf(
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и упралению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy//fyb ",
            published = "2 сентября в 18:30",
            likedByMe = false,
            likes = 126,
            shares = 76,
            views = 4543,
            authorAvatar = R.drawable.ic_launcher_foreground
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех: на следующей неделе разбираемся с разработкой мобильных приложений, учимся рассказывать истории и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
            published = "18 сентября в 10:12",
            likedByMe = false,
            likes = 999,
            shares = 120,
            views = 6764,
            authorAvatar = R.drawable.ic_launcher_foreground

        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Языков программирования много, и выбрать какой-то один бывает нелегко. Собрали подборку статей, которая поможет вам начать, если вы остановили свой выбор на JavaScript.",
            published = "19 сентября в 10:24",
            likedByMe = false,
            likes = 343225,
            shares = 203231,
            views = 1023202,
            authorAvatar = R.drawable.ic_launcher_foreground
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Большая афиша мероприятий осени: конференции, выставки и хакатоны для жителей Москвы, Ульяновска и Новосибирска \uD83D\uDE09",
            published = "19 сентября в 14:12",
            likedByMe = false,
            likes = 34543,
            shares = 20127,
            views = 100200,
            authorAvatar = R.drawable.ic_launcher_foreground
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Диджитал давно стал частью нашей жизни: мы общаемся в социальных сетях и мессенджерах, заказываем еду, такси и оплачиваем счета через приложения.",
            published = "20 сентября в 10:14",
            likedByMe = false,
            likes = 656564,
            shares = 23234,
            views = 3243434,
            authorAvatar = R.drawable.ic_launcher_foreground
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "\uD83D\uDE80 24 сентября стартует новый поток бесплатного курса «Диджитал-старт: первый шаг к востребованной профессии» — за две недели вы попробуете себя в разных профессиях и определите, что подходит именно вам → http://netolo.gy/fQ",
            published = "21 сентября в 10:12",
            likedByMe = false,
            likes = 11,
            shares = 3,
            views = 76,
            authorAvatar = R.drawable.ic_launcher_foreground
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Таймбоксинг — отличный способ навести порядок в своём календаре и разобраться с делами, которые долго откладывали на потом. Его главный принцип — на каждое дело заранее выделяется определённый отрезок времени. В это время вы работаете только над одной задачей, не переключаясь на другие. Собрали советы, которые помогут внедрить таймбоксинг \uD83D\uDC47\uD83C\uDFFB",
            published = "22 сентября в 10:12",
            likedByMe = false,
            likes = 9022,
            shares = 1472,
            views = 24504,
            authorAvatar = R.drawable.ic_launcher_foreground
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Делиться впечатлениями о любимых фильмах легко, а что если рассказать так, чтобы все заскучали \uD83D\uDE34\n",
            published = "22 сентября в 10:14",
            likedByMe = false,
            likes = 765,
            shares = 321,
            views = 3427,
            authorAvatar = R.drawable.ic_launcher_foreground
        ),
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Освоение новой профессии — это не только открывающиеся возможности и перспективы, но и настоящий вызов самому себе. Приходится выходить из зоны комфорта и перестраивать привычный образ жизни: менять распорядок дня, искать время для занятий, быть готовым к возможным неудачам в начале пути. В блоге рассказали, как избежать стресса на курсах профпереподготовки → http://netolo.gy/fPD",
            published = "23 сентября в 10:12",
            likedByMe = false,
            likes = 345,
            shares = 201,
            views = 1002,
            authorAvatar = R.drawable.ic_launcher_foreground
        )
    )
    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(likedByMe = !it.likedByMe)
        }
        posts = posts.map {
            if (it.id != id) it else if (it.likedByMe) {
                it.copy(likedByMe = it.likedByMe, likes = it.likes + 1)
            } else {
                it.copy(likedByMe = it.likedByMe, likes = it.likes - 1)
            }
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(shares = it.shares + 1)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id}
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts =  listOf(
                post.copy(
                    id = nextId++,
                    author = "Нетология. Университет интернет-профессий будущего",
                    likedByMe = false,
                    published = "Только что",
                    likes = 0,
                    shares = 0,
                    views = 0,
                    authorAvatar = R.drawable.ic_launcher_foreground
                )
            ) + posts
            data.value = posts
            return
        }
        posts = posts.map{
            if (it.id !=post.id) it  else it.copy(content = post.content)
        }
        data.value = posts
    }

}