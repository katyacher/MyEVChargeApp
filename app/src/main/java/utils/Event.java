package utils;

/**
 * Класс-обертка для событий, которые должны быть обработаны только один раз.
 * Полезно для навигации, показов Snackbar и других одноразовых событий.
 */
public class Event<T> {
    private final T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Возвращает содержимое и помечает событие как обработанное.
     * Последующие вызовы вернут null.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        }
        hasBeenHandled = true;
        return content;
    }

    /**
     * Возвращает содержимое без пометки о обработке.
     */
    public T peekContent() {
        return content;
    }

    /**
     * Проверяет, было ли событие уже обработано.
     */
    public boolean isHandled() {
        return hasBeenHandled;
    }
}
