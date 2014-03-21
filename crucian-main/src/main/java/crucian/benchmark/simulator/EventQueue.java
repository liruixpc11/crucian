package crucian.benchmark.simulator;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 事件队列
 */
public class EventQueue {
    private static class EventTimeComparator implements Comparator<Event> {
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getTime() - o2.getTime();
        }
    }

    public Event top() {
        return eventPriorityQueue.peek();
    }

    public Event dequeue() {
        return eventPriorityQueue.poll();
    }

    public void enqueue(Event event) {
        eventPriorityQueue.offer(event);
    }

    public boolean isEmpty() {
        return eventPriorityQueue.isEmpty();
    }

    private PriorityQueue<Event> eventPriorityQueue = new PriorityQueue<Event>(10, new EventTimeComparator());
}
