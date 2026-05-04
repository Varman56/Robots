package events;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class EventBus {
    private final Subject<Object> bus = PublishSubject.create().toSerialized();

    public void send(Object event) {
        bus.onNext(event);
    }

    public <T> Observable<T> listen(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}
