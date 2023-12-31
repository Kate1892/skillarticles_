package ru.skillbranch.skillarticles.viewmodels

import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import java.io.Serializable

abstract class BaseViewModel<T>(initState: T, private val savedStateHandle: SavedStateHandle) :
    ViewModel() where T : VMState {
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val notifications = MutableLiveData<Event<Notify>>()

    /***
     * Инициализация начального состояния аргументом конструктоа, и объявления состояния как
     * MediatorLiveData - медиатор исспользуется для того чтобы учитывать изменяемые данные модели
     * и обновлять состояние ViewModel исходя из полученных данных
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        val restoredState = savedStateHandle.get<Any>("state")?.let {
            if (it is Bundle) initState.fromBundle(it) as? T
            else it as T
        }
        Log.e("BaseViewModel", "handle restore state $restoredState")
        value = restoredState ?: initState
    }

    /***
     * getter для получения not null значения текущего состояния ViewModel
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val currentState
        get() = state.value!!


    /***
     * лямбда выражение принимает в качестве аргумента текущее состояние и возвращает
     * модифицированное состояние, которое присваивается текущему состоянию
     */
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }

    /***
     * функция для создания уведомления пользователя о событии (событие обрабатывается только один раз)
     * соответсвенно при изменении конфигурации и пересоздании Activity уведомление не будет вызвано
     * повторно
     */
    protected fun notify(content: Notify) {
        notifications.postValue(Event(content))
    }

    /***
     * более компактная форма записи observe() метода LiveData принимает последним аргумент лямбда
     * выражение обрабатывающее изменение текущего стостояния
     */
    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    /***
     * вспомогательная функция позволяющая наблюдать за изменениями части стейта ViewModel
     */
    fun <D> observeSubState(
        owner: LifecycleOwner,
        transform: (T) -> D,
        onChanged: (substate: D) -> Unit
    ) {
        state
            .map(transform) //трансыормируем весь стейт в необходимую модель substate
            .distinctUntilChanged() //отфильтровываем и пропускаем дальше только если значение измнилось
            .observe(owner, Observer { onChanged(it!!) })
    }

    /***
     * более компактная форма записи observe() метода LiveData вызывает лямбда выражение обработчик
     * только в том случае если уведомление не было уже обработанно ранее,
     * реализует данное поведение с помощью EventObserver
     */
    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver { onNotify(it) })
    }

    /***
     * функция принимает источник данных и лямбда выражение обрабатывающее поступающие данные источника
     * лямбда принимает новые данные и текущее состояние ViewModel в качестве аргументов,
     * изменяет его и возвращает модифицированное состояние, которое устанавливается как текущее
     */
    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S, currentState: T) -> T?
    ) {
        state.addSource(source) {
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }

    /***
     * сохранение стейта в bundle
     */
    fun saveSate() {
        Log.e("BaseViewModel", "save state $currentState")
        savedStateHandle.set("state", currentState)
    }

    /***
     * восстановление стейта из bundle после смерти процесса
     */
    /* fun restoreSate(){
         val restoredState = savedStateHandle.get<T>("state")
         Log.e("BaseViewModel", "restore state $restoredState")
         restoredState ?: return
         state.value = restoredState
     }*/

}

class ViewModelFactory(owner: SavedStateRegistryOwner, private val params: String) :
    AbstractSavedStateViewModelFactory(owner, bundleOf()) {
    // TODO
//    override fun <T : ViewModel?> create(
//        key: String,
//        modelClass: Class<T>,
//        handle: SavedStateHandle
//    ): T {
//        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
//            return ArticleViewModel(params, handle) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(params, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class Event<out E>(private val content: E) {
    var hasBeenHandled = false

    /***
     * возвращает контент который еще не был обработан иначе null
     */
    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): E = content
}

/***
 * в качестве аргумента конструктора принимает лямбда выражение обработчик в аргумент которой передается
 * необработанное ранее событие получаемое в реализации метода Observer`a onChanged
 */
class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {

    // TODO
//    override fun onChanged(event: Event<E>?) {
//        //если есть необработанное событие (контент) передай в качестве аргумента в лямбду
//        // onEventUnhandledContent
//        event?.getContentIfNotHandled()?.let {
//            onEventUnhandledContent(it)
//        }
//    }

    override fun onChanged(event: Event<E>) {
        event.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class Notify() {
    abstract val message: String

    data class TextMessage(override val message: String) : Notify()

    data class ActionMessage(
        override val message: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)
    ) : Notify()

    data class ErrorMessage(
        override val message: String,
        val errLabel: String?,
        val errHandler: (() -> Unit)?
    ) : Notify()
}

public interface VMState : Serializable {
    fun toBundle(): Bundle
    fun fromBundle(bundle: Bundle): VMState?
}